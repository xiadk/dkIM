package service;

import bean.User;
import dao.FriendsDao;
import dao.UserDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;


public class FriendsService {

    private static FriendsService service = new FriendsService();
    private FriendsDao friendsDao = FriendsDao.getFriendsDao();
    private UserDao userDao = UserDao.getUserDao();

    public static FriendsService getFriendsService(){
        return service;
    }

    public void addFriend(int uid, Handler<AsyncResult<String>> handler){


    }

    public void getFriend(String condition, Handler<AsyncResult<List<JsonObject>>> handler){

        userDao.getUserByPhoneOrName(condition,handler);
    }

}
