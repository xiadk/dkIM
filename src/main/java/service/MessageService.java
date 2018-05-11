package service;

import bean.Message;
import bean.Type;
import dao.FriendsDao;
import dao.MessageDao;
import io.netty.util.internal.StringUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private static MessageService messageService = new MessageService();
    private MessageDao messageDao = MessageDao.getMessageDao();
    private FriendsDao friendsDao = FriendsDao.getFriendsDao();

    public static MessageService getMessageService() {
        return messageService;
    }

    public void updateRead(int mid, Handler<AsyncResult<Void>> handler) {
        messageDao.updateRead(mid, handler);
    }

    public void getMessages(int uid, int fid, int page, Handler<AsyncResult<List<JsonObject>>> handler) {
        messageDao.getMessagesByUidAndFid(uid, fid, page, res -> {
            List<Future> list = new ArrayList<>();
            for (int i = 0, j = res.result().size(); i < j; i++) {
                Future<Void> future = Future.future();
                messageDao.updateRead(res.result().get(i).getInteger("mid"), future);
                list.add(future);
            }
            CompositeFuture.any(list).setHandler(mesRes -> {

                if (mesRes.failed()) {
                    handler.handle(Future.failedFuture(mesRes.cause()));
                } else {
                    handler.handle(Future.succeededFuture(res.result()));
                }
            });
        });
    }

    public void getAddFriendMessages(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        messageDao.getAddFriendMessages(uid, handler);
    }

    public void delMessage(int uid, int fid, Handler<AsyncResult<Void>> handler) {
        Future<Void> future = Future.future();
        messageDao.delMessage(uid, fid, future);
        Future<Void> future1 = Future.future();
        Message message = new Message();
        message.setBody("");
        friendsDao.updateNewContent(message, Type.TEXT,uid, fid, future1);

        CompositeFuture.all(future, future1).setHandler(mesRes -> {

            if (mesRes.failed()) {
                handler.handle(Future.failedFuture(mesRes.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });

    }



}
