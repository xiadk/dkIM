package service;

import connector.RedisOperator;
import dao.UserDao;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.Session;
import util.BasicUtils;
import util.ResponseUtils;

public class LoginService {
    private static LoginService loginService = new LoginService();
    private UserDao userDao = UserDao.getUserDao();
    public static LoginService getLoginService(){
        return loginService;
    }

    public void login(String phone,String password,Handler<AsyncResult<String>> handler){
        userDao.getUserByPhone(phone,res->{
            if (res.failed()) {
               handler.handle(Future.failedFuture(res.cause()));
            } else {

                int uid = res.result().getInteger("uid");
                String pwd = res.result().getString("password");
                if (!pwd.equals(password)) {
                    handler.handle(Future.failedFuture(new AppException(ResponseUtils.PWD_ERROR,"密码错误")));
                } else {
                    RedisOperator.get("uid:"+uid,tokenRes->{
                        if(tokenRes.failed()) {
                            handler.handle(Future.failedFuture(res.cause()));
                        } else {
                            String oldToken = tokenRes.result();
                            if(oldToken!=null) {
                                RedisOperator.delete(oldToken,delTokenRes->{
                                    if(delTokenRes.failed()) {
                                        handler.handle(Future.failedFuture(res.cause()));
                                    } else {
                                        setToken(uid,handler);
                                    }
                                });
                            } else {
                                setToken(uid,handler);
                            }

                        }
                    });


                }
            }
        });

    }

    public void setToken (int uid,Handler<AsyncResult<String>> handler){
        //将令牌放入redis中
        String token = BasicUtils.createToken();
        Future<Long> future1 = Future.future();
        RedisOperator.add(token,uid+"",3600l,future1);

        Future<Void> future2 = Future.future();
        RedisOperator.set("uid:"+uid,token,future2);
        CompositeFuture.all(future1,future2).setHandler(res1->{
            if(res1.failed()){
                handler.handle(Future.failedFuture(res1.cause()));
            }else {
                handler.handle(Future.succeededFuture(token));
            }
        });
    }

    public void login_out(int uid,Handler<AsyncResult<Long>> handler){
        RedisOperator.get("uid:"+String.valueOf(uid),tokenRes->{
            if(tokenRes.failed()) {
                handler.handle(Future.failedFuture(tokenRes.cause()));
            } else {
                String token = tokenRes.result();
                RedisOperator.delete(token,handler);
            }
        });
    }
}
