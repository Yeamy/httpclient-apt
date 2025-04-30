package yeamy.restlite.httpclient;

import java.util.Calendar;
import java.util.Date;

/**
 * A thin wrapper of Date, whit format {@code yyyy-MM-dd HH:mm:ss}
 */
public class DateTime extends Date {

    /**
     * toString() cache value.
     */
    private transient String str;

    /**
     * Allocate with current timestamp
     */
    public DateTime() {
        super();
    }

    /**
     * Allocate with the specified timestamp
     * @param date the milliseconds since January 1, 1970, 00:00:00 GMT.
     */
    public DateTime(long date) {
        super(date);
    }

    @Override
    public void setTime(long time) {
        super.setTime(time);
        str = null;
    }

    /**
     * {@code yyyy-MM-dd HH:mm:ss} format string
     */
    @Override
    public String toString() {
        if (str != null) return str;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
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
        return str = sb.toString();
    }

    private static void append(StringBuilder sb, int num) {
        if (num < 10) sb.append('0');
        sb.append(num);
    }
}
