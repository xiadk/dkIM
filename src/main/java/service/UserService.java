package service;

import bean.User;
import dao.FriendsDao;
import dao.UserDao;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;
import util.ResponseUtils;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static UserService userService = new UserService();
    private static UserDao userDao = UserDao.getUserDao();
    public static UserService getUserService(){
        return userService;
    }

    public void register(User user, Handler<AsyncResult<Void>> handler){
        userDao.VerifyUserByPhone(user.getPhone(),res->{
            if(res.failed()){
                handler.handle(Future.failedFuture(res.cause()));
            }else{
                userDao.insertUser(user,insertUserRes->{
                    if(insertUserRes.failed()){
                        handler.handle(Future.failedFuture(insertUserRes.cause()));
                    }else {
                        handler.handle(Future.succeededFuture());
                    }
                });
            }
        });
    }

    public void getUserInfo(int uid,Handler<AsyncResult<JsonObject>> handler) {
        userDao.getUserById(uid,handler);
    }

    public void getUserInfoById(int uid,Handler<AsyncResult<JsonObject>> handler){
        userDao.getUserInfoById(uid,handler);
    }

     public void updateUser(User user,Handler<AsyncResult<Integer>> handler){
        userDao.updateUser(user,handler);

    }
}
