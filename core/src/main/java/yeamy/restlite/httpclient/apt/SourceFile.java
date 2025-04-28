package yeamy.restlite.httpclient.apt;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.OutputStream;
import java.util.*;

import static yeamy.restlite.httpclient.apt.Utils.*;

abstract class SourceFile {
    protected final ProcessingEnvironment env;
    protected final Elements elements;
    protected final Types types;
    private final boolean hasInjectProvider;
    protected final TypeElement type;
    private final String pkg;
    protected final String className;// qualified name
    protected final String httpMethod, baseUri;
    protected final Protocol protocol;
    protected final Values[] header, cookie;
    private final boolean createConstant;
    protected final TypeMirror serializeAdapter, responseHandler;
    private final ArrayList<ExecutableElement> methods = new ArrayList<>();
    private final HashMap<String, String> imports = new HashMap<>();
    private final int maxTryTimes;

    public SourceFile(ProcessingEnvironment env, boolean hasInjectProvider, TypeElement type, HttpClient template) {
        HttpClient client = type.getAnnotation(HttpClient.class);
        if (client != null && template != null) {
            createConstant = client.createConstant();
            serializeAdapter = serializeAdapter(client, template);
            responseHandler = responseHandler(client, template);
            className = firstNotEmpty(client.className(), type.getQualifiedName() + "Impl");
            httpMethod = firstNotEmpty(client.method(), template.method());
            baseUri = firstNotEmpty(client.uri(), template.uri());
            protocol = firstNotEquals(Protocol.NOT_DEFINED, client.protocol(), template.protocol());
            header = appendArray(template.header(), client.header());
            cookie = appendArray(template.cookie(), client.cookie());
            maxTryTimes = firstGreaterThan(0, client.maxTryTimes(), template.maxTryTimes());
        } else if (template != null) {
            createConstant = true;
            serializeAdapter = serializeAdapter(template);
            responseHandler = responseHandler(template);
            className = type.getQualifiedName() + "Impl";
            httpMethod = template.method();
            baseUri = template.uri();
            protocol = template.protocol();
            header = template.header();
            cookie = template.cookie();
            maxTryTimes = template.maxTryTimes();
        } else {
            assert client != null;
            createConstant = client.createConstant();
            serializeAdapter = serializeAdapter(client);
            responseHandler = responseHandler(client);
            className = firstNotEmpty(client.className(), type.getQualifiedName() + "Impl");
            httpMethod = client.method();
            baseUri = client.uri();
            protocol = client.protocol();
            header = client.header();
            cookie = client.cookie();
            maxTryTimes = client.maxTryTimes();
        }
        this.env = env;
        this.types = env.getTypeUtils();
        this.elements = env.getElementUtils();
        this.hasInjectProvider = hasInjectProvider;
        this.type = type;
        this.pkg = ((PackageElement) type.getEnclosingElement()).getQualifiedName().toString();
        for (Element e : type.getEnclosedElements()) {
            if (e instanceof ExecutableElement) {
                Set<Modifier> ms = e.getModifiers();
                if (!ms.contains(Modifier.DEFAULT) && !ms.contains(Modifier.STATIC)) {
                    methods.add((ExecutableElement) e);
                }
            }
        }
        imports("java.net.URI");
        imports("org.apache.hc.client5.http.classic.methods.HttpUriRequestBase");
        imports("org.apache.hc.client5.http.cookie.BasicCookieStore");
        imports("org.apache.hc.client5.http.impl.cookie.BasicClientCookie");
        imports("org.apache.hc.core5.http.HttpVersion");
    }

