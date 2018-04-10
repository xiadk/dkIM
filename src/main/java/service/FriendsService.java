package service;

import dao.FriendsDao;
import dao.UserDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;


public class FriendsService {

    private static FriendsService service = new FriendsService();
    private FriendsDao friendsDao = FriendsDao.getFriendsDao();
    private UserDao userDao = UserDao.getUserDao();

    public static FriendsService getFriendsService() {
        return service;
    }

    public void addFriend(int uid, int fid, String fidName, String uidName, Handler<AsyncResult<Void>> handler) {
        Future<Void> future1 = Future.future();
        friendsDao.insertFriend(uid, fid, fidName, uid, future1);
        Future<Void> future2 = Future.future();
        friendsDao.insertFriend(fid, uid, uidName, uid, future2);
        CompositeFuture.all(future1, future2).setHandler(res1 -> {
            if (res1.failed()) {
                handler.handle(Future.failedFuture(res1.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });

    }

    public void getFriend(String condition, Handler<AsyncResult<List<JsonObject>>> handler) {

        userDao.getUserByPhoneOrName(condition, handler);
    }


    public void findFriends(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        userDao.findFriends(uid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result()));
            }
        });
    }


    public void delFriend(int uid, int fid, Handler<AsyncResult<Void>> handler) {
        Future<Void> future1 = Future.future();
        friendsDao.deleteFriend(uid, fid, future1);
        Future<Void> future2 = Future.future();
        friendsDao.deleteFriend(fid, uid, future2);
        CompositeFuture.all(future1, future2).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void updateAlias(String alias,int uid,int fid,Handler<AsyncResult<Void>> handler){
        friendsDao.updateAlias(alias,uid,fid,handler);
    }

    public void addContact(int fid,int uid,Handler<AsyncResult<Void>> handler) {
        friendsDao.insertContact(fid,uid,handler);
    }

    public void delContact(int uid,int fid,Handler<AsyncResult<Void>> handler) {
        friendsDao.deleteContact(uid,fid,handler);
    }

    public void getContacts(int uid,Handler<AsyncResult<List<JsonObject>>> handler){
        friendsDao.selectContact(uid,handler);
    }

}
