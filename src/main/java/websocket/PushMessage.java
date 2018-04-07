package websocket;

import bean.Message;
import dao.FriendsDao;
import dao.MessageDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class PushMessage {
    private static PushMessage pushMessage = new PushMessage();
    private MessageDao messageDao = MessageDao.getMessageDao();
    private FriendsDao friendsDao = FriendsDao.getFriendsDao();
    public static PushMessage getPushMessage(){
        return pushMessage;
    }
    public void push(Message message,int uid,Handler<AsyncResult<Integer>> handler){
        switch (message.getType()) {
            case TEXT:

                break;
            case PHOTO:

                break;
            case ADD_FRIEND:
                friendsDao.selectFriendByUidAndFid(uid, message.getFid(), res -> {
                    if (res.failed()) {
                        handler.handle(Future.failedFuture(res.cause()));
                    } else {
                        messageDao.insertAddFriend(message, uid, handler);
                    }
                });
                break;
            default:
                break;
        }
    }

}
