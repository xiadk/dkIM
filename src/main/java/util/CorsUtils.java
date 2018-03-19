package util;

import io.vertx.core.http.HttpMethod;

import java.util.HashSet;
import java.util.Set;

public class CorsUtils {

    public static Set getCorsMethods() {

        Set set = new HashSet();
        set.add(HttpMethod.GET);
        set.add(HttpMethod.POST);
        set.add(HttpMethod.PUT);
        set.add(HttpMethod.DELETE);
        set.add(HttpMethod.OPTIONS);
        set.add(HttpMethod.HEAD);
        return set;
    }

    public static String getCorsHeader() {

        return "Content-Type, token, Access-Control-Allow-Headers, Authorization, X-Requested-With, " +
                "device, user-agent,withCredential,X-random";
    }
}
