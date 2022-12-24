package yeamy.restlite.httpclient.apt;

import org.apache.hc.core5.util.TextUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import static yeamy.restlite.httpclient.apt.Utils.*;

public class SourceMethod extends SourceFile {

    public SourceMethod(ProcessingEnvironment env, TypeElement type, HttpClient template) {
        super(env, type, template);
    }

    public SourceMethod(ProcessingEnvironment env, TypeElement type) {
        super(env, type, null);
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
        header(content, req, method, params);
        cookie(content, req, method, params);
        if (parts.length == 1) {
            body(content, req, parts[0], method, params);
        } else if (parts.length > 1) {
            multiPart(content, req, parts, method, params);
        }
        String responseHandler = firstNotEmpty(req.responseHandler(), this.responseHandler);
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

    private void uri(StringBuilder content, HttpClientRequest req, String method,
                     LinkedHashMap<String, VariableElement> params) {
        String uri = this.baseUri + req.uri();
        if (TextUtils.isEmpty(uri)) {
            content.append("return null;// ERROR");
            String msg = "Uri cannot be empty: " + this.className + '.' + method;
            printError(msg);
            throw new RuntimeException(msg);
        }
        StringBuilder sb = new StringBuilder("String uri = \"");
        int from = 0;
        while (true) {
            int begin = uri.indexOf('{', from);
            if (begin == -1) {
                sb.append(uri, from, uri.length());
                break;
            }
            int end = uri.indexOf('}', begin);
            if (uri.charAt(end + 1) == '}') end++;
            String pName = uri.substring(begin + 1, end);
            boolean noEncode = isParam(pName);
            if (noEncode) pName = getParamName(pName);
            if (!params.containsKey(pName)) {
                content.append("return null;// ERROR");
                String msg = "Missing uri param '" + pName + "': " + this.className + '.' + method;
                printError(msg);
                throw new RuntimeException(msg);
            }
            if (from > 0) sb.append('+');
            sb.append(uri, from, begin).append("\" + ");
            if (noEncode) {
                sb.append(pName);
            } else {
                sb.append("encodeUrl(").append(pName).append(')');
            }
            sb.append("+\"");
            from = end + 1;
        }
        if (sb.charAt(sb.length() - 2) == '+' && sb.charAt(sb.length() - 1) == '"') {
            sb.delete(sb.length() - 2, sb.length()).append(';');
        } else {
            sb.append("\";");
        }
        content.append(sb);
    }

    private void protocol(StringBuilder content, HttpClientRequest req) {
        Protocol protocol = firstNotEquals(Protocol.NOT_DEFINED, req.protocol(), this.protocol);
        if (protocol != Protocol.NOT_DEFINED) {
            content.append("req.setVersion(HttpVersion.").append(protocol).append(");");
        }
    }

    private void header(StringBuilder content, HttpClientRequest req, String method,
                        LinkedHashMap<String, VariableElement> params) {
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

    private void cookie(StringBuilder content, HttpClientRequest req, String method,
                        LinkedHashMap<String, VariableElement> params) {
        ArrayList<Values> cookies = new ArrayList<>();
        Collections.addAll(cookies, this.cookie);
        Collections.addAll(cookies, req.cookie());
        if (cookies.size() == 0) {
            return;
        }
        StringBuilder temp = new StringBuilder();
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
        content.append("BasicCookieStore cookie = new BasicCookieStore();");
        content.append(temp);
        content.append("req.setHeader(\"Cookie\", cookie);");
    }

    private void body(StringBuilder content, HttpClientRequest req, PartValues body, String method,
                      LinkedHashMap<String, VariableElement> params) {
        String pName = body.value();
        if (isParam(pName)) {
            pName = getParamName(pName);
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
                content.append("req.setEntity(new ").append(imports("org.apache.hc.core5.http.io.entity.StringEntity"))
                        .append('(').append(pName);
                if (contentType.length() > 0) {
                    content.append(',').append(imports("org.apache.hc.core5.http.ContentType"))
                            .append(".create(\"").append(contentType).append("\")");
                }
                content.append("));");
                break;
            }
            case "byte[]": {
                body(content, "org.apache.hc.core5.http.io.entity.ByteArrayEntity", pName, contentType);
                break;
            }
            case "java.io.File": {
                body(content, "org.apache.hc.core5.http.io.entity.FileEntity", pName, contentType);
                break;
            }
            case "java.io.InputStream": {
                body(content, "org.apache.hc.core5.http.io.entity.InputStreamEntity", pName, contentType);
            }
            break;
            default:
                String responseHandler = firstNotEmpty(req.responseHandler(), this.responseHandler);
                if (TextUtils.isBlank(responseHandler)) {
                    String msg = "ResponseHandler cannot be empty: class " + type.getQualifiedName();
                    printError(msg);
                    content.append("/*").append(msg).append("*/;");
                } else {
                    content.append(imports(firstNotEmpty(req.serializeAdapter(), this.serializeAdapter)))
                            .append("().serializeAsBody(").append(pName).append(",\"").append(contentType).append("\");");
                }
        }
    }

    private void body(StringBuilder content, String entity, String pName, String contentType) {
        content.append("req.setEntity(new ").append(imports(entity)).append('(').append(pName)
                .append(',').append(imports("org.apache.hc.core5.http.ContentType"));
        if (contentType.length() == 0) {
            content.append(".WILDCARD").append(')');
        } else {
            content.append(".create(\"").append(contentType).append("\")");
        }
        content.append(");");
    }

    private void multiPart(StringBuilder content, HttpClientRequest req, PartValues[] parts, String method,
                           LinkedHashMap<String, VariableElement> params) {
        content.append("req.setEntity(MultipartEntityBuilder.create()");
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
                content.append(".addTextBody(\"").append(part.name()).append("\", ").append(pName);
                if (part.contentType().length() > 0) {
                    content.append(", ").append(imports("org.apache.hc.core5.http.ContentType"))
                            .append(".create(\"").append(part.contentType()).append("\")");
                }
                content.append(")");
                break;
            }
            case "byte[]":
            case "java.io.File":
            case "java.io.InputStream": {
                content.append(".addBinaryBody(\"").append(part.name()).append("\", ").append(pName);
                if (part.contentType().length() > 0 && part.filename().length() > 0) {
                    content.append(", ").append(imports("org.apache.hc.core5.http.ContentType"))
                            .append(".create(\"").append(part.contentType()).append("\"),\"")
                            .append(part.filename()).append('"');
                }
                content.append(")");
                break;
            }
            default:
                String responseHandler = firstNotEmpty(req.responseHandler(), this.responseHandler);
                if (TextUtils.isBlank(responseHandler)) {
                    String msg = "ResponseHandler cannot be empty: class " + type.getQualifiedName();
                    printError(msg);
                    content.append("/*").append(msg).append("*/;");
                } else {
                    content.append(imports(firstNotEmpty(req.serializeAdapter(), this.serializeAdapter)))
                            .append("().serializeAsBody(").append(pName).append(",\"").append(contentType).append("\");");
                }
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
