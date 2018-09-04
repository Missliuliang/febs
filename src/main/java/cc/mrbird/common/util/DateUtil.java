package cc.mrbird.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static Date getNowDate(){
        return  new Date();
    }

    public static String getDate(String  format){
        Calendar calendar=Calendar.getInstance();
        DateFormat dateFormat=new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return  dateFormat.format(calendar.getTime());
    }

    public static String getDateFormat(Date date , String format){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
        return  simpleDateFormat.format(date);
    }

    public static  boolean isTodayWeekend(){
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_WEEK);//获取当前日期星期，英国算法(周日为一周第一天)
        return day==7 || day==1 ;
    }

    /**
     * 获得间隔日期（主要是间隔N周、间隔N天）
     *
     * @param specifiedStrDate 指定日期
     * @param dateForamtType   指定日期格式
     * @param intervalNum      间隔数（周或者天）
     * @param calenderParam    指定修改日期格式的属性
     *                         Calendar.WEEK_OF_MONTH（周）或者Calendar.DAY_OF_MONTH（天）
     * @return String
     */
    public static  String getIntervalStrDate(String specifiedStrDate ,String format,
                                             int intervalNum ,int calendarParam ){
        if (specifiedStrDate==null || "".equals(specifiedStrDate.trim())){
            return  null;
        }
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(format);
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(DateUtil.turnStrDateToJavaUtilDate(specifiedStrDate,format));
        calendar.add(intervalNum,calendarParam);
        return  simpleDateFormat.format(calendar.getTime());
    }
    public static java.sql.Date turnDateToSqlDate(String strDate , String format){
        if (strDate==null || strDate.trim().equals(""))return null ;
        return  new java.sql.Date(turnStrDateToJavaUtilDate(strDate,format).getTime());
    }

    /**
     * 按指定的字符串格式将字符串型日期转化为Date型日期
     *
     * @param dateFormatType "yyyy-MM-dd" 或者 "yyyy-MM-dd hh:mm:ss"
     * @return Date型日期
     * @param strDate 字符型日期
     */
    public  static Date turnStrDateToJavaUtilDate(String strDate ,  String formate){
        Date javaUtilDate=null ;
        SimpleDateFormat dateFormat=new SimpleDateFormat(formate);
        if (strDate!=null && !strDate.equals("") && !strDate.equals(formate)){
            try {
                javaUtilDate= dateFormat.parse(strDate);
            }catch (ParseException p){
                p.printStackTrace();
            }
        }
        return  javaUtilDate;
    }

    /**
     * 格式化CST（ Thu Aug 27 18:05:49 CST 2015 ）格式字符串
     *
     * @param date   date
     * @param format format
     * @return String
     * @throws ParseException ParseException
     */
    public static String formatCSTTime(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date d = sdf.parse(date);
        return DateUtil.getDateFormat(d, format);
    }

}