    private TypeMirror serializeAdapter(HttpClient client) {
        try {
            Class<?> t = client.serializeAdapter();
            return elements.getTypeElement(t.getName()).asType();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
    }

    private TypeMirror serializeAdapter(HttpClient client, HttpClient template) {
        TypeMirror t = serializeAdapter(client);
        if (t.toString().equals("yeamy.restlite.httpclient.NoSerializeAdapter")) {
            return serializeAdapter(template);
        } else {
            return t;
        }
    }

    private TypeMirror responseHandler(HttpClient client) {
        try {
            Class<?> t = client.responseHandler();
            return elements.getTypeElement(t.getName()).asType();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
    }

    private TypeMirror responseHandler(HttpClient client, HttpClient template) {
        TypeMirror t = responseHandler(client);
        if (t.toString().equals("yeamy.restlite.httpclient.NoHttpClientResponseHandler")) {
            return responseHandler(template);
        } else {
            return t;
        }
    }

    private static final String[] systemType = {"int", "long", "short", "char", "boolean", "float", "double",
            "int[]", "long[]", "short[]", "char[]", "boolean[]", "float[]", "double[]"};

    public String imports(String clz) {
        if (clz.length() == 0) {
            return "";
        }
        if (clz.lastIndexOf('.') == -1) {
            for (String s : systemType) {
                if (clz.equals(s)) {
                    return clz;
                }
            }
        }
        if (clz.startsWith("java.lang.") && clz.indexOf('.', 10) == -1) {
            return clz.substring(10);
        }
        String ar = "";
        if (clz.charAt(clz.length() - 1) == ']') {
            for (int i = clz.length() - 2; i > 0; i--) {
                char c = clz.charAt(i);
                if (c != '[' && c != ']') {
                    ar = clz.substring(i);
                    break;
                }
            }
        }
        String vp = "";
        int i = clz.indexOf('<');
        if (i > 0) {
            StringBuilder sb = new StringBuilder("<");
            for (String st : clz.substring(i + 1, clz.lastIndexOf('>')).split(",")) {
                String clz2 = st.trim();
                if (clz2.equals("?")) {
                    sb.append("?,");
                } else {
                    sb.append(imports(clz2)).append(',');
                }
            }
            sb.setCharAt(sb.length() - 1, '>');
            vp = sb.toString();
            clz = clz.substring(0, i);
        }
        String sn = clz.substring(clz.lastIndexOf('.') + 1);
        String clz2 = imports.get(sn);
        if (clz2 == null) {
            imports.put(sn, clz);
            return sn + vp + ar;
        }
        if (clz2.equals(clz)) {
            return sn + vp + ar;
        }
        return clz + vp + ar;
    }

    public void create() {
        JavaFileObject f;
        try {
            f = env.getFiler().createSourceFile(className);
        } catch (Exception e) {
            return;
        }
        StringBuilder methods = new StringBuilder();
        for (ExecutableElement e : this.methods) {
            createMethod(methods, e);
        }
        StringBuilder sb = new StringBuilder("package ").append(pkg).append(';');
        sb.append("import static yeamy.restlite.httpclient.HttpClientImpl.*;");
        for (String clz : imports.values()) {
            sb.append("import ").append(clz).append(';');
        }
        if (hasInjectProvider) {
            sb.append('@').append(imports("yeamy.restlite.annotation.InjectProvider")).append("(provideFor=")
                    .append(imports(type.getQualifiedName().toString())).append(".class)");
        }
        sb.append("public class ").append(className).append(" implements ").append(type.getSimpleName()).append("{");
        if (createConstant) {
            sb.append("public static final ").append(className).append(" INSTANCE = new ").append(className).append("();");
        }
        sb.append(methods).append('}');
        try (OutputStream os = f.openOutputStream()) {
            os.write(sb.toString().getBytes());
            os.flush();
        } catch (Exception e) {
            env.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    protected void createMethod(StringBuilder content, ExecutableElement method) {
        content.append("@Override public ").append(imports(method.getReturnType().toString()))
                .append(' ').append(method.getSimpleName()).append("(");
        List<? extends VariableElement> ps = method.getParameters();
        LinkedHashMap<String, VariableElement> params = new LinkedHashMap<>();
        for (VariableElement p : ps) {
            params.put(p.getSimpleName().toString(), p);
            TypeMirror t = p.asType();
            content.append(imports(t.toString())).append(' ').append(p.getSimpleName()).append(',');
        }
        if (ps.size() > 0) {
            content.deleteCharAt(content.length() - 1);
        }
        content.append("){");
        createMethodContent(content, method, params);
        content.append('}');
    }

    protected abstract void createMethodContent(StringBuilder content, ExecutableElement method,
                                                LinkedHashMap<String, VariableElement> params);

    protected void printError(CharSequence msg) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }

    protected int maxTryTimes(int maxTryTimes) {
        int r = firstNotEquals(0, maxTryTimes, this.maxTryTimes);
        return r == 0 ? 3 : r;
    }
}
