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
        List<JsonObject> list = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        for (int i = 0, j = member.size(); i < j; i++) {
            list.add(jsonObject.clear().put("uid",Integer.parseInt(member.getString(i))));
        }
        Future future1 = Future.future();
        groupDao.delMembers(gid, member, handler);
        futures.add(future1);
        Future delFriendFu = Future.future();
        friendsDao.deleteFriends(list, gid, delFriendFu);
        futures.add(delFriendFu);
        Future delContactFu = Future.future();
        friendsDao.deleteContacts(list,gid,delContactFu);
        futures.add(delContactFu);

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

    public void addGroup_members(int gid,String gname, JsonArray members, Handler<AsyncResult<Void>> handler) {
        List<Future> futures = new ArrayList<>();
        Future future1 = Future.future();
        groupDao.addGroup_members(gid, members, future1);
        futures.add(future1);
        for (int i = 0, j = members.size(); i < j; i++) {
            Future future2 = Future.future();
            friendsDao.insertContact(gid, Integer.parseInt(members.getString(i)), 1, future2);
            futures.add(future2);
            Future future3 = Future.future();
            friendsDao.insertFriend(Integer.parseInt(members.getString(i)), gid, gname, gid, 1, future3);
            futures.add(future3);
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
        groupDao.selectGroup(gid, groupRes -> {
            if (groupRes.failed()) {
                handler.handle(Future.failedFuture(groupRes.cause()));
            } else {

                Future<Void> delemberFu = Future.future();
                Future<Void> delFriendFu = Future.future();
                Future<Void> delContactFu = Future.future();
                Future<Void> delGroupFu = Future.future();
                JsonArray members = new JsonArray();
                int owner = groupRes.result().get(0).getInteger("owner");
                if (owner == uid) {
                    groupDao.selectMembersUid(gid,membsersRes->{
                        if(membsersRes.failed()) {
                            delGroupFu.fail(membsersRes.cause());
                        } else {
                            List<JsonObject> list = membsersRes.result();
                            for(int i = 0,j =list.size();i<j;i++) {
                                members.add(""+list.get(i).getInteger("uid"));
                            }
                            groupDao.delMembers(gid, members, delemberFu);
                            friendsDao.deleteContacts(list,gid,delContactFu);
                            friendsDao.deleteFriends(list, gid, delFriendFu);
                            groupDao.delGroup(gid, delGroupFu);
                        }
                    });
                } else {
                    delGroupFu.complete();
                    groupDao.delMembers(gid, members.add(""+uid), delemberFu);
                    friendsDao.deleteContact(uid, gid, delContactFu);
                    friendsDao.deleteFriend(uid, gid, delFriendFu);
                }




                CompositeFuture.all(delContactFu, delemberFu, delFriendFu, delGroupFu).setHandler(res -> {
                    if (res.failed()) {
                        handler.handle(Future.failedFuture(res.cause()));
                    } else {
                        handler.handle(Future.succeededFuture());
                    }
                });
            }
        });


    }

    public void updateGroupName(int gid, int uid, String gname, Handler<AsyncResult<Void>> handler) {
        Future<Void> future = Future.future();
        groupDao.updateGroupName(gid, gname, future);
        Future<Void> future1 = Future.future();
        groupDao.selectMembersUid(gid,MemberRes->{
            if(MemberRes.failed()) {
                future1.fail(MemberRes.cause());
            } else {
                friendsDao.updateGroupAlias(gname,MemberRes.result(),gid,future1);
            }
        });
        CompositeFuture.all(future, future1).setHandler(res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void selectGroupByUid(int gid, Handler<AsyncResult<List<JsonObject>>> handler) {

        groupDao.selectGroupByUid(gid, handler);
    }


}
