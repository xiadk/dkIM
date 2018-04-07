package route;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import service.MessageService;
import util.ParameterUtils;
import util.ResponseUtils;

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
}
