package util;

import bean.Auth;
import exception.AppException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;

public class BasicUtils {
    static Random random = new Random();

     public static String createRandomId() {
        String order_id = String.valueOf(System.currentTimeMillis()) + random.nextInt(10) + random.nextInt(10);
        return order_id;
    }


    /**
     * 获取登录token
     *
     * @return
     */
    public static String createToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        return token;
    }


    //不舍入
    public static double formatNum(double lag) {
        BigDecimal bd = new BigDecimal(String.valueOf(lag));
        BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_DOWN);
        return Double.parseDouble(bd2.toString());
    }

    public static double sum(String d1, String d2) {
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.add(bd2).doubleValue();
    }

    //相减
    public static double sub(String d1, String d2) {
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.subtract(bd2).doubleValue();
    }

    //相乘
    public static double mul(String d1, String d2) {
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.multiply(bd2).doubleValue();
    }

    //除法
    public static double div(String d1, String d2) {
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.divide(bd2, 2, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     *
     * @param message
     * @return
     */
    public static String md5(String message) {
        if (message == null) {
            throw new NullPointerException("pwd: is " + message);
        }
        try {

            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(new String(message.getBytes(), "UTF-8").getBytes("UTF-8"));
            byte b[] = md.digest();
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                int val = ((int) b[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            throw new AppException(ResponseUtils.SERVER_FAIL, e.getMessage());
        }
    }
}