package route;

import bean.User;
import exception.AppException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.UserService;
import util.ParameterUtils;
import util.ResponseUtils;
import util.ValDataUtils;

public class UserRouter {

    public Router router;
    private Vertx vertx;
    private UserService userService;
    public UserRouter(Vertx vertx){
        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.userService = UserService.getUserService();
        this.init();
    }

    public void init(){
        router.post("/register").handler(this::register);
    }

    public void register(RoutingContext context){
        HttpServerResponse response = context.response();
        String name = ParameterUtils.getStringParam(context,"name");
        String password = ParameterUtils.getStringParam(context,"password");
        String phone = ParameterUtils.getStringParam(context,"phone");

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(phone);

        if(!ValDataUtils.isPhone(phone)){
            context.fail(new AppException(ResponseUtils.PARAM_ERROR,"电话号码格式错误"));
        }else {
            userService.register(user,res->{
                if(res.succeeded()){
                    ResponseUtils.responseSuccess(context);
                }else{
                    context.fail(res.cause());
                }
            });
        }
    }
}
