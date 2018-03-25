package route;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.FriendsService;
import util.ParameterUtils;
import util.ResponseUtils;

public class FriendRouter {

     public Router router;
    private Vertx vertx;
    private FriendsService friendsService = FriendsService.getFriendsService();

    public FriendRouter(Vertx vertx){
        this.vertx = vertx;
        router = Router.router(vertx);
        this.init();
    }

    public void init(){
         //添加好友
         router.post("/").handler(this::addFriend);
         router.get("/").handler(this::getFriend);
    }

    public void addFriend(RoutingContext context) {
        String condition = ParameterUtils.getStringParam(context,"condition");

        friendsService.addFriend(condition,res->{

        });

    }

    public void getFriend(RoutingContext context) {
        String condition = ParameterUtils.getStringParam(context,"condition");

        friendsService.getFriend(condition,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"friends",res.result());
            }

        });

    }
}
