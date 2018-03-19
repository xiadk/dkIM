package util;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

public class FormObjectUtils {
    public static String objectToString(Object obj){
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        String str = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            byte[] bytes = bos.toByteArray();
            str = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static Object stringToObjcet(String str){
        byte[]  bytes = str.getBytes();
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        Object object = null;
        try {
            bis = new ByteArrayInputStream(bytes);
             ois = new ObjectInputStream(bis);
            object = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                bis.close();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;

    }
}
