package verticle;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.HashMap;
import java.util.Map;

public class Run {
    public static Vertx vertx = Vertx.vertx();
    private static Logger logger = LoggerFactory.getLogger(Run.class);
    public static void main(String[] args) {
        vertx.deployVerticle(MainVerticle.class.getName(),res->{
            if(res.succeeded()){
                logger.debug("启动成功");
            }else{
                logger.debug("启动失败");
            }
        });
    }
}
