package util;

import io.vertx.core.json.JsonObject;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ConfigUtils {


    // 静态变量
    private static JsonObject config;

    /**
     * 加载配置（初始化或热更新）
     *
     * @param filePath   参数路径
     * @param production 是否是 prd 模式
     * @throws Exception 异常
     */
    public static void initConfig(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
        StringBuilder stringBuilder = new StringBuilder();
        while (inputStreamReader.ready()) {
            stringBuilder.append((char) inputStreamReader.read());
        }
        inputStreamReader.close();
        fileInputStream.close();
        config = new JsonObject(stringBuilder.toString());
    }

    public static String getString(String key) {
        try {
            return getString(config, key);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 获取整型配置项（支持多级配置，通过“.”来识别）
     * 取不到抛出 RuntimeException，同时记录参数key及原因到 error.log
     *
     * @param key key
     * @return value
     */
    public static Integer getInteger(String key) {
        try {
            return getInteger(config, key);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 递归获取字符串型配置（不对外暴露）
     *
     * @param config 配置 JsonObject
     * @param key    key
     * @return value
     */
    private static String getString(JsonObject config, String key) {
        if (key.contains(".")) {
            int firstDot = key.indexOf(".");
            String superKey = key.substring(0, firstDot);
            String subKey = key.substring(firstDot + 1, key.length());

            if (config.containsKey(superKey)) {
                return getString(config.getJsonObject(superKey), subKey);
            } else {
                throw new RuntimeException("Try to get a nonexistent configuration item.");
            }
        } else {
            if (config.containsKey(key)) {
                try {
                    return config.getString(key);
                } catch (ClassCastException e) {
                    throw new RuntimeException("Wrong type of configuration item.");
                }
            } else {
                throw new RuntimeException("Try to get a nonexistent configuration item.");
            }
        }
    }

    /**
     * 递归获取整型配置（不对外暴露）
     *
     * @param config 配置 JsonObject
     * @param key    key
     * @return value
     */
    private static Integer getInteger(JsonObject config, String key) {
        if (key.contains(".")) {
            int firstDot = key.indexOf(".");
            String superKey = key.substring(0, firstDot);
            String subKey = key.substring(firstDot + 1, key.length());

            if (config.containsKey(superKey)) {
                return getInteger(config.getJsonObject(superKey), subKey);
            } else {
                throw new RuntimeException("Try to get a nonexistent configuration item.");
            }
        } else {
            if (config.containsKey(key)) {
                try {
                    return config.getInteger(key);
                } catch (ClassCastException e) {
                    throw new RuntimeException("Wrong type of configuration item.");
                }
            } else {
                throw new RuntimeException("Try to get a nonexistent configuration item.");
            }
        }
    }



}
