package util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValDataUtils {

    //Mt号是否被修改
    public static boolean isModified(String mtNumber) {
        if (StringUtils.isBlank(mtNumber)) {
            return false;
        }
        return !Pattern.matches("^mt_[a-zA-Z0-9]{3,9}", mtNumber);
    }

    public static boolean isMtNumber(String mtNumber) {
        if (StringUtils.isBlank(mtNumber)) {
            return false;
        }
        return Pattern.matches("[a-zA-Z][a-zA-Z0-9]{5,11}$", mtNumber);
    }

    public static boolean isPhone(String phone) {
        return Pattern.matches("^1\\d{10}$", phone);
    }

    public static boolean isUid(String uid) {
        if (StringUtils.isBlank(uid)) {
            return false;
        }
        return Pattern.matches("^[0-9]*$", uid);
    }

    public static boolean isVcode(String vcode) {
        if (StringUtils.isBlank(vcode)) {
            return false;
        }
        return Pattern.matches("^\\d{6}$", vcode);
    }

    public static boolean isUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        return Pattern.matches("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]", url);
    }

    //todo 不做具体的规则判定
    public static boolean checkOSSRelativeUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isMail(String mail) {
        if (StringUtils.isBlank(mail)) {
            return false;
        }
        return Pattern.matches("^[a-z_0-9.-]{1,64}@([a-z0-9-]{1,200}.){1,5}[a-z]{1,6}$", mail);
    }

    public static boolean isDate(String date) {
        if (StringUtils.isBlank(date)) {
            return false;
        }
        String regx = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        return Pattern.matches(regx, date);
    }

    public static boolean isLonLat(String longitude, String latitude) {
        boolean flag = false;
        if (StringUtils.isBlank(longitude) || StringUtils.isBlank(latitude)) {
            return flag;
        }
        flag = (Pattern.matches("-?(180(.0{06})?|(1[0-7][0-9]|[1-9]?[0-9])(.[0-9]{01,}))$", longitude) && Pattern.matches("^-?(85(.0{06})?|[1-8]?[0-9](.[0-9]{01,})?)$", latitude));
        if (flag) {
            return !(Double.parseDouble(longitude) > 180 || Double.parseDouble(longitude) < -180 || Double.parseDouble(latitude) > 85 || Double.parseDouble(latitude) < -85);
        }
        return flag;
    }

    public static boolean isPaymentPasswd(String passwd) {
        if (StringUtils.isBlank(passwd)) {
            return false;
        }
        return Pattern.matches("^[0-9]{6}$", passwd);
    }

    public static boolean isPassword(String password) {
        boolean flag = false;
        if (StringUtils.isNotBlank(password)) {
            flag = password.matches("^(?=.*\\d)(?=.*[a-zA-Z])(?!.*\\s).{8,16}$");
        }
        return flag;
    }

    public static boolean isQrcode(String qrcode) {
        boolean flag = false;
        if (StringUtils.isNotBlank(qrcode)) {
            flag = qrcode.matches("\\w{39}");
        }
        return flag;
    }

    public static boolean isXRandom(String XRandom) {
        boolean flag = false;
        if (StringUtils.isNotBlank(XRandom)) {
            flag = XRandom.matches("^[0-9a-zA-Z]{8,16}$");
        }
        return flag;
    }

    public static boolean isPhoneSecurityStatus(String status) {
        return StringUtils.isNotBlank(status) && (status.equals("on") || status.equals("off"));
    }

    public static boolean isAppPlatform(String platform) {
        return StringUtils.isNotBlank(platform) && (platform.equalsIgnoreCase("android") || platform.equalsIgnoreCase("ios"));
    }

    public static boolean isDeviceBrand(String brand) {
        return StringUtils.isNotBlank(brand) && brand.equalsIgnoreCase("huawei");
    }

}
