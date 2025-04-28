package yeamy.restlite.httpclient.apt;

import org.apache.hc.core5.util.TextUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static yeamy.restlite.httpclient.apt.Utils.*;

class SourceMethod extends SourceFile {
    private static final String T_HttpClientRequestBodyHandler = "yeamy.restlite.httpclient.HttpClientRequestBodyHandler";
    private static final String T_NoHttpClientRequestBodyHandler = "yeamy.restlite.httpclient.NoHttpClientRequestBodyHandler";
    private static final String T_NoHttpClientResponseHandler = "yeamy.restlite.httpclient.NoHttpClientResponseHandler";

    public SourceMethod(ProcessingEnvironment env, boolean hasInjectProvider, TypeElement type, HttpClient template) {
        super(env, hasInjectProvider, type, template);
    }

    public SourceMethod(ProcessingEnvironment env, boolean hasInjectProvider, TypeElement type) {
        super(env, hasInjectProvider, type, null);
    }

    @Override
    protected void createMethodContent(StringBuilder content, ExecutableElement e,
                                       LinkedHashMap<String, VariableElement> params) {
        HttpClientRequest req = e.getAnnotation(HttpClientRequest.class);
        if (req == null) {
            returnNull(content, e);
            return;
        }
        String method = e.getSimpleName().toString();
        uri(content, req, method, params);
        PartValues[] parts = req.body();
        content.append("HttpUriRequestBase req = new HttpUriRequestBase(\"")
                .append(firstNotEmpty(req.method(), httpMethod, parts.length > 0 ? "POST" : "GET"))
                .append("\", URI.create(uri));");
        protocol(content, req);
        headerMap(content, req, method, params);
        header(content, req, method, params);
        cookie(content, req, method, params);
        if (parts.length == 1) {
            body(content, req, parts[0], method, params);
        } else if (parts.length > 1) {
            multiPart(content, req, parts, method, params);
        }
        String responseHandler = responseHandler(req, this.responseHandler);
        if (TextUtils.isBlank(responseHandler)) {
            String msg = "ResponseHandler cannot be empty: class " + type.getQualifiedName();
            printError(msg);
            content.append("return null;/*").append(msg).append("*/");
        } else {
            int maxTryTimes = maxTryTimes(req.maxTryTimes());
            content.append("return execute(req, new ")
                    .append(imports(responseHandler))
                    .append("<>(),").append(maxTryTimes).append(");");
        }
    }

    private void uri(StringBuilder content, HttpClientRequest req, String method, Map<String, VariableElement> params) {
        String prefix = req.uri().substring(0, 8).toLowerCase();
        String uri = (prefix.startsWith("http://") || prefix.equals("https://"))
                ? req.uri()
                : this.baseUri + req.uri();
        if (TextUtils.isEmpty(uri)) {
            content.append("return null;// ERROR");
            String msg = "Uri cannot be empty: " + this.className + '.' + method;
            printError(msg);
            throw new RuntimeException(msg);
        }
        try {
            CharSequence sb = replaceArgs(uri, params);
            content.append("String uri = ").append(sb).append(';');
        } catch (ParamNotFoundException e) {
            content.append("return null;// ERROR");
            String msg = "Missing uri param '" + e.pName() + "': " + this.className + '.' + method;
            printError(msg);
            throw new RuntimeException(msg);
        }
    }

    private CharSequence replaceArgs(String src, Map<String, VariableElement> params) throws ParamNotFoundException {
        StringBuilder sb = new StringBuilder();
        src = src.replace("\"", "\\\"");
        int from = 0;
        while (true) {
            int begin = src.indexOf('{', from);
            if (begin == -1) {
                if (sb.length() > 0) sb.append('+');
                sb.append('"').append(src, from, src.length()).append('"');
                break;
            }
            int end = src.indexOf('}', begin);
            if (src.charAt(begin + 1) == '{' && src.charAt(end + 1) == '}') end++;
            String pName = src.substring(begin + 1, end);
            boolean noEncode = isParam(pName);
            if (noEncode) pName = getParamName(pName);
            if (!params.containsKey(pName)) {
                throw new ParamNotFoundException(pName);
            }
            if (sb.length() > 0) sb.append('+');
            sb.append('"').append(src, from, begin).append("\" + ");
            if (noEncode) {
                sb.append(pName);
            } else {
                sb.append("encodeUrl(").append(pName).append(')');
            }
            from = end + 1;
            if (from == src.length()) break;
        }
        return sb;
    }

