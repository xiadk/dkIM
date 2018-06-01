package service;

import dao.FriendsDao;
import dao.GroupDao;
import dao.MessageDao;
import dao.UserDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class FriendsService {

    private static FriendsService service = new FriendsService();
    private FriendsDao friendsDao = FriendsDao.getFriendsDao();
    private UserDao userDao = UserDao.getUserDao();
    private GroupDao groupDao = GroupDao.getGroupDao();
    private MessageDao messageDao = MessageDao.getMessageDao();

    public static FriendsService getFriendsService() {
        return service;
    }

    public void addFriend(int uid, int fid, String fidName, String uidName, Handler<AsyncResult<Void>> handler) {
        Future<Void> future1 = Future.future();
        friendsDao.insertFriend(uid, fid, fidName, uid, 0, future1);
        Future<Void> future2 = Future.future();
        friendsDao.insertFriend(fid, uid, uidName, uid, 0, future2);
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
        Future<Void> future3 = Future.future();
        messageDao.delMessage(uid,fid,future3);
        Future<Void> future4 = Future.future();
        messageDao.delMessage(fid,uid,future4);
        CompositeFuture.all(future1, future2,future3,future4).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void updateAlias(String alias, int uid, int fid, Handler<AsyncResult<Void>> handler) {
        friendsDao.updateAlias(alias, uid, fid, handler);
    }

    public void addContact(int fid, int uid, Handler<AsyncResult<Void>> handler) {
        friendsDao.selectContactByUidAndFid(uid, fid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else if (res.result().size() > 0) {
                handler.handle(Future.succeededFuture());
            } else {
                friendsDao.insertContact(fid, uid, 0, handler);
            }
        });
    }

    public void delContact(int uid, int fid, Handler<AsyncResult<Void>> handler) {
        friendsDao.deleteContact(uid, fid, handler);
    }

    public void getContacts(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        friendsDao.selectPersonalContact(uid, perRes -> {
            if (perRes.failed()) {
                handler.handle(Future.failedFuture(perRes.cause()));
            } else {
                friendsDao.selectGroupContact(uid, res -> {
                    if (res.failed()) {
                        handler.handle(Future.failedFuture(perRes.cause()));
                    } else {
                        List<JsonObject> list = res.result();
                        list.addAll(perRes.result());
                        handler.handle(Future.succeededFuture(list));
                    }
                });
            }
        });
    }


    public void getContactsByName(int uid,String name, Handler<AsyncResult<List<JsonObject>>> handler) {
        friendsDao.selectFriendByName(uid,name, perRes -> {
            if (perRes.failed()) {
                handler.handle(Future.failedFuture(perRes.cause()));
            } else {
                List<JsonObject> list = perRes.result();
                List<Future> futures = new ArrayList<>();
                for(int i=0,j=list.size();i<j;i++){
                    int fid = list.get(i).getInteger("fid");
                    JsonObject jsonObject = list.get(i);
                    Future future = Future.future();
                    if(0==list.get(i).getInteger("ope")){
                        userDao.getUserById(fid,res->{
                            String photo = res.result().getString("photo");
                            jsonObject.put("photo",photo);
                            future.complete();
                        });
                        futures.add(future);
                    } else {
                        groupDao.selectGroup(fid,res->{
                            String photo = res.result().get(0).getString("photo");
                            jsonObject.put("photo",photo);
                            future.complete();
                        });
                        futures.add(future);
                    }
                }
                CompositeFuture.all(futures).setHandler(res->{
                    if(res.failed()) {
                        handler.handle(Future.failedFuture(res.cause()));
                    } else {
                         handler.handle(Future.succeededFuture(list));
                    }
                });

            }
        });
    }

    public void getAliasByFid(int uid, int fid, Handler<AsyncResult<String>> handler) {
        friendsDao.getAliasByFid(uid, fid, aliasRes -> {
            if (aliasRes.failed()) {
                handler.handle(Future.failedFuture(aliasRes.cause()));
            } else {
                String alias = "";
                if (aliasRes.result().size() != 0) {
                    alias = aliasRes.result().get(0).getString("alias");
                }
                handler.handle(Future.succeededFuture(alias));
            }
        });
    }

}
