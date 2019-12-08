import java.util.*;
import java.text.*;

public class JavaUtil {
    
    void date() {
        // tag::date[]
        var date = new Date(System.currentTimeMillis());
        date.toString(); // Tue Dec 03 22:08:57 CET 2019
        // uses -Duser.timezone 
        date.setTime(1231211251L); // Mutable!
        // + round about 350 @Deprecated Methods 
        // end::date[]
    }
   
    void calendar() {
        // tag::calendar[]
        var cal = Calendar.getInstance(TimeZone.getDefault());
        /* Interface that uses many -Duser.* properties for
         * selecting an implementation out of e.g.:
         * GregorianCalendar, 
         * BuddhistCalendar or 
         * JapaneseImperialCalendar */
        cal.toString(); 
        // java.util.GregorianCalendar[time=1575409375587,areFieldsSet=true,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="Europe/Berlin",offset=3600000,dstSavings=3600000,useDaylight=true,transitions=143,lastRule=java.util.SimpleTimeZone[id=Europe/Berlin,offset=3600000,dstSavings=3600000,useDaylight=true,startYear=0,startMode=2,startMonth=2,startDay=-1,startDayOfWeek=1,startTime=3600000,startTimeMode=2,endMode=2,endMonth=9,endDay=-1,endDayOfWeek=1,endTime=3600000,endTimeMode=2]],firstDayOfWeek=2,minimalDaysInFirstWeek=4,ERA=1,YEAR=2019,MONTH=11,WEEK_OF_YEAR=49,WEEK_OF_MONTH=1,DAY_OF_MONTH=3,DAY_OF_YEAR=337,DAY_OF_WEEK=3,DAY_OF_WEEK_IN_MONTH=1,AM_PM=1,HOUR=10,HOUR_OF_DAY=22,MINUTE=42,SECOND=55,MILLISECOND=587,ZONE_OFFSET=3600000,DST_OFFSET=0]
        cal.set(2019, 10, 12, 18, 30, 0); // Yeah!
        cal.setTimeInMillis(1231211251L); // What?
        // end::calendar[]
    }

    void format() throws Exception{
        // tag::format[]
        var sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        Date toFormat = Calendar.getInstance().getTime();
        String text = sdf.format(toFormat); 
        // 2019-12-03T22:50:42.562+01:00
        
        Date parsed = sdf.parse(text);
        var cal = Calendar.getInstance();
        cal.setTime(parsed);
        // end::format[]
    }

    void calculate() {
        // tag::calculate[]
        var today = new Date();
        var tomorrow = new Date(today.getTime() + 1000 * 60 * 60 * 24);

        var now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_WEEK_IN_MONTH, 1); // What?
        now.add(Calendar.HOUR, -2); // All right
        now.add(Calendar.HOUR_OF_DAY, 3); // ehhh?
        // BUT: now isn't now anymore!
        var sometime = (Calendar) now.clone();
        // ...
        // end::calculate[]
    }

    public static void main(String... args) throws Exception {
        var javaUtil = new JavaUtil();
        javaUtil.date();
        javaUtil.calendar();
        javaUtil.format();
    }
}