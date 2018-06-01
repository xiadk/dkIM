package route;

import bean.Auth;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.GroupService;
import util.ParameterUtils;
import util.ResponseUtils;

public class GroupRouter {
    public Router router;
    private Vertx vertx;
    private GroupService groupService;

    public GroupRouter(Vertx vertx) {
        this.vertx = vertx;
        router = Router.router(vertx);
        this.groupService = GroupService.getGroupService();
        this.init();
    }

    public void init() {
        router.post("/create").handler(this::createGroup);
        router.get("/members").handler(this::selectMembers);
        router.post("/delmembers").handler(this::delMembers);
        router.get("/memberInfo").handler(this::getMemberInfo);
        router.post("/addMembers").handler(this::addGroup_members);
        router.post("/getfriendsToGroup").handler(this::getfriendsToGroup);
        router.post("/exitGroup").handler(this::exitGroup);
        router.post("/updateGroupName").handler(this::updateGroupName);
        router.get("/").handler(this::getGroups);
    }

    public void createGroup(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        JsonArray members = new JsonArray(context.request().getParam("members"));
        groupService.createGroup(uid, members, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, res.result());
            }
        });
    }

    public void selectMembers(RoutingContext context) {
        int gid = ParameterUtils.getIntegerParam(context, "gid");
        groupService.selectMembers(gid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "members", res.result());
            }
        });
    }

    public void delMembers(RoutingContext context) {
        JsonArray members = new JsonArray(context.request().getParam("members"));
        int gid = ParameterUtils.getIntegerParam(context, "gid");
        groupService.delMembers(gid, members, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "members", res.result());
            }
        });
    }

    public void getMemberInfo(RoutingContext context) {
        int fid = ParameterUtils.getIntegerParam(context, "fid");
        int uid = ((Auth) context.get("auth")).getUid();
        groupService.findFriendByfid(uid, fid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, res.result());
            }
        });
    }

    public void addGroup_members(RoutingContext context) {
        int gid = ParameterUtils.getIntegerParam(context, "gid");
        String gname = ParameterUtils.getStringParam(context,"gname");
        JsonArray members = new JsonArray(context.request().getParam("members"));
        groupService.addGroup_members(gid,gname, members, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void getfriendsToGroup(RoutingContext context) {
        int gid = ParameterUtils.getIntegerParam(context, "gid");
        int uid = ((Auth) context.get("auth")).getUid();
        groupService.getfriendsToGroup(gid, uid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "members", res.result());
            }
        });
    }

    public void exitGroup(RoutingContext context) {
        int gid = ParameterUtils.getIntegerParam(context, "gid");
        int uid = ((Auth) context.get("auth")).getUid();
        groupService.exitGroup(gid, uid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void updateGroupName(RoutingContext context) {
        int gid = ParameterUtils.getIntegerParam(context, "gid");
        int uid = ((Auth) context.get("auth")).getUid();
        String gname = ParameterUtils.getStringParam(context, "gname");
        groupService.updateGroupName(gid, uid, gname, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void getGroups(RoutingContext context) {
        int uid = ((Auth) context.get("auth")).getUid();
        groupService.selectGroupByUid(uid, res -> {
            if (res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context, "groups",res.result());
            }
        });
    }

}
