package route;

import bean.Auth;
import bean.User;
import dao.UserDao;
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
    private static UserDao userDao = UserDao.getUserDao();
    public UserRouter(Vertx vertx){
        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.userService = UserService.getUserService();
        this.init();
    }

    public void init(){
        router.post("/register").handler(this::register);
        router.get("/").handler(this::getUser);
        router.get("/userInfo").handler(this::getUserInfo);
        router.post("/updateInfo").handler(this::updateInfo);
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

    public void getUser(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        userService.getUserInfo(uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,res.result());
            }
        });
    }

    public void getUserInfo(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        userService.getUserInfoById(uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,res.result());
            }
        });
    }

    public void updateInfo(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        String name = ParameterUtils.getStringParam(context,"name");
        String address = ParameterUtils.getStringParam(context,"address");
        String sex = ParameterUtils.getStringParam(context,"sex");
        String photo = ParameterUtils.getStringParam(context,"photo");

        User user = new User();
        user.setName(name);
        user.setAddress(address);
        user.setPhone(sex);
        user.setPhoto(photo);
        user.setUid(uid);
        userService.updateUser(user,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }
}
