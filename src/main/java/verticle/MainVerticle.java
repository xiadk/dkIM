package verticle;

import auth.AuthHandler;
import connector.PGConnector;
import connector.RedisConnector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import route.UserRouter;
import util.ConfigUtils;
import route.LoginRouter;
import util.CorsUtils;
import util.ResponseUtils;
import websocket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class MainVerticle extends AbstractVerticle{
    @Override
    public void start() throws Exception {
        super.start();
        //初始化数据库
        ConfigUtils.initConfig("C:\\+work\\dkIM\\src\\main\\resources\\dev.json");
        PGConnector.init(vertx);
        RedisConnector.init(vertx);

        //开启服务器
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*").allowedHeader(CorsUtils.getCorsHeader())
                .allowedMethods(CorsUtils.getCorsMethods()));

        //加载静态文件
        router.route("/*").handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot("D:/dkIMWeb/webroot").setIndexPage("login.html"));
        router.route().handler(AuthHandler.create());
        //异常捕捉
        router.route().failureHandler(routingContext->{
            String resultMsg = routingContext.failure().getMessage();
            System.out.println(resultMsg);
            routingContext.response().end(resultMsg);

        });


        router.mountSubRouter("/", new LoginRouter(vertx).router);

        router.mountSubRouter("/user", new UserRouter(vertx).router);

        httpServer.websocketHandler(WebSocketHandler.create());
        httpServer.requestHandler(router::accept).listen(8001);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
