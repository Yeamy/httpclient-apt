package yeamy.restlite.httpclient;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.TimeZone;

class DateParser {

    static Time parseTime(String time) throws IOException {
        try {
            char[] c = time.toCharArray();
            long ts = TimeZone.getDefault().getRawOffset()
                    + (c[0] - '0') * 36000L
                    + (c[1] - '0') * 3600L
                    + (c[3] - '0') * 600L
                    + (c[4] - '0') * 60L
                    + (c[6] - '0') * 10L
                    + (c[7] - '0');
            return new Time(ts);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private static Calendar parseDate(char[] c) {
        int y = (c[0] - '0') * 1000 + (c[1] - '0') * 100 + (c[2] - '0') * 10 + (c[3] - '0');
        int M = (c[5] - '0') * 10 + (c[6] - '0') - 1;
        int d = (c[8] - '0') * 10 + (c[9] - '0');
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, M);
        calendar.set(Calendar.DATE, d);
        return calendar;
    }

    static Date parseDate(String time) throws IOException {
        try {
            char[] c = time.toCharArray();
            Calendar calendar = parseDate(c);
            return new Date(calendar.getTimeInMillis());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    static DateTime parseDateTime(String time) throws IOException {
        try {
            char[] c = time.toCharArray();
            Calendar calendar = parseDate(c);
            int h = (c[11] - '0') * 10 + (c[12] - '0');
            int m = (c[14] - '0') * 10 + (c[15] - '0');
            int s = (c[17] - '0') * 10 + (c[18] - '0');
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            calendar.set(Calendar.SECOND, s);
            return new DateTime(calendar.getTimeInMillis());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}