    private void protocol(StringBuilder content, HttpClientRequest req) {
        Protocol protocol = firstNotEquals(Protocol.NOT_DEFINED, req.protocol(), this.protocol);
        if (protocol != Protocol.NOT_DEFINED) {
            content.append("req.setVersion(HttpVersion.").append(protocol).append(");");
        }
    }

    private static TypeMirror MAP;

    private TypeMirror mapTypeMirror() {
        if (MAP == null) {
            MAP = elements.getTypeElement("java.util.Map").asType();
        }
        return MAP;
    }

    private void headerMap(StringBuilder content, HttpClientRequest req, String method, Map<String, VariableElement> params) {
        String headerMap = req.headerMap();
        if (headerMap.isEmpty()) return;
        if (isParam(headerMap)) {
            headerMap = getParamName(headerMap);
            VariableElement e = params.get(headerMap);
            if (e == null) {
                String msg = "Missing headerMap param '" + headerMap + "': " + this.className + '.' + method;
                printError(msg);
            } else if (!isAcceptType(types, e, mapTypeMirror())) {
                String msg = "HeaderMap must be a Map<> '" + headerMap + "': " + this.className + '.' + method;
                printError(msg);
            } else {
                boolean[] kv = getTypeParameters(e.asType().toString());
                content.append(headerMap)
                        .append(".forEach((k,v)->req.setHeader(")
                        .append(kv[0] ? "k" : "String.valueOf(k)")
                        .append(",v));");
            }
        } else {
            String msg = "HeaderMap must with curly bracket '" + headerMap + "': " + this.className + '.' + method;
            printError(msg);
        }
    }

    private void header(StringBuilder content, HttpClientRequest req, String method, Map<String, VariableElement> params) {
        ArrayList<Values> headers = new ArrayList<>();
        Collections.addAll(headers, this.header);
        Collections.addAll(headers, req.header());
        for (Values header : headers) {
            String hName = header.value();
            if (isParam(hName)) {
                hName = getParamName(hName);
                if (!params.containsKey(hName)) {
                    content.append("return null;// ERROR");
                    String msg = "Missing header param '" + hName + "': " + this.className + '.' + method;
                    printError(msg);
                    throw new RuntimeException(msg);
                }
                content.append("req.setHeader(\"")
                        .append(header.name())
                        .append("\", ")
                        .append(hName)
                        .append(");");
            } else {
                content.append("req.setHeader(\"")
                        .append(header.name())
                        .append("\", \"")
                        .append(hName)
                        .append("\");");
            }
        }
    }

    private void cookieMap(StringBuilder content, HttpClientRequest req, String method, Map<String, VariableElement> params) {
        String cookieMap = req.cookieMap();
        if (cookieMap.isEmpty()) return;
        if (isParam(cookieMap)) {
            cookieMap = getParamName(cookieMap);
            VariableElement e = params.get(cookieMap);
            if (e == null) {
                String msg = "Missing cookieMap param '" + cookieMap + "': " + this.className + '.' + method;
                printError(msg);
            } else if (!isAcceptType(types, e, mapTypeMirror())) {
                String msg = "CookieMap must be a Map<> '" + cookieMap + "': " + this.className + '.' + method;
                printError(msg);
            } else {
                boolean[] kv = getTypeParameters(e.asType().toString());
                content.append(cookieMap)
                        .append(".forEach((k,v)->cookie.addCookie(new BasicClientCookie(")
                        .append(kv[0] ? "k" : "String.valueOf(k)").append(',')
                        .append(kv[1] ? "v" : "String.valueOf(v)").append(")));");
            }
        } else {
            String msg = "CookieMap must with curly bracket '" + cookieMap + "': " + this.className + '.' + method;
            printError(msg);
        }
    }

