package yeamy.restlite.httpclient;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

class DateTimeUtil {

    static Time parseTime(String time) throws IOException {
        try {
            char[] c = time.toCharArray();
            int h = (c[0] - '0') * 10 + (c[1] - '0');
            int m = (c[3] - '0') * 10 + (c[4] - '0');
            int s = (c[6] - '0') * 10 + (c[7] - '0');
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            calendar.set(Calendar.SECOND, s);
            return new Time(calendar.getTimeInMillis());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    static String format(Time time) {
        return time.toString();
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

    static String format(Date date) {
        return date.toString();
    }

    static java.util.Date parseDateTime(String time) throws IOException {
        try {
            char[] c = time.toCharArray();
            Calendar calendar = parseDate(c);
            int h = (c[11] - '0') * 10 + (c[12] - '0');
            int m = (c[14] - '0') * 10 + (c[15] - '0');
            int s = (c[17] - '0') * 10 + (c[18] - '0');
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            calendar.set(Calendar.SECOND, s);
            return calendar.getTime();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    static String format(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int y = calendar.get(Calendar.YEAR);
        int M = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DATE);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        StringBuilder sb = new StringBuilder();
        sb.append(y).append('-');
        append(sb, M);
        sb.append('-');
        append(sb, d);
        sb.append(' ');
        append(sb, h);
        sb.append(':');
        append(sb, m);
        sb.append(':');
        append(sb, s);
        return sb.toString();
    }

    private static void append(StringBuilder sb, int num) {
        if (num < 10) sb.append('0');
        sb.append(num);
    }

}
