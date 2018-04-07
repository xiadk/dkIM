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

import java.util.List;
import java.util.Map;


public class MessageHandler implements Handler<WebSocketFrame> {
    private PopMessage popMessage = PopMessage.getPopMessage();
    private PushMessage pushMessage = PushMessage.getPushMessage();
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
        System.out.println(message);
        String messageKey = "message:";
        String token = message.getToken();

        RedisOperator.get(token, uidRes -> {
            if (uidRes.failed()) {
                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
            } else if (uidRes.result() == null) {
                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.REQUEST_NOT_EXIST, "用户未登陆").getMessage());
            } else {
                String uid = uidRes.result();
                ServerWebSocket uidSocket = serverWebSocketMap.get(uid);
                if (uidSocket == null) {
                    serverWebSocketMap.put(uid, serverWebSocket);
                }
                //第一次连接fid默认-1
                if (message.getFid() <= 0) {
                    //拉取缓存信息
                    popMessage.pop(Integer.parseInt(uid), messageRes -> {
                        if (messageRes.failed()) {
                            serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                        } else {
                            List<JsonObject> list = messageRes.result();
                            JsonObject respMessage = new JsonObject();
                            respMessage.put("msgId", "0200").put("body", list);
                            serverWebSocket.writeFinalTextFrame(respMessage.encode());
                        }
                    });
                    /*RedisOperator.lpopall(messageKey + uid, messageRes -> {
                        if (messageRes.failed()) {
                            serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                        } else {
                            JsonArray jsonArray = messageRes.result();
                            JsonObject respMessage = new JsonObject();
                            respMessage.put("msgId", "0200").put("body", jsonArray);
                            serverWebSocket.writeFinalTextFrame(respMessage.encode());
                        }
                    });*/
                } else {
                    //给目标客户端发送消息
                    String fid = String.valueOf(message.getFid());
                    ServerWebSocket fidSocket = serverWebSocketMap.get(fid);
                    pushMessage.push(message, Integer.parseInt(uid), messageRes -> {
                        if (messageRes.failed()) {
                            serverWebSocket.writeFinalTextFrame(messageRes.cause().getMessage());
                        } else if (fidSocket != null) {
                            //目标用户在线
                            formReturnMsg(message, Integer.parseInt(uid), backMsgRes -> {
                                if (backMsgRes.failed()) {
                                    serverWebSocket.writeFinalTextFrame(backMsgRes.cause().getMessage());
                                } else {
                                    JsonArray jsonArray = new JsonArray();
                                    jsonArray.add(backMsgRes.result().put("mid",messageRes.result()));
                                    JsonObject respFidMessage = new JsonObject();
                                    respFidMessage.put("msgId", "0200").put("body", jsonArray);
                                    fidSocket.writeFinalTextFrame(respFidMessage.encode());

                                    JsonObject respMessage = new JsonObject();
                                    respMessage.put("msgId", "0200").put("body", "发送成功");
                                    serverWebSocket.writeFinalTextFrame(respMessage.encode());
                                }
                            });
                        } else {
                            JsonObject respMessage = new JsonObject();
                            respMessage.put("msgId", "0200").put("body", "发送成功");
                            serverWebSocket.writeFinalTextFrame(respMessage.encode());
                        }
                    });
                    /*if (fidSocket == null) {
                        //目标用户不在线，将信息存入缓存中
                        formReturnMsg(message, Integer.parseInt(uid), backMsgRes -> {
                            if (backMsgRes.failed()) {
                                serverWebSocket.writeFinalTextFrame(backMsgRes.cause().getMessage());
                            } else {
                                RedisOperator.lpush(messageKey + fid, backMsgRes.result(), lpushRes -> {
                                    if (lpushRes.failed()) {
                                        serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                                    } else {
                                        JsonObject respMessage = new JsonObject();
                                        respMessage.put("msgId", "0200").put("body", "发送成功");
                                        serverWebSocket.writeFinalTextFrame(respMessage.encode());
                                    }

                                });
                            }
                        });
                    } else {
                        //目标用户在线
                        formReturnMsg(message, Integer.parseInt(uid), backMsgRes -> {
                            if (backMsgRes.failed()) {
                                serverWebSocket.writeFinalTextFrame(backMsgRes.cause().getMessage());
                            } else {
                                JsonArray jsonArray = new JsonArray();
                                jsonArray.add(backMsgRes.result());
                                JsonObject respFidMessage = new JsonObject();
                                respFidMessage.put("msgId", "0200").put("body", jsonArray);
                                fidSocket.writeFinalTextFrame(respFidMessage.encode());
                                JsonObject respMessage = new JsonObject();
                                respMessage.put("msgId", "0200").put("body", "发送成功");
                                serverWebSocket.writeFinalTextFrame(respMessage.encode());
                            }
                        });
                    }*/
                }
                //连接关闭
                serverWebSocket.closeHandler(res -> {
                    System.out.println("退出:" + uid);
                    serverWebSocketMap.remove(uid);
                });

            }
        });


    }

    public static MessageHandler create(ServerWebSocket serverWebSocket, Map<String, ServerWebSocket> serverWebSocketMap) {
        return new MessageHandler(serverWebSocket, serverWebSocketMap);
    }

    //格式化返回信息
    public void formReturnMsg(Message message, int uid, Handler<AsyncResult<JsonObject>> handler) {

        switch (message.getType()) {
            case TEXT:

                break;
            case PHOTO:

                break;
            case ADD_FRIEND:

                try {
                    JsonObject message1 = JsonObject.mapFrom(message);
                    message1.put("ope", message.getOpe().val).put("type", message.getType().val);
                    popMessage.sendAddFriend(message1,uid, handler);
                } catch (Exception e){
                    System.out.println(e);
                }
                break;
            default:
                break;
        }
    }
}
