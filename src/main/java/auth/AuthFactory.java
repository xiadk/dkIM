package auth;

import bean.Auth;
import connector.RedisOperator;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import util.ResponseUtils;

public class AuthFactory {
    public static void getAuth(HttpServerRequest request, Handler<AsyncResult<Auth>> done) {
        String token = request.getHeader("token");
        RedisOperator.get(token, res -> {
            if (res.failed()) {
                done.handle(Future.failedFuture(res.cause()));
            } else if (res.result() != null) {
                String uid = res.result();
                JsonObject authInfo = new JsonObject();
                authInfo.put("uid", Integer.parseInt(uid));
                Auth auth = authInfo.mapTo(Auth.class);
                if (auth == null) {
                    done.handle(Future.failedFuture(new AppException(ResponseUtils.AUTH_ERROR)));
                } else {
                    auth.setToken(token);
                    done.handle(Future.succeededFuture(auth));
                }
            } else {
                done.handle(Future.failedFuture(new AppException(ResponseUtils.AUTH_ERROR)));
            }
        });
    }
}
