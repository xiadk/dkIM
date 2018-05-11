package service;

import dao.FriendsDao;
import dao.GroupDao;
import dao.SpaceDao;
import dao.UserDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import util.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpaceService {

    private static SpaceService groupService = new SpaceService();
    private static GroupDao groupDao = GroupDao.getGroupDao();
    private static FriendsDao friendsDao = FriendsDao.getFriendsDao();
    private static UserDao userDao = UserDao.getUserDao();
    private static SpaceDao spaceDao = SpaceDao.getSpaceDao();

    public static SpaceService getGroupService() {
        return groupService;
    }

    public void addSpace(int uid, int type, String content, Handler<AsyncResult<Void>> handler) {

        spaceDao.addSpace(uid, type, content, handler);

    }

    public void updatelove(int sid, int uid, Handler<AsyncResult<Void>> handler) {
        spaceDao.updatelove(sid, uid, handler);
    }

    public void dellove(int sid, int uid, Handler<AsyncResult<Void>> handler) {
        spaceDao.dellove(sid, uid, handler);
    }

    public void insertComment(int uid, int sid, String comment, Handler<AsyncResult<Void>> handler) {
        spaceDao.insertComment(uid, sid, comment, handler);
    }

    public void getComment(int sid, Handler<AsyncResult<List<JsonObject>>> handler) {
        spaceDao.getComment(sid, handler);

    }

    public void getSpace(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {

        spaceDao.getSpace(uid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result();
                List<Future> futures = new ArrayList<>();
                for (JsonObject js : list) {
                    int sid = js.getInteger("id");
                    Date oldTime = new Date(TimeUtils.StringToDateTime(js.getString("create_time")).getTime());
                    js.put("create_time", TimeUtils.timestamp_beforTime(oldTime, new Date()));
                    Future future = Future.future();
                    //获取点赞
                    spaceDao.getGoods(sid, goodsRes -> {
                        if (goodsRes.failed()) {
                            future.fail(goodsRes.cause());
                        } else if (goodsRes.result().size() > 0) {
                            String name = "";
                            for (int i = 0; i < goodsRes.result().size(); i++) {
                                name = name + goodsRes.result().get(i).getString("name") + ",";
                            }
                            name = name.substring(0, name.lastIndexOf(','));

                            js.put("names", name);
                            future.complete();
                        } else {
                            js.put("names", "");
                            future.complete();
                        }
                    });
                    //获取评论
                    Future future2 = Future.future();
                    spaceDao.getComment(sid, commentsRes -> {
                        if (commentsRes.failed()) {
                            future2.fail(commentsRes.cause());
                        } else if (commentsRes.result().size() > 0) {
                            js.put("comments", commentsRes.result());
                            future2.complete();
                        } else {
                            js.put("comments", "");
                            future2.complete();
                        }
                    });
                    futures.add(future);
                    futures.add(future2);

                }
                CompositeFuture.all(futures).setHandler(allRes -> {
                    if (allRes.failed()) {
                        handler.handle(Future.failedFuture(allRes.cause()));
                    } else {
                        handler.handle(Future.succeededFuture(list));
                    }
                });
            }
        });
    }

    public void getDetail(int sid, Handler<AsyncResult<List<JsonObject>>> handler) {

        spaceDao.getDetail(sid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result();
                List<Future> futures = new ArrayList<>();
                for (JsonObject js : list) {
                    Date oldTime = new Date(TimeUtils.StringToDateTime(js.getString("create_time")).getTime());
                    js.put("create_time", TimeUtils.timestamp_beforTime(oldTime, new Date()));
                    Future future = Future.future();
                    //获取点赞
                    spaceDao.getGoods(sid, goodsRes -> {
                        if (goodsRes.failed()) {
                            future.fail(goodsRes.cause());
                        } else if (goodsRes.result().size() > 0) {
                            String name = "";
                            for (int i = 0; i < goodsRes.result().size(); i++) {
                                name = name + goodsRes.result().get(i).getString("name") + ",";
                            }
                            name = name.substring(0, name.lastIndexOf(','));

                            js.put("names", name);
                            future.complete();
                        } else {
                            js.put("names", "");
                            future.complete();
                        }
                    });
                    //获取评论
                    Future future2 = Future.future();
                    spaceDao.getComment(sid, commentsRes -> {
                        if (commentsRes.failed()) {
                            future2.fail(commentsRes.cause());
                        } else if (commentsRes.result().size() > 0) {
                            js.put("comments", commentsRes.result());
                            future2.complete();
                        } else {
                            js.put("comments", "");
                            future2.complete();
                        }
                    });
                    futures.add(future);
                    futures.add(future2);

                }
                CompositeFuture.all(futures).setHandler(allRes -> {
                    if (allRes.failed()) {
                        handler.handle(Future.failedFuture(allRes.cause()));
                    } else {
                        handler.handle(Future.succeededFuture(list));
                    }
                });
            }
        });
    }

    public void getMyMessages(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        spaceDao.getMyMessages(uid, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result();
                for (int i = 0, j = list.size(); i < j; i++) {
                    Date oldTime = new Date(TimeUtils.StringToDateTime(list.get(i).getString("create_time")).getTime());
                    list.get(i).put("create_time", TimeUtils.timestamp_beforTime(oldTime, new Date()));
                }
                handler.handle(Future.succeededFuture(list));
            }
        });
    }

    public void delSpace(int sid, Handler<AsyncResult<Void>> handler) {
        Future future = Future.future();
        spaceDao.delSpace(sid, future);
        Future future2 = Future.future();
        spaceDao.delComments(sid, future2);
        CompositeFuture.all(future, future2).setHandler(allRes -> {
            if (allRes.failed()) {
                handler.handle(Future.failedFuture(allRes.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

}
