package route;

import bean.Auth;
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
         router.get("/findFriends").handler(this::findFriends);
    }

    public void addFriend(RoutingContext context) {
        int uid = ParameterUtils.getIntegerParam(context,"uid");

        friendsService.addFriend(uid,res->{

        });

    }

    //查找要添加的好友
    public void getFriend(RoutingContext context) {
        String condition = ParameterUtils.getStringParam(context, "condition");

        friendsService.getFriend(condition, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "friends", res.result());
            }

        });
    }

    //查找要添加的好友
    public void findFriends(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        friendsService.findFriends(uid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "friends", res.result());
            }

        });
    }

}
