package dao;

import bean.User;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;
import util.ResponseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserDao {
    public static UserDao userDao = new UserDao();
    public static UserDao getUserDao(){
        return userDao;
    }
    public void VerifyUserByPhone(String phone, Handler<AsyncResult<Void>> handler) {
        String sql = "select uid from users where phone=?";
        JsonArray params = new JsonArray().add(phone);
        BaseDao.queryWithParams(sql, params, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else if (res.result().getRows().size() > 0) {
                handler.handle(Future.failedFuture(new AppException(ResponseUtils.REQUEST_EXIST, "用户已经被注册")));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public void insertUser(User user,Handler<AsyncResult<Integer>> handler){
        String sql="insert into users (name,password,phone) values(?,?,?) returning uid";
        JsonArray params = new JsonArray().add(user.getName()).add(user.getPassword()).add(user.getPhone());
        BaseDao.queryWithParams(sql,params,res->{
            if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            }else {
                List<JsonObject> list = res.result().getRows();
                if (list.size() != 1) {
                    handler.handle(Future.failedFuture(new AppException(ResponseUtils.SERVER_FAIL)));
                } else {
                    handler.handle(Future.succeededFuture(list.get(0).getInteger("uid")));
                }
            }
        });

    }

    public void getUserByPhone(String phone, Handler<AsyncResult<JsonObject>> handler) {
        Map<String,Object> map = new HashMap<>();
        map.put("phone",phone);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add("uid").add("password");
        BaseDao.select("users",map,jsonArray,res->{
            if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            } else if (res.result().getRows().size() == 0){
                handler.handle(Future.failedFuture(new AppException(ResponseUtils.REQUEST_NOT_EXIST,"用户未注册")));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows().get(0)));
            }
        });
    }

}
