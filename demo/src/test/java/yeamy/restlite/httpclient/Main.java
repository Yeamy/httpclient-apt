package yeamy.restlite.httpclient;

import org.apache.commons.lang3.time.FastDateFormat;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        final FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");
        final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
        final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        final DateTimeFormatter TIME_FORMAT2 = DateTimeFormatter.ofPattern("HH:mm:ss");
        final DateTimeFormatter DATE_FORMAT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DateTimeFormatter DATE_TIME_FORMAT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String time = "10:22:33";
        String date = "2025-04-24";
        String dateTime = "2025-04-24 11:00:33";


        System.out.println(LocalTime.parse(time, TIME_FORMAT2));
        System.out.println(LocalDate.parse(date, DATE_FORMAT2));
        System.out.println(LocalDateTime.parse(dateTime, DATE_TIME_FORMAT2));

        int times = 1_000_000;
        long l = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            DateParser.parseTime(time);
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
        for (int i = 0; i < times; i++) {
            LocalDate.parse(date, DATE_FORMAT2);
        }
        tmp = System.currentTimeMillis();
        long t3 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            DateParser.parseDate(date);
        }
        tmp = System.currentTimeMillis();
        long t4 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            DATE_FORMAT.parse(date);
        }
        tmp = System.currentTimeMillis();
        long t5 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            LocalTime.parse(time, TIME_FORMAT2);
        }
        tmp = System.currentTimeMillis();
        long t6 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            DateParser.parseDateTime(dateTime);
        }
        tmp = System.currentTimeMillis();
        long t7 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            DATE_TIME_FORMAT.parse(dateTime);
        }
        tmp = System.currentTimeMillis();
        long t8 = tmp - l;
        l = tmp;
        for (int i = 0; i < times; i++) {
            LocalDateTime.parse(dateTime, DATE_TIME_FORMAT2);
        }
        tmp = System.currentTimeMillis();
        long t9 = tmp - l;
        l = tmp;

        System.out.println("t1 = " + t1 + "  t2 = " + t2 + " t3 = " + t3
                + "\nt4 = " + t4 + " t5 = " + t5 + "  t6 = " + t6
                + "\nt7 = " + t7 + " t8 = " + t8 + "  t9 = " + t9
        );
    }

}
