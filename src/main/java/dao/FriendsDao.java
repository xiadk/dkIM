package dao;

import bean.Message;
import bean.Type;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;
import util.ResponseUtils;

import java.util.List;


public class FriendsDao {
    private static FriendsDao friendsDao = new FriendsDao();


    public static FriendsDao getFriendsDao() {
        return friendsDao;
    }

    public void selectFriendByUid(int uid,Handler<AsyncResult<List<JsonObject>>> handler){
        String sql="select friends.fid,photo,alias from friends left join users on friends.fid=users.uid where friends.uid=? and ope=0";
        JsonArray params = new JsonArray();
        params.add(uid);
        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void selectFriendByUidAndFid(int uid,int fid,Handler<AsyncResult<List<JsonObject>>> handler){
        String sql="select * from friends where uid=? and fid=?";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid);
        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void insertFriend(int uid,int fid,String name,int invite,int ope,Handler<AsyncResult<Void>> handler){
        String sql="insert into friends (uid,fid,alias,invite,ope)values (?,?,?,?,?)";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid).add(name).add(invite).add(ope);
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

    /**
     *
     * @param fid 联系人id
     * @param uid 用户id
     * @param ope 联系人类型 (0:个人1:群)
     * @param handler
     */
    public void insertContact(int fid,int uid,int ope,Handler<AsyncResult<Void>> handler) {
        String sql="insert into contacts (uid,fid,ope)values (?,?,?)";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid).add(ope);
        BaseDao.updateWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void deleteContact(int uid,int fid,Handler<AsyncResult<Void>> handler){
        String sql = "delete from contacts where uid=? and fid=?";
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

    public void selectContactByUidAndFid(int uid,int fid,Handler<AsyncResult<List<JsonObject>>> handler){

        String sql = "select cid from contacts where uid=? and fid=?";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid);
        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void selectPersonalContact(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        String sql = "select contacts.new_content,contacts.type,contacts.fid,contacts.ope,users.photo,alias,coalesce(msg.unread,0) as unread from users,friends,contacts LEFT JOIN (select uid,fid,count(*) as unread from messages where is_read=0 GROUP BY uid,fid) as msg on msg.fid = contacts.uid and msg.uid = contacts.fid where contacts.fid=users.uid and contacts.fid=friends.fid and contacts.uid=friends.uid and contacts.uid=?";
        JsonArray params = new JsonArray();
        params.add(uid);
        BaseDao.queryWithParams(sql, params, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void selectGroupContact(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {
        String sql = "select contacts.new_content,contacts.type,contacts.fid,contacts.ope,groups.photo,gname as alias,coalesce(msg.unread,0) as unread from groups,friends,contacts LEFT JOIN (select uid,fid,count(*) as unread from messages where is_read=0 GROUP BY uid,fid) as msg on msg.fid = contacts.uid and msg.uid = contacts.fid where contacts.fid=groups.gid and contacts.fid=friends.fid and contacts.uid=friends.uid and contacts.uid=?";
        JsonArray params = new JsonArray();
        params.add(uid);
        BaseDao.queryWithParams(sql, params, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void updateNewContent(Message message, Type type, int uid, int fid, Handler<AsyncResult<Void>> handler){
        String sql = "update contacts set new_content=?,type=? where uid=? and fid=?";
         JsonArray params = new JsonArray();
        params.add(message.getBody()).add(type.val).add(uid).add(fid);
        BaseDao.updateWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void getAliasByFid(int uid,int fid,Handler<AsyncResult<List<JsonObject>>> handler){
        String sql = "select alias from friends where uid=? and fid=?";
        JsonArray params = new JsonArray();
        params.add(uid).add(fid);
        BaseDao.queryWithParams(sql, params, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void selectFriendByName(int uid,String name,Handler<AsyncResult<List<JsonObject>>> handler){
        String sql="select fid,alias,ope from friends where uid=? and alias like ?";
        JsonArray params = new JsonArray();
        params.add(uid).add("%"+name+"%");
        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

}
