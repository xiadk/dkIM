package auth;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;


public interface AuthHandler extends Handler<RoutingContext> {

    static AuthHandlerImp create() {
        return new AuthHandlerImp();
    }
}
