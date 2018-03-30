package websocket;

import bean.Message;
import connector.RedisOperator;
import dao.UserDao;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.ResponseUtils;

import java.util.Map;


public class MessageHandler implements Handler<WebSocketFrame> {
    private ServerWebSocket serverWebSocket;
    private Map<String, ServerWebSocket> serverWebSocketMap;

    public MessageHandler(ServerWebSocket serverWebSocket, Map<String, ServerWebSocket> serverWebSocketMap) {
        this.serverWebSocket = serverWebSocket;
        this.serverWebSocketMap = serverWebSocketMap;
    }

    @Override
    public void handle(WebSocketFrame webSocketFrame) {
        Message message = new JsonObject(webSocketFrame.textData()).mapTo(Message.class);
        //输出打印日志
        System.out.println("clientID:" + serverWebSocket.binaryHandlerID() + "websocket消息：" + message);

        String clientIDKey = "clientID:";
        String messageKey = "message:";
        String token = message.getToken();

        RedisOperator.get(token, uidRes -> {
            if (uidRes.failed()) {
                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
            } else if (uidRes.result() == null) {
                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.REQUEST_NOT_EXIST, "用户未登陆").getMessage());
            } else {
                String uid = uidRes.result();
                RedisOperator.get(clientIDKey + uid, binaryHandlerIDRes -> {
                    if (binaryHandlerIDRes.failed()) {
                        serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                    } else if (binaryHandlerIDRes.result() == null) {
                        //注册客户端
                        RedisOperator.set(clientIDKey + uid, serverWebSocket.binaryHandlerID(), saveIDRes -> {
                            if (saveIDRes.failed()) {
                                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                            }
                        });
                        //拉取信息
                        RedisOperator.lpopall(messageKey + uid, messageRes -> {
                            if (messageRes.failed()) {
                                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                            } else {
                                JsonArray jsonArray = messageRes.result();
                                JsonObject respMessage = new JsonObject();
                                respMessage.put("msgId", 0200).put("body", jsonArray);
                                serverWebSocket.writeFinalTextFrame(respMessage.encode());
                            }
                        });
                    } else {

                        //给目标客户端发送消息
                        int fid = message.getFid();
                        RedisOperator.get(clientIDKey + String.valueOf(fid), fidRes -> {
                            if (fidRes.failed()) {
                                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                            } else if (fidRes.result() == null) {
                                //目标用户不在线，将信息存入缓存中
                                RedisOperator.lpush(messageKey + String.valueOf(fid), webSocketFrame.textData(), lpushRes -> {
                                    if (lpushRes.failed()) {
                                        serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                                    } else {
                                        JsonObject respMessage = new JsonObject();
                                        respMessage.put("msgId", "0200").put("body", "发送成功");
                                        serverWebSocket.writeFinalTextFrame(respMessage.encode());
                                    }

                                });
                            } else {
                                //目标用户在线
                                String clientID = fidRes.result();
                                ServerWebSocket fidSocket = serverWebSocketMap.get(clientID);
                                String backMsg = formReturnMsg(message, Integer.parseInt(uid), backMsgRes -> {
                                    if (backMsgRes.failed()) {
                                        serverWebSocket.writeFinalTextFrame(backMsgRes.cause().getMessage());
                                    } else {
                                        fidSocket.writeFinalTextFrame(backMsgRes.result());
                                        JsonObject respMessage = new JsonObject();
                                        respMessage.put("msgId", "0200").put("body", "发送成功");
                                        serverWebSocket.writeFinalTextFrame(respMessage.encode());
                                    }
                                });


                            }
                        });

                    }


                });
            }
        });


    }

    public static MessageHandler create(ServerWebSocket serverWebSocket, Map<String, ServerWebSocket> serverWebSocketMap) {
        return new MessageHandler(serverWebSocket, serverWebSocketMap);
    }

    //格式化返回信息
    public String formReturnMsg(Message message, int uid, Handler<AsyncResult<String>> handler) {

        switch (message.getType()) {
            case TEXT:

                break;
            case PHOTO:

                break;
            case ADD_FRIEND:

                sendAddFriend(message, uid, handler);
                break;
            default:
                break;
        }
        return "ss";
    }


    //添加好友请求发送给目标用户
    public void sendAddFriend(Message message, int uid, Handler<AsyncResult<String>> handler) {

        UserDao.getUserDao().getUserById(uid, userRes -> {
            if (userRes.failed()) {
                handler.handle(Future.failedFuture(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误")));
            } else {
                JsonObject returnMsg = userRes.result();
                returnMsg.put("ope", message.getOpe());
                returnMsg.put("type", message.getType());
                handler.handle(Future.succeededFuture(returnMsg.toString()));
            }

        });
    }
}
