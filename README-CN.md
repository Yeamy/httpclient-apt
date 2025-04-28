# HttpClient-APT

[![](https://img.shields.io/badge/platform-Java1.8+-red)](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase) [![](https://img.shields.io/github/license/Yeamy/httpclient-apt)](https://github.com/Yeamy/httpclient-apt/blob/master/LICENSE)   
[English](README.md) | 中文

通过Java APT生成http请求代码。基于Apache HttpClient5开发，效率高于OpenFeign和Forest的动态代理实现。

## 如何使用

### 1. 添加依赖
以gradle为例

```gradle
dependencies {
    // 使用gson
    implementation 'io.github.yeamy:httpclient-apt-gson:1.1.0'
    // 或者使用jackson
    //implementation 'io.github.yeamy:httpclient-apt-jackson:1.1.0'
    // 或者使用jackson xml
    //implementation 'io.github.yeamy:httpclient-apt-jacksonxml:1.1.0'
}
```

### 2. 为interface添加HttpClient注解

```java
import yeamy.restlite.httpclient.GsonRequestHandler;
import yeamy.restlite.httpclient.GsonResponseHandler;

@HttpClient(uri = "http://localhost:8080", // 基础uri
        requestBodyHandler = GsonRequestHandler.class,// 请求body的序列化适配器
        responseHandler = GsonResponseHandler.class,// http应答数据处理器
        maxTryTimes = 1,
        header = @Values(name = "user-agent", value = "custom-app/1.0"), // 共用的header
        cookie = {@Values(name = "1", value = "2"), // 共用的cookie
                @Values(name = "3", value = "4")})
public interface DemoClient {// 必须是interface
}
```

### 3. 使用HttpClientRequest注解创建请求方法

```java
public interface DemoClient {// 带@HttpClient声明的接口

    @HttpClientRequest(uri = "/hello")
    Object get();
}
```

### 4. 声明变量

1. 用大括号声明变量。
2. 为java方法添加同名参数。

```java
public interface DemoClient {
    @HttpClientRequest(uri = "http://localhost:8080/a/{u1}?x={{u2}}",
            headerMap = "{m1}",
            cookieMap = "{m2}",
            header = @Values(name = "h", value = "{h1}"),
            cookie = @Values(name = "v", value = "{c1}"),
            body = {@PartValues(name = "name", value = "{b1}")}
    )
    String getPo(String u1, String u2, String c1, String h1, String b1, Map<?, String> m1, Map<String, String> m2);
}
```

| 变量         | 属性        | 用法                                         |
|------------|-----------|--------------------------------------------|
| **{u1}**   | uri       | 为uri添加变量，该变量将会被URL编码。                      |
| **{{u2}}** | uri       | 两个大括号，让URI变量不被编码。                          |
| **{h1}**   | header    | 为header定义个变量值。                             |
| **{c1}**   | cookie    | 为cookie定义变量值。                              |
| **{m1}**   | headerMap | 多个header变量值，通过Map<>传入。                     |
| **{m2}**   | cookieMap | 多个cookie变量值，通过Map<>传入。                     |
| **{b1}**   | body      | 定义body变量，该变量将被requestBodyHandler转化为Entity。 |  


### *5. 可以把@HttpClient制作成一个模板，供多个类使用

创建一个模板注解

```java
import yeamy.restlite.httpclient.JacksonRequestHandler;
import yeamy.restlite.httpclient.JacksonResponseHandler;

@HttpClient(
        requestBodyHandler = JacksonRequestHandler.class,
        responseHandler = JacksonResponseHandler.class,
        maxTryTimes = 1,
        uri = "http://localhost:8080",
        header = @Values(name = "user-agent", value = "custom-app/1.0"),
        cookie = {@Values(name = "1", value = "2"),
                @Values(name = "3", value = "4")})
public @interface TemplateClient {
}
```

为**接口**添加该模板注解

```java
@TemplateClient
// @HttpClient(maxTryTimes = 2) // 可以再次使用HttpClient覆盖TemplateClient的属性
public interface DemoClient {// 必须是interface
}
```

## 属性是如何被选择的

### 冲突的属性将被覆盖

**属性包括:** requestBodyHandler, responseHandler, maxTryTimes, protocol, method.

**选择顺序:** HttpClientRequest > HttpClient > `TemplateClient`

### 默认HTTP方法:

当http方法未必定义，或者为空字符串时，使用GET，或者POST(如果请求包含body)。

### 完整的URI由基础URI加上子URI组成:

**基础uri:** HttpClient的uri 或者 `TemplateClient`的uri.

**子uri:** HttpClientRequest的uri.

**完整uri:** 基础uri + 子uri.

### headers和cookies:

**全部:** HttpClientRequest的属性集加上HttpClient的属性集，再加上（如果存在）`TemplateClient`的属性集。