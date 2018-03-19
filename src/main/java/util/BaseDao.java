package util;

import connector.PGConnector;
import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by jiangxb on 17-9-5.
 */
public class BaseDao {

    private static Logger logger = LoggerFactory.getLogger("DATABASE");

    public static void query(String sql, Handler<AsyncResult<ResultSet>> done) {
        PGConnector.getAsyncConnection().query(sql, res -> {
            if (res.failed()) {
                done.handle(Future.failedFuture(new AppException(ResponseUtils.SERVER_FAIL, "SQL execution failed.")));
                logger.warn("BaseDao query(Failed): sql = {}", sql);
            } else {
                try {
                    done.handle(res);
                } catch (Exception e) {
                    done.handle(Future.failedFuture(e));
                    logger.error("BaseDao query(Exception): sql = {}, exception = {}", sql, StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : e.getStackTrace());
                }
            }
        });
    }

    public static void queryWithParams(String sql, JsonArray params, Handler<AsyncResult<ResultSet>> done) {

        PGConnector.getAsyncConnection().queryWithParams(sql, params, res -> {
            if (res.failed()) {
                done.handle(Future.failedFuture(new AppException(ResponseUtils.SERVER_FAIL, "SQL execution failed.")));
                logger.warn("BaseDao queryWithParams(Failed): sql = {}, params = {}", sql, params.toString());
            } else {
                try {
                    done.handle(res);
                } catch (Exception e) {
                    done.handle(Future.failedFuture(e));
                    logger.error("BaseDao queryWithParams(Exception): sql = {}, params = {}, exception = {}", sql, params.toString(),
                            StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : e.getStackTrace());
                }
            }
        });
    }



    public static void update(String sql, Handler<AsyncResult<Integer>> done) {
        PGConnector.getAsyncConnection().update(sql, res -> {
            try {
                if (res.succeeded()) {
                    done.handle(Future.succeededFuture(res.result().getUpdated()));
                } else {
                    done.handle(Future.failedFuture(res.cause()));
                    logger.warn("BaseDao update(Failed): sql = {}", sql);
                }
            } catch (Exception e) {
                done.handle(Future.failedFuture(e));
                logger.error("BaseDao update(Exception): sql = {}, exception = {}", sql, StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : e.getStackTrace());
            }
        });
    }

    public static void updateWithParams(String sql, JsonArray params, Handler<AsyncResult<UpdateResult>> done) {

        PGConnector.getAsyncConnection().updateWithParams(sql, params, ms -> {
            if (ms.failed()) {
                done.handle(Future.failedFuture(new AppException(ResponseUtils.SERVER_FAIL, "SQL execution failed.")));
                logger.warn("BaseDao updateReturnAsyncResult(Failed): sql = {}, params = {}", sql, params.toString());
            } else {
                try {
                    done.handle(ms);
                } catch (Exception e) {
                    done.handle(Future.failedFuture(e));
                    logger.error("BaseDao updateReturnAsyncResult(Exception): sql = {}, params = {}, exception = {}", sql, params.toString(),
                            StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : e.getStackTrace());
                }
            }
        });
    }


    //查询语句
    public static void select(String tableName,Map<String,Object> condition,JsonArray resColumn,Handler<AsyncResult<ResultSet>> handler){

        StringBuilder sb = new StringBuilder("select ");
        for (int i=0,j=resColumn.size();i<j;i++) {
            sb.append(resColumn.getString(i)+",");
        }

        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(" from "+tableName+" where ");

        JsonArray params = new JsonArray();
        for (Map.Entry<String,Object> map:condition.entrySet()) {
            sb.append(map.getKey()+"=? and ");
            params.add(map.getValue());
        }

        sb.delete(sb.lastIndexOf("and"),sb.length());

        queryWithParams(sb.toString(),params,handler);
    }
}