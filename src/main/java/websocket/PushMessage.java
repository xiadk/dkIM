package websocket;

import bean.Message;
import bean.Type;
import dao.FriendsDao;
import dao.MessageDao;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import util.ResponseUtils;

public class PushMessage {
    private static PushMessage pushMessage = new PushMessage();
    private MessageDao messageDao = MessageDao.getMessageDao();
    private FriendsDao friendsDao = FriendsDao.getFriendsDao();

    public static PushMessage getPushMessage() {
        return pushMessage;
    }

    public void push(Message message, int poryId, int uid, Handler<AsyncResult<Integer>> handler) {
        switch (message.getType()) {
            case TEXT:
            case GROUP_HINT:
            case PHOTO:
            case FILE:
                messageInit(message, poryId, uid, handler);

                break;
            case ADD_FRIEND:
                friendsDao.selectFriendByUidAndFid(uid, message.getFid(), res -> {
                    if (res.failed() || res.result().size()>0) {
                        handler.handle(Future.failedFuture(res.cause()));
                    } else if(res.result().size()>0){
                        handler.handle(Future.failedFuture(new AppException(ResponseUtils.REQUEST_EXIST,"好友已经存在")));
                    } else {
                        messageDao.insertMessage(message, uid, uid, handler);
                    }
                });
                break;
            default:
                break;
        }
    }

    public void messageInit(Message message, int uid, int rea_send, Handler<AsyncResult<Integer>> handler) {
        Future<Integer> future = Future.future();
        Future<Void> future1 = Future.future();
        Future<Void> future2 = Future.future();
        messageDao.insertMessage(message, uid, rea_send, future);
        if(message.getType()== Type.GROUP_HINT) {
            future1.complete();
            future2.complete();
        } else {
            friendsDao.updateNewContent(message, message.getType(), uid, message.getFid(), future1);
            friendsDao.updateNewContent(message, message.getType(), message.getFid(), uid, future2);
        }
        CompositeFuture.all(future, future1, future2).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(future.result()));
            }
        });
    }

}
