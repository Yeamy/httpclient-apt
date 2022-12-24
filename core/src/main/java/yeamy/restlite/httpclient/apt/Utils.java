package yeamy.restlite.httpclient.apt;

public class Utils {

    @SafeVarargs
    public static <T> T firstNotEquals(T c, T... ts) {
        for (T t : ts) {
            if (!t.equals(c)) {
                return t;
            }
        }
        return c;
    }

    public static Values[] appendArray(Values[] a, Values[] b) {
        if (a.length == 0) {
            return b;
        } else if (b.length == 0) {
            return a;
        } else {
            Values[] o = new Values[a.length + b.length];
            System.arraycopy(a, 0, o, 0, a.length);
            System.arraycopy(b, 0, o, a.length, b.length);
            return o;
        }
    }

    public static String firstNotEmpty(String... ts) {
        return firstNotEquals("", ts);
    }

    public static boolean isParam(String value) {
        return value.charAt(0) == '{' && value.charAt(value.length() - 1) == '}';
    }

    public static String getParamName(String value) {
        return value.substring(1, value.length() - 1).trim();
    }

}
