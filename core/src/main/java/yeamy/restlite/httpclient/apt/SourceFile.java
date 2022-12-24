package yeamy.restlite.httpclient.apt;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static yeamy.restlite.httpclient.apt.Utils.*;

abstract class SourceFile {
    private final ProcessingEnvironment env;
    protected final TypeElement type;
    private final String pkg;
    protected final String className;
    protected final String httpMethod, baseUri;
    protected final Protocol protocol;
    protected final Values[] header, cookie;
    protected final String serializeAdapter, responseHandler;
    private final ArrayList<ExecutableElement> methods = new ArrayList<>();
    private final HashMap<String, String> imports = new HashMap<>();
    private final int maxTryTimes;

    public SourceFile(ProcessingEnvironment env, TypeElement type, HttpClient template) {
        HttpClient client = type.getAnnotation(HttpClient.class);
        if (client == null) {
            serializeAdapter = template.serializeAdapter();
            responseHandler = template.responseHandler();
            className = type.getSimpleName() + "Impl";
            httpMethod = template.method();
            baseUri = template.uri();
            protocol = template.protocol();
            header = template.header();
            cookie = template.cookie();
            maxTryTimes = template.maxTryTimes();
        } else if (template == null) {
            serializeAdapter = client.serializeAdapter();
            responseHandler = client.responseHandler();
            className = firstNotEmpty(client.className(), type.getSimpleName() + "Impl");
            httpMethod = client.method();
            baseUri = client.uri();
            protocol = client.protocol();
            header = client.header();
            cookie = client.cookie();
            maxTryTimes = client.maxTryTimes();
        } else {
            serializeAdapter = firstNotEmpty(client.serializeAdapter(), template.serializeAdapter());
            responseHandler = firstNotEmpty(client.responseHandler(), template.responseHandler());
            className = firstNotEmpty(client.className(), type.getSimpleName() + "Impl");
            httpMethod = firstNotEmpty(client.method(), template.method());
            baseUri = firstNotEmpty(client.uri(), template.uri());
            protocol = firstNotEquals(Protocol.NOT_DEFINED, client.protocol(), template.protocol());
            header = appendArray(template.header(), client.header());
            cookie = appendArray(template.cookie(), client.cookie());
            maxTryTimes = firstNotEquals(0, client.maxTryTimes(), template.maxTryTimes());
        }
        this.env = env;
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

    public String imports(String clz) {
        if (clz.length() == 0) {
            return "";
        }
        if (clz.startsWith("java.lang.") && clz.indexOf('.', 10) == -1)
            return clz.substring(10);
        String sn = clz.substring(clz.lastIndexOf('.') + 1);
        String clz2 = imports.get(sn);
        if (clz2 == null) {
            imports.put(sn, clz);
            return sn;
        }
        if (clz2.equals(clz)) {
            return sn;
        }
        return clz;
    }

    public void create() throws IOException {
        String file = pkg + '.' + className;
        JavaFileObject f = env.getFiler().createSourceFile(file);
        StringBuilder methods = new StringBuilder();
        for (ExecutableElement e : this.methods) {
            createMethod(methods, e);
        }
        StringBuilder sb = new StringBuilder("package ").append(pkg).append(';');
        sb.append("import static yeamy.restlite.httpclient.HttpClientImpl.*;");
        for (String clz : imports.values()) {
            sb.append("import ").append(clz).append(';');
        }
        sb.append("public class ").append(className).append(" implements ")
                .append(type.getSimpleName()).append('{').append(methods).append('}');
        try (OutputStream os = f.openOutputStream()) {
            os.write(sb.toString().getBytes());
            os.flush();
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
