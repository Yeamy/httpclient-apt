# HttpClient-APT

[English](README.md) | 中文

通过Java APT生成简单的http客户端。

## 依赖关系

![依赖关系](dependencies.png)

## 如何使用

### 为interface添加HttpClient注解

```
@HttpClient(
        serializeAdapter = "yeamy.restlite.httpclient.GsonRequestAdapter",// 请求body的序列化适配器
        responseHandler = "yeamy.restlite.httpclient.GsonResponseHandler",// http应答数据处理器
        uri = "http://localhost:8080", // 基础uri
        maxTryTimes = 1,
        header = @Values(name = "user-agent", value = "custom-app/1.0"), // 共用的header
        cookie = {@Values(name = "1", value = "2"), // 共用的cookie
                @Values(name = "3", value = "4")})
public interface DemoClient {// 必须是interface
}
```

### 或者创建一个模板注解

创建一个模板注解(此处命名为 `TemplateClient`)

```
@HttpClient(
        serializeAdapter = "yeamy.restlite.httpclient.GsonRequestAdapter",
        responseHandler = "yeamy.restlite.httpclient.GsonResponseHandler",
        maxTryTimes = 1,
        uri = "http://localhost:8080",
        header = @Values(name = "user-agent", value = "custom-app/1.0"),
        cookie = {@Values(name = "1", value = "2"),
                @Values(name = "3", value = "4")})
public @interface TemplateClient {
}
```

为interface添加`TemplateClient`注解

```
@TemplateClient
// @HttpClient(maxTryTimes = 2) // 可以再次使用HttpClient覆盖TemplateClient的属性
public interface DemoClient {// 必须是interface
}
```

### 使用HttpClientRequest注解创建请求方法

```
public interface DemoClient {

    @HttpClientRequest(uri = "/baidu")
    Object get();
}
```

### 声明变量

1. 用大括号声明变量。
2. 为java方法添加同名参数。

```
public interface DemoClient {
    @HttpClientRequest(
            uri = "http://localhost:8080/a/{u1}?x={{u2}}",
            header = @Values(name = "h", value = "{h1}"),
            cookie = @Values(name = "v", value = "{c1}"),
            body = {@PartValues(name = "name", value = "{b1}")}
    )
    String getPo(String u1, String u2, String c1, String h1, String b1);
}
```

| 变量         | 属性     | 用法                                 |
|------------|--------|------------------------------------|
| **{u1}**   | uri    | 为uri添加变量，该变量将会被URL编码。              |
| **{{u2}}** | uri    | 两个大括号，让URI变量不被编码。                  |
| **{h1}**   | header | 为header定义个变量值。                     |
| **{c1}**   | cookie | 为cookie定义变量值。                      |
| **{b1}**   | body   | 定义body变量，该变量将被serializeAdapter序列化。 |  

## 属性是如何被选择的

### 冲突的属性将被覆盖

**属性包括:** serializeAdapter, responseHandler, maxTryTimes, protocol, method.

**选择顺序:** HttpClientRequest > HttpClient > `TemplateClient`

### 默认HTTP方法:

当http方法未必定义，或者为空字符串时，使用GET，或者POST(如果请求包含body)。

### 完整的URI由基础URI加上子URI组成:

**基础uri:** HttpClient的uri 或者 `TemplateClient`的uri.

**子uri:** HttpClientRequest的uri.

**完整uri:** 基础uri + 子uri.

### headers和cookies:

**全部:** HttpClientRequest的属性集加上HttpClient的属性集，再加上（如果存在）`TemplateClient`的属性集。