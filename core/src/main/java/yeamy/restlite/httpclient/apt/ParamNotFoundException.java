package yeamy.restlite.httpclient.apt;

public class ParamNotFoundException extends Exception {
    private final String pName;

    public ParamNotFoundException(String pName) {
        this.pName = pName;
    }

    public String pName() {
        return pName;
    }
}
