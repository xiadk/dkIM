package dao;

import bean.Message;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;

import java.util.List;

public class MessageDao {
    private static MessageDao messageDao = new MessageDao();

    public static MessageDao getMessageDao(){
        return messageDao;
    }

    public void insertMessage(Message message, int uid,int rea_send, Handler<AsyncResult<Integer>> handler){
        String sql = "insert into messages (uid,fid,ope,type,body,rea_send) values(?,?,?,?,?,?) returning mid";

        JsonArray params = new JsonArray();
        params.add(uid).add(message.getFid()).add(message.getOpe().val).add(message.getType().val).add(message.getBody()).add(rea_send);
        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows().get(0).getInteger("mid")));
            }
        });
    }

    public void updateRead(int mid, Handler<AsyncResult<Void>> handler){
        String sql ="update messages set  is_read=1  where mid=?";
        JsonArray params = new JsonArray();
        params.add(mid);
        BaseDao.updateWithParams(sql,params,res->{
            if(res.failed() || res.result().getUpdated()!=1) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void getMessages(int uid, Handler<AsyncResult<List<JsonObject>>> handler){
        String sql="select * from messages where fid=? and is_read=0";

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

    public void getMessagesByUidAndFid(int uid,int fid,int page, Handler<AsyncResult<List<JsonObject>>> handler){
        String sql="select messages.*,photo from messages,users where users.uid=messages.rea_send and  messages.uid in (?,?)  and messages.fid in(?,?) and type <> 2 and ? <> all(invisible) order by create_time desc LIMIT 6 OFFSET (?-1)*6 ";

        JsonArray params = new JsonArray();
        params.add(fid).add(uid).add(fid).add(uid).add(uid).add(page);

        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

    public void getAddFriendMessages(int uid, Handler<AsyncResult<List<JsonObject>>> handler){
        String sql="select messages.*,users.name,users.photo from messages,users where fid=? and is_read=0 and messages.is_del=0 and messages.type=2 and users.uid=messages.uid order by messages.create_time";

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

     //删除信息
        public void delMessage(int uid,int fid, Handler<AsyncResult<Void>> handler){
        String sql ="update messages set invisible = array_append(invisible,?) where uid in(?,?) and fid in(?,?) and ? <> all(invisible) ";
        JsonArray params = new JsonArray();
        params.add(""+uid).add(fid).add(uid).add(uid).add(fid).add(uid);
        BaseDao.updateWithParams(sql,params,res->{
            if(res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }
}
