package connector;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import util.ConfigUtils;

public class PGConnector {

    static AsyncSQLClient asc = null;
    private static int port = ConfigUtils.getInteger("postgersql.port");
    private static String username = ConfigUtils.getString("postgersql.username");
    private static String password = ConfigUtils.getString("postgersql.password");
    private static String database = ConfigUtils.getString("postgersql.database");
    private static int maxPoolSize = ConfigUtils.getInteger("postgersql.maxPoolSize");

    public static void init(Vertx vertx){
        JsonObject pgConfig = new JsonObject();
        pgConfig.put("host", "127.0.0.1")
                .put("port", port)
                .put("username", username)
                .put("password", password)
                .put("database", database)
                .put("maxPoolSize", maxPoolSize);
        asc = PostgreSQLClient.createShared(vertx, pgConfig);
    }

    public static AsyncSQLClient getAsyncConnection() {
        return asc;
    }
}
