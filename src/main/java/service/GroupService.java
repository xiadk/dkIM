package service;

import dao.FriendsDao;
import dao.GroupDao;
import dao.UserDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;
import util.BasicUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupService {

    private static GroupService groupService = new GroupService();
    private static GroupDao groupDao = GroupDao.getGroupDao();
    private static FriendsDao friendsDao = FriendsDao.getFriendsDao();
    private static UserDao userDao = UserDao.getUserDao();

    public static GroupService getGroupService() {
        return groupService;
    }

    public void createGroup(int uid, JsonArray members, Handler<AsyncResult<JsonObject>> handler) {

        String gname = "群聊" + BasicUtils.createRandomId();
        members.add("" + uid);
        groupDao.createGroup(gname, uid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<Future> futures = new ArrayList<>();
                int gid = res.result();
                Future<Void> future = Future.future();
                groupDao.addGroup_members(gid, members, future);
                futures.add(future);

                //插入联系人列表
                for (int i = 0, j = members.size(); i < j; i++) {
                    Future<Void> future1 = Future.future();
                    friendsDao.insertContact(gid, Integer.parseInt(members.getString(i)), 1, future1);
                    futures.add(future1);
                    Future<Void> future2 = Future.future();
                    friendsDao.insertFriend(Integer.parseInt(members.getString(i)), gid, gname, gid, 1, future2);
                    futures.add(future2);
                }

                CompositeFuture.all(futures).setHandler(res1 -> {
                    if (res1.failed()) {
                        handler.handle(Future.failedFuture(res1.cause()));
                    } else {
                        JsonObject js = new JsonObject();
                        js.put("gid", gid);
                        js.put("gname", gname);
                        handler.handle(Future.succeededFuture(js));
                    }
                });
            }
        });
    }

    public void selectMembers(int gid, Handler<AsyncResult<List<JsonObject>>> handler) {
        groupDao.selectMembers(gid, handler);
    }

    public void delMembers(int gid, JsonArray member, Handler<AsyncResult<Void>> handler) {
        List<Future> futures = new ArrayList<>();
        Future future1 = Future.future();
        groupDao.delMembers(gid, member, handler);
        futures.add(future1);
        for(int i=0,j=futures.size();i<j;i++) {
            Future future2 = Future.future();
            friendsDao.deleteContact(Integer.parseInt(member.getString(i)),gid,future2);
            futures.add(future2);
        }
        CompositeFuture.all(futures).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });

    }

    public void findFriendByfid(int uid, int fid, Handler<AsyncResult<JsonObject>> handler) {
        userDao.findFriendByfid(fid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                friendsDao.getAliasByFid(uid, fid, aliasRes -> {
                    if (aliasRes.failed()) {
                        handler.handle(Future.failedFuture(aliasRes.cause()));
                    } else {
                        String alias = "";
                        if (aliasRes.result().size() > 0) {
                            alias = aliasRes.result().get(0).getString("alias");
                        }
                        JsonObject jsonObject = res.result().put("alias", alias);
                        handler.handle(Future.succeededFuture(jsonObject));
                    }
                });
            }
        });
    }

    public void addGroup_members(int gid, JsonArray members, Handler<AsyncResult<Void>> handler) {
        List<Future> futures = new ArrayList<>();
        Future future1 = Future.future();
        groupDao.addGroup_members(gid, members, future1);
        futures.add(future1);
        for(int i=0,j=futures.size();i<j;i++) {
            Future future2 = Future.future();
            friendsDao.insertContact(gid, Integer.parseInt(members.getString(i)), 1, future2);
            futures.add(future2);
        }
        CompositeFuture.all(futures).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void getfriendsToGroup(int gid, int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        groupDao.selectMembers(gid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                friendsDao.selectFriendByUid(uid, friendsRes -> {
                    if (friendsRes.failed()) {
                        handler.handle(Future.failedFuture(res.cause()));
                    } else {
                        List<JsonObject> list = friendsRes.result();
                        List<JsonObject> members = res.result();
                        for (int i = 0; i < list.size(); i++) {
                            for (int j = 0; j < members.size(); j++) {
                                int memberid = members.get(j).getInteger("uid");
                                int fid = list.get(i).getInteger("fid");
                                if (memberid == fid) {
                                    list.remove(i);
                                    i--;
                                    break;
                                }
                            }
                        }

                        handler.handle(Future.succeededFuture(list));
                    }
                });
            }
        });
    }

    public void exitGroup(int gid, int uid, Handler<AsyncResult<Void>> handler) {
        Future<Void> future = Future.future();
        friendsDao.deleteFriend(uid, gid, future);
        Future<Void> future1 = Future.future();
        friendsDao.deleteContact(uid, gid, future1);
        Future<Void> future2 = Future.future();
        groupDao.delMembers(gid, new JsonArray().add(""+uid), future2);
        CompositeFuture.all(future, future2, future1).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void updateGroupName(int gid ,int uid,String gname,Handler<AsyncResult<Void>> handler){
        Future<Void> future = Future.future();
        groupDao.updateGroupName(gid,gname,future);
        Future<Void> future1 = Future.future();
        friendsDao.updateAlias(gname,uid,gid,future1);
        CompositeFuture.all(future,future1).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }




}
