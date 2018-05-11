package dao;

import bean.Group;
import io.netty.util.Recycler;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;

import java.util.ArrayList;
import java.util.List;

public class GroupDao {

     private static GroupDao groupDao = new GroupDao();

    public static GroupDao getGroupDao(){
        return groupDao;
    }


    public void createGroup(String gname,int owner, Handler<AsyncResult<Integer>> handler){

        String sql = "insert into groups (gname,owner) values(?,?) returning gid";

        JsonArray params = new JsonArray();
        params.add(gname).add(owner);
        BaseDao.queryWithParams(sql,params, res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows().get(0).getInteger("gid")));
            }
        });
    }

    public void addGroup_members(int gid,JsonArray members,Handler<AsyncResult<Void>> handler){
        String sql = "insert into groups_members (gid,uid) values(?,?)";

        JsonArray params = new JsonArray();
        List<Future> futures = new ArrayList<>();
        for(int i=0,j=members.size();i<j;i++) {
            params.clear().add(gid).add(Integer.parseInt(members.getString(i)));
            Future future = Future.future();
            BaseDao.queryWithParams(sql, params,future);
            futures.add(future);
        }

        CompositeFuture.all(futures).setHandler(res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void selectMembers(int gid, Handler<AsyncResult<List<JsonObject>>> handler){

        String sql = "select gm.uid,photo,name from groups_members as gm left join users on users.uid=gm.uid where gid=?";

        JsonArray params = new JsonArray();
        params.add(gid);
        BaseDao.queryWithParams(sql,params, res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void delMembers(int gid,JsonArray member, Handler<AsyncResult<Void>> handler){

        StringBuilder sql = new StringBuilder("delete from groups_members where gid=? and uid in (");
        JsonArray params = new JsonArray();
        params.add(gid);
        for(int i=0;i<member.size();i++) {
            sql.append("?,");
            params.add(Integer.parseInt(member.getString(i)));
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(")");


        BaseDao.updateWithParams(sql.toString(),params, res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void updateGroupName(int gid ,String gname,Handler<AsyncResult<Void>> handler){
        String sql="update groups set gname=? where gid=?";

        JsonArray params = new JsonArray();
        params.add(gname).add(gid);
        BaseDao.queryWithParams(sql,params, res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }


    public void selectMembersUid(int gid, Handler<AsyncResult<List<JsonObject>>> handler){

        String sql = "select uid from groups_members where gid=?";

        JsonArray params = new JsonArray();
        params.add(gid);
        BaseDao.queryWithParams(sql,params, res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

     public void selectGroup(int gid, Handler<AsyncResult<List<JsonObject>>> handler){

        String sql = "select * from groups where gid=?";

        JsonArray params = new JsonArray();
        params.add(gid);
        BaseDao.queryWithParams(sql,params, res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

}
