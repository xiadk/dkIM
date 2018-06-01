package route;

import bean.Auth;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.FileService;
import util.ParameterUtils;
import util.QiniuUtil;
import util.ResponseUtils;

public class FileRouter {

     public Router router;
    private Vertx vertx;
    private FileService filesService = FileService.getFileService();

    public FileRouter(Vertx vertx){
        this.vertx = vertx;
        router = Router.router(vertx);
        this.init();
    }

    public void init(){

         router.get("/uploadToken").handler(this::UploadToken);
    }

    public void UploadToken(RoutingContext context) {
        ResponseUtils.responseSuccess(context,"uploadToken", QiniuUtil.getUpToken());
    }



}
