package util;

import bean.Auth;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verticle.Run;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 对返回给客户端的格式封装
 * Created by jiangxb on 17-9-6.
 */
public class ResponseUtils {

    public final static String SUCCESS = "0200"; // 操作成功
    public final static String PARAM_ERROR = "0400"; // 参数错误
    public final static String FAIL = "0414"; // 操作失败
    public final static String PWD_ERROR = "1406";//密码错误
    public final static String SERVER_FAIL = "0500"; // 服务器错误
    public final static String REQUEST_EXIST = "1501"; // 请求保存的数据已存在
    public final static String REQUEST_NOT_EXIST = "1502"; // 请求数据不存在
    public final static String AUTH_ERROR = "0555"; // token 失效
    private final static String RETURN_CODE = "msgId";
    private static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

    private static String success() {
        return new JsonObject().put(RETURN_CODE, SUCCESS).toString();
    }

    private static String success(JsonObject jsonObject) {
        return jsonObject.put(RETURN_CODE, SUCCESS).toString();
    }

    private static String success(String arg1, Object val1) {
        Map<String, Object> result = new HashMap<>();
        result.put(RETURN_CODE, SUCCESS);
        result.put(arg1, val1);
        return Json.encode(result);
    }

    private static String fail() {
        return new JsonObject().put(RETURN_CODE, FAIL).toString();
    }

    private static String fail(String arg1, String val1) {
        Map<String, String> result = new HashMap<>();
        result.put(RETURN_CODE, FAIL);
        result.put(arg1, val1);
        return Json.encode(result);
    }

    /**
     * 返回message信息,不做任何更改
     *
     * @param routingContext  routingContext
     * @param responseContent response content
     */
    public static void response(RoutingContext routingContext, String responseContent) {
        HttpServerRequest request = routingContext.request();
        String remoteAddress = request.remoteAddress().toString();
        String method = request.method().toString();
        String uri = request.uri();
        String requestParams = request.params().toString().replace("\n", "\t");
        Auth auth = routingContext.get("auth");
        System.out.println("OUT---url:"+uri+";method:"+method+";params:"+requestParams);
        logger.info("RESPONSE(uid:{} ip:{}): {} {} time:{} response: {}",
                auth == null ? 0 : auth.getUid(), remoteAddress, method, uri, TimeUtils.chargeDateToString(new Date()), responseContent);
        routingContext.response().end(responseContent);
    }

    /**
     * 返回成功
     *
     * @param routingContext routingContext
     */
    public static void responseSuccess(RoutingContext routingContext) {
        response(routingContext, success());
    }

    /**
     * 返回成功状态,携带一组key,value 信息
     *
     * @param routingContext routingContext
     * @param key            key
     * @param value          value
     */
    public static void responseSuccess(RoutingContext routingContext, String key, Object value) {
        response(routingContext, success(key, value));
    }

    /**
     * 返回成功状态,携带JsonObject 对象
     *
     * @param routingContext routingContext
     * @param values         values
     */
    public static void responseSuccess(RoutingContext routingContext, JsonObject values) {
        response(routingContext, success(values));
    }

    /**
     * 返回失败信息
     *
     * @param routingContext routingContext
     */
    public static void responseFail(RoutingContext routingContext) {
        response(routingContext, fail());
    }

    /**
     * 返回成功状态,携带一组key,value 信息
     *
     * @param routingContext routingContext
     * @param key            key
     * @param value          value
     */
    public static void responseFail(RoutingContext routingContext, String key, String value) {
        response(routingContext, fail(key, value));
    }

}
