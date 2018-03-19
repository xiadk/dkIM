package websocket;

import connector.RedisOperator;
import exception.AppException;
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
        JsonObject message = new JsonObject(webSocketFrame.textData());
        String clientIDKey = "clientID:";
        String messageKey = "message:";
        int uid = message.getInteger("uid");
        RedisOperator.get(clientIDKey + String.valueOf(uid), uidRes -> {
            if (uidRes.failed()) {
                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
            } else if (uidRes.result() == null) {
                //注册客户端
                RedisOperator.set(clientIDKey + String.valueOf(uid), serverWebSocket.binaryHandlerID(), saveIDRes -> {
                    if (saveIDRes.failed()) {
                        serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                    }
                });
                //拉取信息
                RedisOperator.lpopall(messageKey + String.valueOf(uid), messageRes -> {
                    if (messageRes.failed()) {
                        serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                    } else {
                        JsonArray jsonArray = messageRes.result();
                        JsonObject respMessage = new JsonObject();
                        respMessage.put("msgId", 0200).put("body", jsonArray);
                        serverWebSocket.writeFinalTextFrame(respMessage.encode());
                    }
                });
            }
        });
        //给目标客户端发送消息
        int fid = message.getInteger("fid");
        RedisOperator.get(clientIDKey + String.valueOf(fid), fidRes -> {
            if (fidRes.failed()) {
                serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
            } else if (fidRes.result() == null) {
                RedisOperator.lpush(messageKey + String.valueOf(fid), webSocketFrame.textData(), lpushRes -> {
                    if (lpushRes.failed()) {
                        serverWebSocket.writeFinalTextFrame(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误").getMessage());
                    } else {
                        JsonObject respMessage = new JsonObject();
                        respMessage.put("msgId", 0200).put("body", "发送成功");
                        serverWebSocket.writeFinalTextFrame(respMessage.encode());
                    }

                });
            } else {
                String clientID = fidRes.result();
                ServerWebSocket fidSocket = serverWebSocketMap.get(clientID);
                fidSocket.writeFinalTextFrame(message.getString("body"));
                JsonObject respMessage = new JsonObject();
                respMessage.put("msgId", 0200).put("body", "发送成功");
                serverWebSocket.writeFinalTextFrame(respMessage.encode());
            }
        });


    }

    public static MessageHandler create(ServerWebSocket serverWebSocket, Map<String, ServerWebSocket> serverWebSocketMap) {
        return new MessageHandler(serverWebSocket, serverWebSocketMap);
    }
}
