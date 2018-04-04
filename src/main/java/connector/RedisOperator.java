package connector;

import exception.AppException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisTransaction;
import io.vertx.redis.op.GeoRadiusOptions;
import io.vertx.redis.op.GeoUnit;
import util.ResponseUtils;

/**
 * Created by xox on 17-9-5.
 */
public class RedisOperator {

    final static Logger logger = LoggerFactory.getLogger(RedisOperator.class);
    public static final String REDIS_OK = "OK";

    public static void set(String key, String value, Handler<AsyncResult<Void>> handler) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.set(key, value, res -> {
            close(redisClient);
            if (res.failed()) {
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
    }

    public static void add(String key, String value, long seconds, Handler<AsyncResult<Long>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        Future.<Void>future(addKeyFuture -> {
            redisClient.set(key, value, addKeyFuture.completer());
        }).compose(suceess -> {
            Future<Long> expiresFuture = Future.future();
            redisClient.expire(key, seconds, expiresFuture.completer());
            return expiresFuture;
        }).setHandler(handler -> {
            close(redisClient);
            if (handler.succeeded()) {
                done.handle(Future.succeededFuture(handler.result()));
            } else {
                logger.debug("redis添加key失败: ", handler.cause(), " key: " + key, " value: " + value);
                done.handle(Future.failedFuture(handler.cause()));
            }
        });
    }

    public static void add(String key, JsonObject values, long expire, Handler<AsyncResult<Long>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        Future.<String>future(addHmSet -> {
            redisClient.hmset(key, values, addHmSet.completer());
        }).compose(hmSetResult -> {
            Future<Long> expireFuture = Future.future();
            redisClient.expire(key, expire, expireFuture.completer());
            return expireFuture;
        }).setHandler(handler -> {
            close(redisClient);
            if (handler.succeeded()) {
                done.handle(Future.succeededFuture(handler.result()));
            } else {
                logger.debug("redis添加key失败: ", handler.cause(), " key: " + key, " value: " + values.toString());
                done.handle(Future.failedFuture(handler.cause()));
            }
        });
    }

    public static void hmset(String key, JsonObject values, Handler<AsyncResult<String>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.hmset(key, values, res -> {
            close(redisClient);
            if (res.failed()) {
                done.handle(Future.failedFuture(res.cause()));
                logger.debug("redis添加key失败: ", res.cause(), " key: " + key, " value: " + values.toString());
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
        });
    }

    public static void hset(String key, String field, String value, long seconds, Handler<AsyncResult<Long>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.hset(key, field, value, res -> {
            if (res.failed()) {
                logger.debug("redis添加key失败: ", res.cause(), " key: " + key, " field: " + field, " value: " + value.toString());
                done.handle(Future.failedFuture(res.cause()));
                close(redisClient);
            } else {
                setExpires(redisClient, key, seconds, res_exp -> {
                    close(redisClient);
                    done.handle(Future.succeededFuture(res.result()));
                });
            }

        });

    }

    public static void hset(String name, String field, String value, Handler<AsyncResult<Long>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.hset(name, field, value, res -> {
            close(redisClient);
            if (res.failed()) {
                logger.debug("redis设置key失败: ", res.cause(), " key: " + name, " field: " + field, " value: " + value.toString());
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }

        });

    }

    public static void hexists(String key, String field, Handler<AsyncResult<Long>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.hexists(key, field, res -> {
            close(redisClient);
            if (res.failed()) {
                logger.debug("redis判断是否存在key失败: ", res.cause(), " key: " + key);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
        });

    }

    public static void exists(String key, Handler<AsyncResult<Long>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.exists(key, res -> {
            if (res.failed()) {
                logger.debug("redis判断是否存在key失败: ", res.cause(), " key: " + key);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void get(String key, Handler<AsyncResult<String>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.get(key, res -> {
            if (res.failed()) {
                logger.debug("redis获取key失败: ", res.cause(), " key: " + key);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void hget(String key, String field, Handler<AsyncResult<String>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.hget(key, field, res -> {
            if (res.failed()) {
                logger.debug("redis获取key失败: ", res.cause(), " key: " + key, " field: " + field);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void hgetall(String key, Handler<AsyncResult<JsonObject>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.hgetall(key, res -> {
            if (res.failed()) {
                logger.debug("redis获取key失败: ", res.cause(), " key: " + key);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void hgetallWithoutClose(RedisClient redisClient, String key, Handler<AsyncResult<JsonObject>> done) {
        redisClient.hgetall(key, res -> {
            if (res.failed()) {
                logger.debug("redis获取key失败: ", res.cause(), " key: " + key);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
        });
    }


    public static void geoadd(String key, double lonitude, double latitude, String member, Handler<AsyncResult<Long>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.geoadd(key, lonitude, latitude, member, res -> {
            if (res.failed()) {
                logger.error("redis新增geo失败: ", res.cause(), " key: " + key, " lonitude: " + lonitude, " latitude: " + latitude);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);

        });
    }

    public static void geopos(String key, String member, Handler<AsyncResult<JsonArray>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.geopos(key, member, res -> {
            close(redisClient);
            if (res.failed()) {
                logger.error("redis 获取geopos失败: ", res.cause(), " key: " + key, " member: " + member);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
        });
    }

    public static void georadius(String key, double lat, double lon, double radius, GeoUnit unit, Handler<AsyncResult<JsonArray>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        GeoRadiusOptions options = new GeoRadiusOptions();
        options.setWithDist(true);
        options.setWithCoord(true);
        redisClient.georadiusWithOptions(key, lat, lon, radius, unit, options, res -> {
            if (res.failed()) {
                logger.error("redis 获取georadius失败: ", res.cause(), " lat: " + lat, " lon: " + lon);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void georadiusbymember(String key, String uid, double radius, GeoUnit unit, Handler<AsyncResult<JsonArray>> done) {

        RedisClient redisClient = RedisConnector.getRedisClient();
        GeoRadiusOptions options = new GeoRadiusOptions();
        options.setWithDist(true);
        options.setWithCoord(true);
        options.setCount(200L);
        redisClient.georadiusbymemberWithOptions(key, uid, radius, unit, options, res -> {

            if (res.failed()) {
                logger.error("redis 获取georadiusbymember失败: ", res.cause(), " uid: " + uid, " radius: " + radius);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void geodist(String key, String member1, String member2, Handler<AsyncResult<String>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.geodist(key, member1, member2, res -> {
            if (res.failed()) {
                logger.error("redis geodist: ", res.cause(), " member1: " + member1, " member2: " + member2);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void lpush(String key, String value, Handler<AsyncResult<Long>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.lpush(key, value, res -> {
            if (res.failed()) {
                logger.error("redis lpush: ", res.cause(), " key: " + key, "value: " + value);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture());
            }
        });
    }

    public static void lpopall(String key, Handler<AsyncResult<JsonArray>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.llen(key, lenRes -> {
            if (lenRes.failed()) {
                logger.error("redis llen: ", lenRes.cause(), " key: " + key);
                done.handle(Future.failedFuture(lenRes.cause()));
            } else {
                redisClient.lrange(key, 0, lenRes.result(), lrangeRes -> {
                    if (lrangeRes.failed()) {
                        logger.error("redis lrange: ", lrangeRes.cause(), " key: " + key);
                        done.handle(Future.failedFuture(lrangeRes.cause()));
                    }else{
                        redisClient.del(key,delRes->{
                            close(redisClient);
                            done.handle(Future.succeededFuture(lrangeRes.result()));
                        });
                    }
                });
            }
        });
    }

    public static void zrem(String key, String member, Handler<AsyncResult<Long>> done) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.zrem(key, member, res -> {
            if (res.failed()) {
                logger.error("redis zrem失败: ", res.cause(), " key: " + key, "member: " + member);
                done.handle(Future.failedFuture(res.cause()));
            } else {
                done.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    /**
     * 如果redisClient不为null , 不会自动关闭redisClient连接;否则需要自动关闭连接.
     *
     * @param redisClient
     * @param key
     * @param seconds
     * @param done
     */
    public static void setExpires(RedisClient redisClient, String key, long seconds, Handler<AsyncResult<Long>> done) {
        boolean flag = false;
        if (redisClient == null) {
            redisClient = RedisConnector.getRedisClient();
            flag = true;
        }
        boolean realFlag = flag;
        RedisClient rc = redisClient;
        rc.expire(key, seconds, res -> {
            if (res.succeeded()) {
                done.handle(Future.succeededFuture(res.result()));
            } else {
                logger.error("redis del失败: ", res.cause(), " key: " + key);
                done.handle(Future.failedFuture(res.cause()));
            }
            if (realFlag) {
                close(rc);
            }
        });
    }

    public static void delete(String key, Handler<AsyncResult<Long>> done) {
        setExpires(null, key, 0L, res -> {
            if (res.failed()) {
                done.handle(Future.failedFuture(res.cause()));
            } else if (res.result() == 1L) {
                done.handle(Future.succeededFuture(res.result()));
            } else {
                RedisClient redisClient = RedisConnector.getRedisClient();
                redisClient.del(key, res_del -> {
                    if (res_del.succeeded()) {
                        done.handle(Future.succeededFuture(res_del.result()));
                    } else {
                        logger.error("redis del失败: ", res_del.cause(), " key: " + key);
                        done.handle(Future.failedFuture(res_del.cause()));
                    }
                    close(redisClient);
                });
            }
        });
    }

    public static void incr(String key, Handler<AsyncResult<Long>> handler) {
        RedisClient redisClient = RedisConnector.getRedisClient();
        redisClient.incr(key, res -> {
            if (res.failed()) {
                logger.error("redis incr: ", res.cause(), " key: " + key);
                handler.handle(Future.failedFuture(res.cause()));
            } else {
                handler.handle(Future.succeededFuture(res.result()));
            }
            close(redisClient);
        });
    }

    public static void close(RedisClient redisClient) {
        redisClient.close(res_close -> {
            if (res_close.failed()) {
                logger.error("RedisClient close fail");
            } else {
                logger.debug("RedisClient close success");
            }
        });
    }

    public static class transaction {
        RedisClient redisClient = null;
        RedisTransaction tran = null;

        public transaction() {
            this.redisClient = RedisConnector.getRedisClient();
            this.tran = redisClient.transaction();
        }

        public void start(String key, Handler<AsyncResult<Void>> done) {
            tran.watch(key, res -> {
                if (res.failed()) {
                    done.handle(Future.failedFuture(new AppException(ResponseUtils.FAIL, "Redis failure")));
                } else {
                    tran.multi(mulRes -> {
                        if (mulRes.failed()) {
                            done.handle(Future.failedFuture(new AppException(ResponseUtils.FAIL, "Redis failure")));
                        } else {
                            done.handle(Future.succeededFuture());
                        }
                    });
                }
            });
        }

        public void end(Handler<AsyncResult<JsonArray>> done) {
            tran.exec(res -> {
                if (res.failed()) {
                    done.handle(Future.failedFuture(new AppException(ResponseUtils.FAIL, "Redis failure")));
                } else {
                    done.handle(Future.succeededFuture(res.result()));
                }
                close();
            });
        }

        public void hmset(String key, JsonObject values, Handler<AsyncResult<Void>> done) {
            tran.hmset(key, values, res -> {
                if (res.failed()) {
                    done.handle(Future.failedFuture(new AppException(ResponseUtils.FAIL, "Redis failure")));
                } else {
                    done.handle(Future.succeededFuture());
                }
            });
        }

        public void hset(String key, String field, String value, Handler<AsyncResult<Void>> done) {
            tran.hset(key, field, value, res -> {
                if (res.failed()) {
                    done.handle(Future.failedFuture(new AppException(ResponseUtils.FAIL, "Redis failure")));
                } else {
                    done.handle(Future.succeededFuture());
                }
            });
        }

        public void hdel(String key, String field, Handler<AsyncResult<Void>> done) {
            tran.hdel(key, field, res -> {
                if (res.failed()) {
                    done.handle(Future.failedFuture(new AppException(ResponseUtils.FAIL, "Redis failure")));
                } else {
                    done.handle(Future.succeededFuture());
                }
            });
        }

        public void discard() {
            tran.discard(res -> {
                if (res.failed()) {
                    logger.error("transaction discard fail");
                } else {
                    logger.debug("transaction discard success");
                }
                close();
            });
        }

        public void close() {
            redisClient.close(res_close -> {
                if (res_close == null) {
                    logger.error("RedisClient close fail");
                } else {
                    logger.debug("RedisClient close success");
                }
            });
        }
    }
}
