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
         router.get("/del").handler(this::delFriends);
         router.post("/alias").handler(this::updateAlias);
        router.post("/delcontact").handler(this::delContact);
        router.post("/addcontact").handler(this::addContact);
        router.get("/getcontacts").handler(this::getContacts);
        router.get("/alias").handler(this::getAliasByFid);
        router.get("/search").handler(this::search);
    }

    public void addFriend(RoutingContext context) {
        int fid = ((Auth) context.get("auth")).getUid();
        int uid = ParameterUtils.getIntegerParam(context,"uid");
        String fidName = ParameterUtils.getStringParam(context,"fidname");
        String uidName = ParameterUtils.getStringParam(context,"uidname");

        friendsService.addFriend(uid,fid,fidName,uidName,res->{

            if(res.failed()){
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
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


    //删除指定好友
    public void delFriends(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        int fid = ParameterUtils.getIntegerParam(context,"fid");
        friendsService.delFriend(uid,fid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void updateAlias(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        int fid = ParameterUtils.getIntegerParam(context,"fid");
        String alias = ParameterUtils.getStringParam(context,"alias");
        friendsService.updateAlias(alias,uid,fid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void addContact(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        int fid = ParameterUtils.getIntegerParam(context,"fid");
        friendsService.addContact(fid,uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void delContact(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        int fid = ParameterUtils.getIntegerParam(context,"fid");
        friendsService.delContact(uid,fid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void getContacts(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();

        friendsService.getContacts(uid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "contacts", res.result());
            }

        });
    }

    public void search(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        String name = context.request().getParam("name");
        friendsService.getContactsByName(uid,name, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "friends", res.result());
            }

        });
    }

    public void getAliasByFid(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        int fid = ParameterUtils.getIntegerParam(context,"fid");

        friendsService.getAliasByFid(uid,fid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "alias", res.result());
            }

        });
    }

}
