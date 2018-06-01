package util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Exrickx
 */
public class QiniuUtil {

    /**
     * 生成上传凭证，然后准备上传
     */
    private static String accessKey = "vE4qS1_PCT25dzaVB9ynIHmHvUHvW_zB_dqlB5SU";
    private static String secretKey = "JdbREEWbIS8P0XwPWmphed-1LYQ6PeQRkji1GdFK";
    private static String bucket = "dkstore";
    private static Auth auth = Auth.create(accessKey, secretKey);


    public static String getUpToken() {
        StringMap stringMap = new StringMap();
        stringMap.put("insertOnly", 1);
        return auth.uploadToken(bucket, null, 3600, stringMap);
    }

//    public static String getdownToken(){
////        auth.privateDownloadUrl()
//    }

    public static void main(String[] args){
        System.out.println(auth.privateDownloadUrl("http://p8go9rpgo.bkt.clouddn.com"));
    }
}
