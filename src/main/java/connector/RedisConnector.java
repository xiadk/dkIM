package connector;

import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import util.ConfigUtils;

public class RedisConnector {

    static RedisOptions redisOptions = null;
    static Vertx vertx = null;
    private static int port = ConfigUtils.getInteger("redis_port");

    public static void init(Vertx vertx) {
        RedisOptions redisOptions = new RedisOptions();
        redisOptions.setHost("127.0.0.1").setPort(port).setSelect(0);
        RedisConnector.vertx = vertx;
        RedisConnector.redisOptions = redisOptions;
    }

    public static RedisClient getRedisClient() {
        RedisClient redisClient = RedisClient.create(vertx, redisOptions);
        return redisClient;
    }
}
