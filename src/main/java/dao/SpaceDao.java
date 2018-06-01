package dao;

import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;
import util.ResponseUtils;

import java.util.ArrayList;
import java.util.List;

public class SpaceDao {

     private static SpaceDao spaceDao = new SpaceDao();

    public static SpaceDao getSpaceDao(){
        return spaceDao;
    }

    public void addSpace(int uid,int type,String content,Handler<AsyncResult<Integer>> handler) {
        String sql="insert into space (uid,content,type)values(?,?,?) returning id";

         JsonArray params = new JsonArray();
         params.add(uid).add(content).add(type);
         BaseDao.queryWithParams(sql,params,res->{
             if(res.failed() || res.result().getRows().size()==0){
                handler.handle(Future.failedFuture(res.cause()));
            }else {
                 handler.handle(Future.succeededFuture(res.result().getRows().get(0).getInteger("id")));
            }
         });
    }

    public void updatelove(int sid,int uid,Handler<AsyncResult<Void>> handler) {
        String sql="insert into comments (uid,sid,type)values(?,?,?)";

         JsonArray params = new JsonArray();
         params.add(uid).add(sid).add(1);
         BaseDao.updateWithParams(sql,params,res->{
             if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            }else {
                 handler.handle(Future.succeededFuture());
            }
         });
    }

    public void dellove(int sid,int uid,Handler<AsyncResult<Void>> handler) {
        String sql="delete from comments where uid=? and sid=? and type=1";

         JsonArray params = new JsonArray();
         params.add(uid).add(sid);
         BaseDao.updateWithParams(sql,params,res->{
             if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            }else {
                 handler.handle(Future.succeededFuture());
            }
         });
    }

    public void insertComment(int uid,int sid,String comment,Handler<AsyncResult<Void>> handler){
        String sql="insert into comments (uid,comment,sid) values (?,?,?)";

         JsonArray params = new JsonArray();
         params.add(uid).add(comment).add(sid);
         BaseDao.updateWithParams(sql,params,res->{
             if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            }else {
                 handler.handle(Future.succeededFuture());
            }
         });
    }

    public void getComment(int sid, Handler<AsyncResult<List<JsonObject>>> handler) {

        String sql = "select users.name,comments.comment,comments.isread from comments,users where users.uid=comments.uid and  sid=? and type=0 order by comments.create_time desc";
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sid);
        BaseDao.queryWithParams(sql,jsonArray,res->{
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result().getRows();
                handler.handle(Future.succeededFuture(list));
            }
        });

    }

    public void getSpace(int uid, Handler<AsyncResult<List<JsonObject>>> handler) {

        String sql = "select  DISTINCT(space.id),content,space.create_time,type, users.photo,friends.alias,friends.fid from space LEFT JOIN friends on friends.fid=space.uid LEFT JOIN users on users.uid=friends.fid where   friends.uid=? or space.uid=? order by space.create_time desc";
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(uid).add(uid);
        BaseDao.queryWithParams(sql,jsonArray,res->{
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result().getRows();
                handler.handle(Future.succeededFuture(list));
            }
        });

    }

    public void getDetail(int sid, Handler<AsyncResult<List<JsonObject>>> handler) {

        String sql = "select  DISTINCT(space.id),content,space.create_time,type, users.photo,friends.alias,friends.fid from space LEFT JOIN friends on friends.fid=space.uid LEFT JOIN users on users.uid=friends.fid where  space.id=? order by space.create_time desc";
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sid);
        BaseDao.queryWithParams(sql,jsonArray,res->{
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result().getRows();
                handler.handle(Future.succeededFuture(list));
            }
        });

    }

    public void getGoods(int sid, Handler<AsyncResult<List<JsonObject>>> handler) {

        String sql = "select name from comments left join users on comments.uid=users.uid where sid=? and comments.type=1 order by comments.create_time desc";
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(sid);
        BaseDao.queryWithParams(sql,jsonArray,res->{
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result().getRows();
                handler.handle(Future.succeededFuture(list));
            }
        });

    }

    public void getMyMessages(int uid,Handler<AsyncResult<List<JsonObject>>> handler){

        String sql = "select DISTINCT (comments.id),friends.alias,space.id as sid,friends.fid,space.content,comments.type,comments.comment,comments.create_time,users.photo,space.type as stp from space, comments, friends  , users   where space.uid=? and space.id=comments.sid and friends.fid=comments.uid and users.uid=friends.fid order by comments.create_time desc";
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(uid);
        BaseDao.queryWithParams(sql,jsonArray,res->{
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                List<JsonObject> list = res.result().getRows();
                handler.handle(Future.succeededFuture(list));
            }
        });
    }


     public void delSpace(int sid,Handler<AsyncResult<Void>> handler) {
        String sql="delete from space where id=?";

         JsonArray params = new JsonArray();
         params.add(sid);
         BaseDao.updateWithParams(sql,params,res->{
             if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            }else {
                 handler.handle(Future.succeededFuture());
            }
         });
    }

    public void delComments(int sid,Handler<AsyncResult<Void>> handler) {
        String sql="delete from comments where sid=?";

         JsonArray params = new JsonArray();
         params.add(sid);
         BaseDao.updateWithParams(sql,params,res->{
             if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            }else {
                 handler.handle(Future.succeededFuture());
            }
         });
    }
}
