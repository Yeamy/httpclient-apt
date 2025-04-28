package yeamy.restlite.httpclient.apt;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class Utils {

    public static int firstGreaterThan(int c, int... ts) {
        for (int t : ts) {
            if (t > c) {
                return t;
            }
        }
        return c;
    }

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
        return value.charAt(0) == '{' && value.indexOf('}') == value.length() - 1;
    }

    public static String getParamName(String value) {
        return value.substring(1, value.length() - 1).trim();
    }


    public static boolean[] getTypeParameters(String type) {
        boolean[] out = {false, false};
        int i = type.indexOf('<');
        if (i > 0) {
            String[] tps = type.substring(i + 1, type.indexOf('>')).split(",");
            out[0] = "java.lang.String".equals(tps[0].trim());
            out[1] = "java.lang.String".equals(tps[1].trim());
        }
        return out;
    }

    public static boolean isAcceptType(Types types, VariableElement e, TypeMirror map) {
        String name = e.asType().toString();
        return name.equals("java.util.Map")
                || name.startsWith("java.util.Map<")
                || types.isSubtype(e.asType(), map);
    }
}
