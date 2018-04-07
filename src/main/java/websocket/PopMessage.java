package websocket;

import bean.Message;
import bean.Type;
import dao.MessageDao;
import dao.UserDao;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import util.ResponseUtils;

import java.util.ArrayList;
import java.util.List;

import static bean.Type.ADD_FRIEND;
import static bean.Type.PHOTO;
import static bean.Type.TEXT;

public class PopMessage {
    private MessageDao messageDao = MessageDao.getMessageDao();
    private static PopMessage popMessage = new PopMessage();

    public static PopMessage getPopMessage() {
        return popMessage;
    }

    public void pop(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        messageDao.getMessages(uid, res -> {
            List<JsonObject> list = res.result();
            List<Future> futures = new ArrayList<>();
            for (int i = 0, j = list.size(); i < j; i++) {
                JsonObject message = list.get(i);
                Future<JsonObject> future = Future.future();
                switch (message.getInteger("type")) {
                    case 0:

                        futures.add(future);
                        break;
                    case 1:

                        futures.add(future);
                        break;
                    case 2:
                        sendAddFriend(message,uid,future);
                        futures.add(future);
                        break;
                    default:
                        break;
                }
            }
            CompositeFuture.all(futures).setHandler(allRes->{
                if(allRes.failed()) {
                    handler.handle(Future.failedFuture(allRes.cause()));
                } else {
                    handler.handle(Future.succeededFuture(list));
                }
            });
        });
    }

    //添加好友请求发送给目标用户
    public void sendAddFriend(JsonObject message,int uid, Handler<AsyncResult<JsonObject>> handler) {
        UserDao.getUserDao().getUserById(uid, userRes -> {
            if (userRes.failed()) {
                handler.handle(Future.failedFuture(new AppException(ResponseUtils.SERVER_FAIL, "服务器错误")));
            } else {
                JsonObject returnMsg = message.mergeIn(userRes.result());
                handler.handle(Future.succeededFuture(returnMsg));
            }

        });
    }
}
