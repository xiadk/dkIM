package dao;

import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsDao {
    private static FriendsDao friendsDao = new FriendsDao();



    public static FriendsDao getFriendsDao() {
        return friendsDao;
    }
}
