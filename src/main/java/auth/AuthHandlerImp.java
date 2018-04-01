package auth;

import connector.RedisOperator;
import exception.AppException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.BasicUtils;
import util.ResponseUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthHandlerImp implements AuthHandler {

    private static Logger request_logger = LoggerFactory.getLogger("MITURES_REQUEST");
    private static Logger response_logger = LoggerFactory.getLogger("MITURES_RESPONSE");
    static JsonArray jsonArray = new JsonArray();

    //不需要token验证
    static {
        jsonArray.add("/login");
        jsonArray.add("/user/register");
        jsonArray.add(".*/css/.*");
        jsonArray.add(".*/js/.*");
        jsonArray.add(".*/audio/.*");
    }

    public boolean filter_path(String path) {
        for (int i = 0, il = jsonArray.size(); i < il; i++) {
            if (path.matches(jsonArray.getString(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        String token = request.headers().get("token");
        String url = request.path();
        String host = request.remoteAddress().toString();
        if (filter_path(url)) {
            context.next();
        } else if (StringUtils.isBlank(token)) {
            logRequestResponse(context, "0", token);
            context.fail(new AppException(ResponseUtils.AUTH_ERROR));
        } else {
            logRequestResponse(context, "0", token);
            AuthFactory.getAuth(request, auth -> {
                if (auth.failed()) {
                    context.fail(new AppException(ResponseUtils.AUTH_ERROR));
                } else {
                    RedisOperator.setExpires(null, token,3600, res -> {
                        context.put("auth", auth.result()).next();
                    });
                }
            });
        }
    }

    private static void logRequestResponse(RoutingContext routingContext, String uid, String token) {

        HttpServerRequest request = routingContext.request();
        String remoteAddress = request.remoteAddress().toString();
        String method = request.method().toString();
        String uri = request.uri();
        String requestParams = request.params().toString().replace("\n", "\t");
        Long currentTimeMillis = System.currentTimeMillis();
        System.out.println("IN---url:"+uri+";method:"+method+";params:"+requestParams+";time:"+currentTimeMillis);
        request_logger.info("REQUEST(uid:{} token:{} ip:{}): {} {} time:{} params:{}", uid, token, remoteAddress, method, uri, currentTimeMillis, requestParams);
    }

}
