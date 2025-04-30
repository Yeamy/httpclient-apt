package yeamy.restlite.httpclient;

import java.util.Calendar;
import java.util.Date;

public class DateTime extends Date {

    private String str;

    public DateTime() {
        super();
    }

    public DateTime(long date) {
        super(date);
    }

    @Override
    public void setTime(long time) {
        super.setTime(time);
        str = null;
    }

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
