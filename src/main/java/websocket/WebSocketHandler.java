package websocket;

import connector.RedisOperator;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;


public class WebSocketHandler implements Handler<ServerWebSocket> {

    Map<String, ServerWebSocket> serverWebSocketMap = new HashMap<>();

    @Override
    public void handle(ServerWebSocket serverWebSocket) {
        /*String clientID = serverWebSocket.binaryHandlerID();
        serverWebSocketMap.put(clientID, serverWebSocket);*/
        serverWebSocket.frameHandler(MessageHandler.create(serverWebSocket,serverWebSocketMap));
    }

    public static WebSocketHandler create() {
        return new WebSocketHandler();
    }
}
