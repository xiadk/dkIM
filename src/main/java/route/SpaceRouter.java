package route;

import bean.Auth;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.MessageService;
import service.SpaceService;
import util.ParameterUtils;
import util.ResponseUtils;

public class SpaceRouter {
    public Router router;
    private Vertx vertx;
    private SpaceService spaceService = SpaceService.getGroupService();

    public SpaceRouter(Vertx vertx){
        this.vertx = vertx;
        router = Router.router(vertx);
        this.init();
    }

    public void init(){

        //添加朋友圈
         router.post("/add").handler(this::addSpace);
         //点赞
         router.post("/updatelove").handler(this::updatelove);
         //取消点赞
         router.post("/dellove").handler(this::dellove);
         //添加评论
         router.post("/insertComment").handler(this::insertComment);
         //查看评论
         router.get("/Comment").handler(this::getComment);
         //查看朋友圈
         router.post("/getSpace").handler(this::getSpace);
         //查看我的消息
        router.get("/messages").handler(this::getMyMessages);
        //查看某个朋友圈
         router.post("/detail").handler(this::getDetail);
         //删除朋友圈
         router.post("/delSpace").handler(this::delSpace);


    }

    public void addSpace(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        int type = ParameterUtils.getIntegerParam(context,"type");
        String  content = ParameterUtils.getStringParam(context,"content");
        spaceService.addSpace(uid,type,content,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });

     }

     public void updatelove(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        int sid = ParameterUtils.getIntegerParam(context,"sid");

        spaceService.updatelove(sid,uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });

     }

     public void dellove(RoutingContext context){
         int uid = ((Auth) context.get("auth")).getUid();
        int sid = ParameterUtils.getIntegerParam(context,"sid");

        spaceService.dellove(sid,uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });

     }

     public void insertComment(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        int sid = ParameterUtils.getIntegerParam(context,"sid");
        String  comment = ParameterUtils.getStringParam(context,"comment");
        spaceService.insertComment(uid,sid,comment,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });

     }
     public void getComment(RoutingContext context){
        int sid = ParameterUtils.getIntegerParam(context,"sid");

        spaceService.getComment(sid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"comments",res.result());
            }
        });

     }

     public void getSpace(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();

        spaceService.getSpace(uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"spaces",res.result());
            }
        });
     }

     public void getDetail(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        int sid = ParameterUtils.getIntegerParam(context,"sid");

        spaceService.getDetail(sid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"spaces",res.result());
            }
        });
     }

     public void getMyMessages(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();

        spaceService.getMyMessages(uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"messages",res.result());
            }
        });
     }

     public void delSpace(RoutingContext context){
        int sid = ParameterUtils.getIntegerParam(context,"sid");

        spaceService.delSpace(sid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
     }


}
