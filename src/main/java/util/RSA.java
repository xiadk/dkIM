package util;

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.security.KeyFactory.getInstance;

/**
 * Created by xox on 17-9-8.
 */
public class RSA {
    /**
     * 指定key的大小
     */
    private static int KEYSIZE = 1024;
    static String CHAR_ENCODING = "UTF-8";
    static String RSA_ALGORITHM = "RSA";
    static String privateKey = null;
    static String publicKey = null;
    /*static String rsa_private_key_path = ConfigUtils.getString("rsa_private_key_path");
    static String rsa_public_key_path = ConfigUtils.getString("rsa_public_key_path");*/
    static String rsa_private_key_path;
    static String rsa_public_key_path;
    /**
     * 生成密钥对
     */
    public static Map<String, String> generateKeyPair() throws Exception {
        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        kpg.initialize(KEYSIZE, sr);
        /** 生成密匙对 */
        KeyPair kp = kpg.generateKeyPair();
        /** 得到公钥 */
        Key publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String pub = new String(Base64.getEncoder().encode(publicKeyBytes),
                CHAR_ENCODING);
        /** 得到私钥 */
        Key privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String pri = new String(Base64.getEncoder().encode(privateKeyBytes),
                CHAR_ENCODING);

        Map<String, String> map = new HashMap<String, String>();
        map.put("publicKey", pub);
        map.put("privateKey", pri);
        RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
        BigInteger bint = rsp.getModulus();
        byte[] b = bint.toByteArray();
        byte[] deBase64Value = Base64.getEncoder().encode(b);
        String retValue = new String(deBase64Value);
        map.put("modulus", retValue);
        return map;
    }

    /**
     * 加密方法 source： 源数据
     */
    public static String encrypt(String source, String publicKey)
            throws Exception {
        Key key = getPublicKey(publicKey);
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        return new String(Base64.getEncoder().encode(b1),
                CHAR_ENCODING);
    }

    /**
     * 解密算法 cryptograph:密文
     */
    public static String decrypt(String cryptograph, String privateKey)
            throws Exception {
        Key key = getPrivateKey(privateKey);
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] b1 = Base64.getDecoder().decode(cryptograph.getBytes());
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(key.getBytes()));
        KeyFactory keyFactory = getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String sign(String content, String privateKey) {
        String charset = CHAR_ENCODING;
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.getDecoder().decode(privateKey.getBytes()));
            KeyFactory keyf = getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature.getInstance("SHA1WithRSA");

            signature.initSign(priKey);
            signature.update(content.getBytes(charset));

            byte[] signed = signature.sign();

            return new String(Base64.getEncoder().encode(signed));
        } catch (Exception e) {

        }

        return null;
    }

    public static boolean checkSign(String content, String sign, String publicKey) {
        try {
            KeyFactory keyFactory = getInstance("RSA");
            byte[] encodedKey = Base64.getDecoder().decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            Signature signature = Signature
                    .getInstance("SHA1WithRSA");

            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));

            boolean bverify = signature.verify(Base64.getDecoder().decode(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 读取文件获取公钥和私钥
     *
     * @param path
     * @return
     */
    public static String read(String path) {
        File file = new File(path);
        if (!file.exists()) {
            new FileNotFoundException();
        }
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file));
            int len = 1024;
            byte[] bytes = new byte[len];
            int end = -1;
            StringBuffer sb = new StringBuffer();
            while ((end = is.read()) != -1) {
                /*sb.append(bytes);*/
                if (end != 10)
                    sb.append((char) end);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取私钥
     *
     * @return
     */
    public static String getPrivateKey() {
        if (privateKey == null) {
            privateKey = read(rsa_private_key_path);//Config.private_key);
        }
        return privateKey;
    }

     /**
     * 获取公钥
     *
     * @return
     */
    public static String getPublicKey() {
        if (publicKey == null) {
            publicKey = read(rsa_public_key_path);//Config.private_key);
        }
        return publicKey;
    }

    public static void main(String[] args) {
        try {
            /*String pub = read(rsa_public_key_path);//Config.public_key);
            String pri = read(rsa_private_key_path);//Config.private_key);
            String test = "123456al";
            String en = encrypt(test, pub);
            String de = decrypt(en, pri);
            System.out.println(de);*/
            /*Map<String, String> m = generateKeyPair();
            String test = "test";
            String pub = m.get("publicKey");
            String prr = m.get("privateKey");
            pub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApWlge6p6sm6LXFYZ00hd2xjo4RjbppKF+XPJqf/Z/wDGxZ08BV15prwW6w/7phcQTcpimBxP+JoLfpEjFJVnobGrDJ6C7TDaJHrBrWVKqit6ag78G8a0x/Lovl3MJziHnr4p7YNPfpJxO9gcOoKmp9Mzn2Ud9VUF2m3oq1/jOUnHKZRHs06kf1p4m2kB2EoPjiq0ge5eCRAIwA4KIvuHbO0SwXtjP6G2v8EjWVQz4fffdiNlSW4jsEP9LFEHVsCO80zw5shME0ZSteEYdtjVdPGmTdyjHFf+6o7HAjKRRABJz234Pldz3eMtzNfPZx+CtJpa+cUiQghwr/c1mdaLLQIDAQAB";
            String en = encrypt(test,pub);
            String pri = read(System.getProperty("user.home")+PropertiesUtil.getProperty("private"));
            String de = decrypt(en , pri);
            System.out.println(de);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte b[] = md.digest(str.getBytes());
            BASE64Encoder base64 = new BASE64Encoder();
            return base64.encode(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