    private void cookie(StringBuilder content, HttpClientRequest req, String method, Map<String, VariableElement> params) {
        StringBuilder temp = new StringBuilder();
        cookieMap(temp, req, method, params);
        ArrayList<Values> cookies = new ArrayList<>();
        Collections.addAll(cookies, this.cookie);
        Collections.addAll(cookies, req.cookie());
        for (Values cookie : cookies) {
            String cName = cookie.value();
            if (isParam(cName)) {
                cName = getParamName(cName);
                if (!params.containsKey(cName)) {
                    content.append("return null;// ERROR");
                    String msg = "Missing cookie param '" + cName + "': " + this.className + '.' + method;
                    printError(msg);
                    throw new RuntimeException(msg);
                }
                temp.append("cookie.addCookie(new BasicClientCookie(\"")
                        .append(cookie.name())
                        .append("\", ")
                        .append(cName)
                        .append("));");
            } else {
                temp.append("cookie.addCookie(new BasicClientCookie(\"")
                        .append(cookie.name())
                        .append("\", \"")
                        .append(cName)
                        .append("\"));");
            }
        }
        if (temp.length() > 0) {
            content.append("BasicCookieStore cookie = new BasicCookieStore();");
            content.append(temp);
            temp.append("req.setHeader(\"Cookie\", cookie);");
        }
    }

    private void body(StringBuilder content, HttpClientRequest req, PartValues body, String method, Map<String, VariableElement> params) {
        String pName = body.value();
        if (isParam(pName)) {
            pName = getParamName(pName);
        } else {
            try {
                CharSequence data = replaceArgs(pName, params);
                bodyString(content, data, body.contentType());
            } catch (ParamNotFoundException e) {
                content.append("return null;// ERROR");
                String msg = "Missing body param '" + e.pName() + "': " + this.className + '.' + method;
                printError(msg);
            }
            return;
        }
        VariableElement e = params.get(pName);
        if (e == null) {
            String msg = "Missing body param '" + pName + "': " + this.className + '.' + method;
            content.append("/*").append(msg).append("*/");
            printError(msg);
            return;
        }
        String contentType = body.contentType();
        switch (e.asType().toString()) {
            case "java.lang.String": {
                bodyString(content, pName, contentType);
                break;
            }
            case "byte[]": {
                bodyBinary(content, "org.apache.hc.core5.http.io.entity.ByteArrayEntity", pName, contentType);
                break;
            }
            case "java.io.File": {
                bodyBinary(content, "org.apache.hc.core5.http.io.entity.FileEntity", pName, contentType);
                break;
            }
            case "java.io.InputStream": {
                bodyBinary(content, "org.apache.hc.core5.http.io.entity.InputStreamEntity", pName, contentType);
            }
            break;
            default:
                String requestBodyHandler = requestBodyHandler(req, this.requestBodyHandler);
                if (requestBodyHandler.equals(T_NoHttpClientRequestBodyHandler)) return;
                TypeElement te = elements.getTypeElement(requestBodyHandler);
                final int l = T_HttpClientRequestBodyHandler.length();
                for (TypeMirror m : te.getInterfaces()) {
                    String type = m.toString();
                    if (!type.startsWith(T_HttpClientRequestBodyHandler)) continue;
                    if (type.length() == l) break;
                    if (type.charAt(l) != '<') continue;
                    String varType = type.substring(l + 1, type.length() - 1);
                    TypeMirror vt = elements.getTypeElement(varType).asType();
                    TypeMirror pt = e.asType();
                    if (types.isAssignable(pt, vt)) break;
                    printError(requestBodyHandler + "cannot serialize type " + pt + " in " + this.type.getQualifiedName() + "." + method + "()");
                    return;
                }
                content.append("req.setEntity(new ").append(imports(requestBodyHandler))
                        .append("().createEntity(").append(pName).append(",\"").append(contentType).append("\"));");
        }
    }

    private void bodyString(StringBuilder content, CharSequence pName, String contentType) {
        content.append("req.setEntity(new ").append(imports("org.apache.hc.core5.http.io.entity.StringEntity"))
                .append('(').append(pName);
        if (contentType.length() > 0) {
            content.append(',').append(imports("org.apache.hc.core5.http.ContentType"))
                    .append(".create(\"").append(contentType).append("\")");
        }
        content.append("));");
    }

    private void bodyBinary(StringBuilder content, String entity, String pName, String contentType) {
        content.append("req.setEntity(new ").append(imports(entity)).append('(').append(pName)
                .append(',').append(imports("org.apache.hc.core5.http.ContentType"));
        if (contentType.isEmpty()) {
            content.append(".DEFAULT_BINARY").append(')');
        } else {
            content.append(".create(\"").append(contentType).append("\")");
        }
        content.append(");");
    }

