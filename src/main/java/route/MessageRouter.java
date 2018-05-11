package route;

import bean.Auth;
import io.netty.util.internal.StringUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.MessageService;
import util.ParameterUtils;
import util.ResponseUtils;

import java.util.Iterator;
import java.util.Set;

public class MessageRouter {
    public Router router;
    private Vertx vertx;
    private MessageService messageService = MessageService.getMessageService();

    public MessageRouter(Vertx vertx){
        this.vertx = vertx;
        router = Router.router(vertx);
        this.init();
    }

    public void init(){
         //添加好友
         router.post("/read").handler(this::updateRead);
         //获取未读消息
        router.get("/getMessages").handler(this::getMessages);
        //获取新好友消息
        router.get("/friendMsg").handler(this::getAddFriendMessages);
        //删除聊天记录
        router.get("/delMessage").handler(this::delMessage);
        //上传文件
        router.post("/upload").handler(this::uploadFile);
        //下载文件
        router.post("/downFile").handler(this::downFile);
    }

    public void updateRead(RoutingContext context){
        int mid = ParameterUtils.getIntegerParam(context,"mid");
        messageService.updateRead(mid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
    }

    public void getMessages(RoutingContext context){
        int fid = ParameterUtils.getIntegerParam(context,"fid");
        int page = ParameterUtils.getIntegerParam(context,"page");
        int uid = ((Auth) context.get("auth")).getUid();
        messageService.getMessages(uid,fid,page,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"messages",res.result());
            }
        });
    }

     public void getAddFriendMessages(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        messageService.getAddFriendMessages(uid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context,"addFriendMsg",res.result());
            }
        });
     }

     public void delMessage(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
        int fid = ParameterUtils.getIntegerParam(context,"fid");
        messageService.delMessage(uid,fid,res->{
            if(res.failed()) {
                context.fail(res.cause());
            } else {
                ResponseUtils.responseSuccess(context);
            }
        });
     }

     public void uploadFile(RoutingContext context){
        int uid = ((Auth) context.get("auth")).getUid();
//        int fid = ParameterUtils.getIntegerParam(context,"fid");

        Set<FileUpload> uploads = context.fileUploads();
        Iterator <FileUpload>it1 = uploads.iterator();
        if(it1.hasNext()) {
            FileUpload fileUpload=  it1.next();
            System.out.println(fileUpload.fileName());
            System.out.println(fileUpload.name());
            System.out.println(fileUpload.uploadedFileName());
            System.out.println(fileUpload.size());
            System.out.println(fileUpload.charSet());
        }
     }

     public void downFile(RoutingContext context){
          int uid = ((Auth) context.get("auth")).getUid();
          context.response().sendFile("C:\\+work\\dkIM\\file-uploads\\你好.txt");
     }

}
