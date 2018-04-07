package dao;

import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import util.BaseDao;
import util.ResponseUtils;


public class FriendsDao {
    private static FriendsDao friendsDao = new FriendsDao();


    public static FriendsDao getFriendsDao() {
        return friendsDao;
    }

    public void selectFriendByUidAndFid(int uid,int fid,Handler<AsyncResult<Void>> handler){
        String sql="select uid from friends where uid=? and fid=?";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid);
        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else if(res.result().getRows().size()>0){
                handler.handle(Future.failedFuture(new AppException(ResponseUtils.REQUEST_EXIST,"好友已存在")));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void insertFriend(int uid,int fid,String name,int invite,Handler<AsyncResult<Void>> handler){
        String sql="insert into friends (uid,fid,alias,invite)values (?,?,?,?)";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid).add(name).add(invite);
        BaseDao.updateWithParams(sql,params,res->{
            if(res.failed() || res.result().getUpdated()!=1) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void deleteFriend(int uid,int fid,Handler<AsyncResult<Void>> handler){
        String sql = "delete from friends where uid=? and fid=?";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid);
        BaseDao.updateWithParams(sql,params,res->{
            if(res.failed() || res.result().getUpdated()!=1) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void updateAlias(String alias,int uid,int fid,Handler<AsyncResult<Void>> handler){
        String sql ="update friends set alias=? where uid=? and fid=?";
        JsonArray params = new JsonArray();
        params.add(alias).add(uid).add(fid);
        BaseDao.updateWithParams(sql,params,res->{
            if(res.failed() || res.result().getUpdated()!=1) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }
}