    private void multiPart(StringBuilder content, HttpClientRequest req, PartValues[] parts, String method,
                           LinkedHashMap<String, VariableElement> params) {
        content.append("req.setEntity(")
                .append(imports("org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder"))
                .append(".create()");
        for (PartValues part : parts) {
            multiPart(content, req, part, method, params);
        }
        content.append(".build());");
    }

    private void multiPart(StringBuilder content, HttpClientRequest req, PartValues part, String method,
                           LinkedHashMap<String, VariableElement> params) {
        String pName = part.value();
        if (isParam(pName)) {
            pName = getParamName(pName);
        } else {
            multiPartString(content, "\"" + pName.replace("\"", "\\\"") + '\"', part);
            return;
        }
        VariableElement e = params.get(pName);
        if (e == null) {
            String msg = "Missing body param '" + pName + "': " + this.className + '.' + method;
            content.append("/*").append(msg).append("*/");
            printError(msg);
            return;
        }
        String contentType = part.contentType();
        switch (e.asType().toString()) {
            case "java.lang.String": {
                multiPartString(content, pName, part);
                break;
            }
            case "byte[]":
            case "java.io.File":
            case "java.io.InputStream": {
                content.append(".addBinaryBody(\"").append(part.name()).append("\", ").append(pName);
                if (part.filename().length() > 0 || contentType.length() > 0) {
                    content.append(", ").append(imports("org.apache.hc.core5.http.ContentType"));
                    if (contentType.length() > 0) {
                        content.append(".create(\"").append(contentType).append("\"),\"");
                    } else {
                        content.append(".DEFAULT_BINARY").append("),\"");
                    }
                    content.append(part.filename()).append('"');
                }
                content.append(")");
                break;
            }
            default:
                String responseHandler = responseHandler(req, this.responseHandler);
                if (TextUtils.isBlank(responseHandler)) {
                    String msg = "ResponseHandler cannot be empty: class " + type.getQualifiedName();
                    printError(msg);
                    content.append("/*").append(msg).append("*/;");
                } else {
                    content.append(imports(requestBodyHandler(req, this.requestBodyHandler)))
                            .append("().serializeAsPart(").append(pName).append(",\"").append(contentType).append("\");");
                }
        }
    }

    private String responseHandler(HttpClientRequest req, TypeMirror fallback) {
        TypeMirror t;
        try {
            Class<?> clz = req.responseHandler();
            t = elements.getTypeElement(clz.getName()).asType();
        } catch (MirroredTypeException e) {
            t = e.getTypeMirror();
        }
        if (t.toString().equals(T_NoHttpClientResponseHandler)) {
            return fallback.toString();
        } else {
            return t.toString();
        }
    }

    private String requestBodyHandler(HttpClientRequest req, TypeMirror fallback) {
        TypeMirror t;
        try {
            Class<?> clz = req.requestBodyHandler();
            t = elements.getTypeElement(clz.getName()).asType();
        } catch (MirroredTypeException e) {
            t = e.getTypeMirror();
        }
        if (t.toString().equals(T_NoHttpClientRequestBodyHandler)) {
            return fallback.toString();
        } else {
            return t.toString();
        }
    }

    private void multiPartString(StringBuilder content, String pName, PartValues part) {
        String contentType = part.contentType();
        if (part.filename().length() > 0 && contentType.length() > 0) {
            content.append(".addBinaryBody(\"").append(part.name()).append("\", ").append(pName)
                    .append(".getBytes(").append(imports("java.nio.charset.Charset"))
                    .append(".defaultCharset())");
            content.append(", ").append(imports("org.apache.hc.core5.http.ContentType"))
                    .append(".create(\"").append(contentType).append("\"),\"")
                    .append(part.filename()).append("\")");
        } else {
            content.append(".addTextBody(\"").append(part.name()).append("\", ").append(pName);
            if (contentType.length() > 0) {
                content.append(", ").append(imports("org.apache.hc.core5.http.ContentType"))
                        .append(".create(\"").append(contentType).append("\")");
            }
            content.append(")");
        }
    }

    private void returnNull(StringBuilder content, ExecutableElement e) {
        TypeMirror rt = e.getReturnType();
        switch (rt.getKind()) {
            case VOID:
                break;
            case DOUBLE:
            case FLOAT:
            case LONG:
            case INT:
            case CHAR:
            case BYTE:
                content.append("return 0;");
                break;
            case BOOLEAN:
                content.append("return false;");
                break;
            default:
                content.append("return null;");
                break;
        }
    }

}
