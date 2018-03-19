package service;

import bean.User;
import dao.UserDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

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
}
