package dao;


import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.BaseDao;

import java.util.List;


public class FileDao {
    private static FileDao friendsDao = new FileDao();


    public static FileDao getFileDao() {
        return friendsDao;
    }

    public static void addFile(String url, int uid, Handler<AsyncResult<Void>> handler) {
        String sql = "insert into files (url,uid) values(?,?)";

        JsonArray params = new JsonArray();
        params.add(url).add(uid);
        BaseDao.updateWithParams(sql, params, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });

    }

    public static void getFiles(int sid , Handler<AsyncResult<List<JsonObject>>> handler){
        String sql ="select url from files where uid=? order by id desc";

        JsonArray params = new JsonArray();
        params.add(sid);
        BaseDao.queryWithParams(sql, params, res -> {
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result().getRows()));
            }
        });
    }

}
