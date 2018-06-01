package websocket;

import bean.Message;
import bean.Ope;
import connector.RedisOperator;
import dao.GroupDao;
import exception.AppException;
import io.vertx.core.AsyncResult;
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
    private GroupDao groupDao = GroupDao.getGroupDao();

    public MessageHandler(ServerWebSocket serverWebSocket, Map<String, ServerWebSocket> serverWebSocketMap) {
        this.serverWebSocket = serverWebSocket;
        this.serverWebSocketMap = serverWebSocketMap;
    }

    @Override
    public void handle(WebSocketFrame webSocketFrame) {
        Message message = new JsonObject(webSocketFrame.textData()).mapTo(Message.class);
        //输出打印日志
//        System.out.println(message);
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
                //给目标客户端发送消息
                if (message.getFid() > 0) {
                    if (message.getOpe() == Ope.PERSONAL) {
                        //发送个人消息
                        String fid = String.valueOf(message.getFid());
                        ServerWebSocket fidSocket = serverWebSocketMap.get(fid);
                        sendMessage(message, uid, uid, fidSocket);
                    } else if (message.getOpe() == Ope.GROUP) {
                        //发送群消息
                        //更新用户自己的消息记录
                        pushMessage.messageInit(message, Integer.parseInt(uid), Integer.parseInt(uid), res -> {
                            if (res.succeeded()) {
                                groupDao.selectMembersUid(message.getFid(), membersRes -> {
                                    List<JsonObject> members = membersRes.result();
                                    int gid = message.getFid();
                                    for (int i = 0, j = members.size(); i < j; i++) {
                                        String fid = String.valueOf(members.get(i).getInteger("uid"));
                                        //排除掉自己
                                        if (!fid.equals(uid)) {
                                            ServerWebSocket fidSocket = serverWebSocketMap.get(fid);
                                            //更换位置，群发给其他成功
                                            message.setFid(Integer.parseInt(fid));
                                            sendMessage(message, gid + "", uid, fidSocket);
                                        }
                                    }
                                });
                            } else {
//                                System.out.println("发消息消息失败" + res.cause());
                            }

                        });
                    }

                }
                //连接关闭
                serverWebSocket.closeHandler(res -> {
//                    System.out.println("退出:" + uid);
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
            case PHOTO:
            case TEXT:
            case GROUP_HINT:
            case ADD_FRIEND:
            case FILE:

                try {

                    JsonObject message1 = JsonObject.mapFrom(message);
                    message1.put("ope", message.getOpe().val).put("type", message.getType().val);
                    popMessage.sendAddFriend(message1, uid, handler);

                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            default:
                break;
        }
    }

    public void sendMessage(Message message, String poryId, String uid, ServerWebSocket fidSocket) {
        pushMessage.push(message, Integer.parseInt(poryId),Integer.parseInt(uid), messageRes -> {
            if (messageRes.failed()) {
                serverWebSocket.writeFinalTextFrame(messageRes.cause().getMessage());
            } else if (fidSocket != null) {
                //目标用户在线
                formReturnMsg(message, Integer.parseInt(uid), backMsgRes -> {
                    if (backMsgRes.failed()) {
                        serverWebSocket.writeFinalTextFrame(backMsgRes.cause().getMessage());
                    } else {
                        JsonObject back = backMsgRes.result();
                        if(message.getOpe()==Ope.GROUP) {
                            back.put("uid",poryId);
                        }
                        JsonArray jsonArray = new JsonArray();
                        jsonArray.add(back.put("mid", messageRes.result()));
                        JsonObject respFidMessage = new JsonObject();
                        respFidMessage.put("msgId", "0200").put("body", jsonArray);
                        fidSocket.writeFinalTextFrame(respFidMessage.encode());

                        JsonObject respMessage = new JsonObject();
                        respMessage.put("msgId", "0201").put("body", "发送成功");
                        serverWebSocket.writeFinalTextFrame(respMessage.encode());
                    }
                });
            } else {
                JsonObject respMessage = new JsonObject();
                respMessage.put("msgId", "0201").put("body", "发送成功");
                serverWebSocket.writeFinalTextFrame(respMessage.encode());
            }
        });
    }
}
