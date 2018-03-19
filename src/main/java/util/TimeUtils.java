package util;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by wp on 17-9-4.
 */
public class TimeUtils {

    final static String timeRegex = "yyyy-MM-dd HH:mm:ss";
    final static String dateRegex = "yyyy-MM-dd";

    public static String getCurrentTime() {
        return chargeDateToString(new Date());
    }

    //todo 这个地方函数名...
    public static String getCurrentData() {
        return DateFormatUtils.format(new Date(), dateRegex);
    }

    public static String getAfterYearTime() {
        return DateFormatUtils.format(DateUtils.addYears(new Date(), 1), timeRegex);
    }

    public static String chargeDateToString(Date date) {
        return DateFormatUtils.format(date, timeRegex);
    }

    /**
     * todo 可能存在问题时间转换
     *
     * @param str
     * @return
     */
    public static Timestamp StringToDateTime(String str) {
        str = str.replace("T", " ");
        return Timestamp.valueOf(str);
    }

    public static Long birthdayDateToTimeStamp(String time) {

        try {
            return DateUtils.parseDate(time, dateRegex).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long StringDateToTimeStamp(String time) {

        try {
            return DateUtils.parseDate(timeRegex, time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String timestamp_beforTime(Date old_time, Date now_time) {

        long interval = (now_time.getTime() - old_time.getTime()) / 1000;
        if (interval < 60L) {
            if (interval == 0) {
                interval = 1;
            }
            return interval + "秒前";
        }
        if (interval < 3600L) {
            return interval / 60 + "分钟前";
        }
        LocalDateTime old_localDateTime = DateToLocalDateTime(old_time);
        LocalDateTime now_localDateTime = DateToLocalDateTime(now_time);

        Duration duration = Duration.between(old_localDateTime.toLocalDate().atTime(0, 0), now_localDateTime.toLocalDate().atTime(0, 0));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        if (duration.toDays() == 0) {
            return "今天 " + old_localDateTime.format(dtf);
        }
        if (duration.toDays() == 1) {
            return "昨天 " + old_localDateTime.format(dtf);
        }
        if (now_localDateTime.getYear() == old_localDateTime.getYear()) {
            dtf = DateTimeFormatter.ofPattern("MM-dd HH:mm");
            return old_localDateTime.format(dtf);
        }
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return old_localDateTime.format(dtf);
    }

    public static Long balanceMillisInToday() {
        LocalDateTime now_localDateTime = LocalDateTime.now();
        LocalDateTime tomorrow = now_localDateTime.plusDays(1).toLocalDate().atTime(0, 0);
        Duration duration = Duration.between(now_localDateTime, tomorrow);
        return duration.getSeconds();
    }

    //获取到下个月1号的时间(毫秒)
    public static Long balanceMillisToNextMonth() {
        LocalDateTime now_localDateTime = LocalDateTime.now();
        LocalDateTime nextMonth = now_localDateTime.plusMonths(1).withDayOfMonth(1).toLocalDate().atTime(0, 0);
        Duration duration = Duration.between(now_localDateTime, nextMonth);
        return duration.toMillis();
    }


    public static Long getSeconds(Date now_Date, Date old_Date) {
        LocalDateTime old_localDateTime = DateToLocalDateTime(old_Date);
        LocalDateTime now_localDateTime = DateToLocalDateTime(now_Date);
        Duration duration = Duration.between(old_localDateTime, now_localDateTime);
        return duration.getSeconds();
    }

    public static LocalDateTime DateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    //1509621076000 => 2017年11月
    public static String timestampToYearAndMonth(Long timestamp) {
        return DateFormatUtils.format(timestamp, "yyyy-MM");
    }
}
