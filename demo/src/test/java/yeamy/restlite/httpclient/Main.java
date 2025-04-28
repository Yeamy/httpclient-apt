package yeamy.restlite.httpclient;

import org.apache.commons.lang3.time.FastDateFormat;

import java.io.IOException;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        final FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");
        final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
        final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        String time = "10:22:33";
        int times = 10000000;
        long l = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            DateTimeUtil.parseTime(time);
        }
        long tmp = System.currentTimeMillis();
        long t1 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            TIME_FORMAT.parse(time);
        }
        tmp = System.currentTimeMillis();
        long t2 = tmp - l;
        l = tmp;
        String date = "2025-04-24";
        for (int i = 0; i < times; i++) {
            DateTimeUtil.parseDate(date);
        }
        tmp = System.currentTimeMillis();
        long t3 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            DATE_FORMAT.parse(date);
        }
        tmp = System.currentTimeMillis();
        long t4 = tmp - l;
        l = tmp;
        String dateTime = "2025-04-24 11:00:33";
        for (int i = 0; i < times; i++) {
            DateTimeUtil.parseDateTime(dateTime);
        }
        tmp = System.currentTimeMillis();
        long t5 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            DATE_TIME_FORMAT.parse(dateTime);
        }
        tmp = System.currentTimeMillis();
        long t6 = tmp - l;
        l = tmp;

        System.out.println("t1 = " + t1 + "  t2 = " + t2 + " t3 = " + t3 + "  t4 = " + t4 + " t5 = " + t5 + "  t6 = " + t6);
    }

}
