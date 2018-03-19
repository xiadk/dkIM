package exception;

import io.vertx.core.json.JsonObject;

/**
 * Created by robot on 2017/10/11.
 */
public class AppException extends RuntimeException {

    /**
     * @param state      状态码
     * @param jsonObject 参数
     * @param message    备注
     */
    public AppException() {

    }

    public AppException(String state) {
        super(new JsonObject().put("msgId", state).encode());
    }

    public AppException(String state, String message) {
        super(new JsonObject().put("msgId", state).put("message", message).encode());
    }

    public AppException(String state, JsonObject jsonObject) {
        super(jsonObject.put("msgId", state).encode());
    }

    public AppException(String state, JsonObject jsonObject, String message) {
        super(jsonObject.put("msgId", state).put("message", message).encode());
    }

}
