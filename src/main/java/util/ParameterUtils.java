package util;

import exception.AppException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by robot on 2017/10/10.
 */
public class ParameterUtils {

    /**
     * 封装必需的参数，如有缺少，直接抛异常给failureHandler
     *
     * @param routingContext routingContext
     * @param params         不定参数
     * @return 由参数和参数值组成的jsonObject
     */
    public static JsonObject getStringParams(RoutingContext routingContext, String... params) {
        JsonObject jsonObject = new JsonObject();
        for (String param : params) {
            String value = getStringParam(routingContext, param);
            jsonObject.put(param, value);
        }
        return jsonObject;
    }

    /**
     * 获取单个String参数
     *
     * @param routingContext routingContext
     * @param param          参数名
     * @return 参数值
     */
    public static String getStringParam(RoutingContext routingContext, String param) {
        String value = routingContext.request().getParam(param);
        if (StringUtils.isBlank(value)) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Missing parameter " + param + ".");
        }
        return value;
    }

    /**
     * 封装多个Integer参数
     *
     * @param routingContext routingContext
     * @param params         不定参数
     * @return 由参数和参数值组成的jsonObject
     */
    public static JsonObject getIntegerParams(RoutingContext routingContext, String... params) {
        JsonObject jsonObject = new JsonObject();
        for (String param : params) {
            String value = routingContext.request().getParam(param);
            if (StringUtils.isBlank(value)) {
                throw new AppException(ResponseUtils.PARAM_ERROR, "Missing parameter " + param + ".");
            }
            try {
                jsonObject.put(param, Integer.valueOf(value));
            } catch (NumberFormatException e) {
                throw new AppException(ResponseUtils.PARAM_ERROR, "Invalid parameter " + param + ".");
            }
        }
        return jsonObject;
    }

    /**
     * 获取单个Integer参数
     *
     * @param routingContext routingContext
     * @param param          参数名
     * @return 参数值
     */
    public static Integer getIntegerParam(RoutingContext routingContext, String param) {

        Integer integerValue;
        String value = routingContext.request().getParam(param);
        if (StringUtils.isBlank(value)) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Missing parameter " + param + ".");
        }
        try {
            integerValue = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Invalid parameter " + param + ".");
        }
        return integerValue;
    }

    /**
     * 获取单个Double参数（最多为小数点后2位 Rounding unnecessary）
     *
     * @param routingContext routingContext
     * @param param          参数名
     * @return 参数值
     */
    public static Double getDoubleParamRoundingUnnecessaryScaleOfTwo(RoutingContext routingContext, String param) {
        double doubleValue;
        String value = routingContext.request().getParam(param);
        if (StringUtils.isBlank(value)) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Missing parameter " + param + ".");
        }
        try {
            BigDecimal bd = new BigDecimal(value);
            doubleValue = bd.setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue();
            if (doubleValue == Double.POSITIVE_INFINITY || doubleValue == Double.NEGATIVE_INFINITY) {
                throw new AppException(ResponseUtils.PARAM_ERROR, "Invalid parameter " + param + ".");
            }
        } catch (Exception e) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Invalid parameter " + param + ".");
        }
        return doubleValue;
    }

    /**
     * 将请求 body 中的内容视为 json object 处理
     * <p>
     * 需满足条件：
     * - 请求带有<code>Content-Type</code>
     * - 可被转化为 JsonObject
     * - 转换后 size() != 0
     *
     * @param routingContext routingContext
     * @return 参数值
     */
    public static JsonObject getJsonObjectParamFromRawBody(RoutingContext routingContext) {
        if (!routingContext.request().headers().get("Content-Type").equalsIgnoreCase("application/json")) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Missing header Content-Type.");
        }
        JsonObject jsonObjectParam;
        try {
            jsonObjectParam = routingContext.getBodyAsJson();
        } catch (Exception e) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Invalid json object body.");
        }
        if (jsonObjectParam == null || jsonObjectParam.size() == 0) {
            throw new AppException(ResponseUtils.PARAM_ERROR, "Missing json object body.");
        }
        return jsonObjectParam;
    }

    /**
     * 获取单个Timestamp参数并且转化为Date类型
     *
     * @param routingContext routingContext
     * @param param          参数名
     * @return 参数值
     */
    public static Date getTimestampParam(RoutingContext routingContext, String lasttime) {
        Date lastTime = new Date();
        String time = routingContext.request().getParam("lasttime");
        if (StringUtils.isNotBlank(time)) {
            try {
                lastTime = new Date(Long.valueOf(time));
                return lastTime;
            } catch (Exception e) {
                throw new AppException(ResponseUtils.PARAM_ERROR, "not Timestamp");
            }
        } else {
            return lastTime;
        }
    }

}
