package route;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.LoginService;
import util.ParameterUtils;
import util.ResponseUtils;

public class LoginRouter {

    public Router router;
    private Vertx vertx;
    private LoginService loginService;
    public LoginRouter(Vertx vertx){
        this.vertx = vertx;
        router = Router.router(vertx);
        this.loginService = LoginService.getLoginService();
        this.init();
    }

    public void init(){
         router.post("/login").handler(this::login);
    }

    public void login(RoutingContext context){
        String phone = ParameterUtils.getStringParam(context,"phone");
        String password = ParameterUtils.getStringParam(context,"password");

        loginService.login(phone,password,res->{
            if(res.failed()){
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"token",res.result());
            }
        });

    }
}
