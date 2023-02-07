# HttpClient-APT
[![](https://img.shields.io/badge/platform-Java1.8+-red)](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase) [![](https://img.shields.io/github/license/Yeamy/httpclient-apt)](https://github.com/Yeamy/httpclient-apt/blob/master/LICENSE)  
English | [中文](README-CN.md)

Generate simple http clients with Java APT.

## Dependencies

![dependencies](dependencies.png)
```
// gradle
dependencies {
    // with gson
    implementation 'io.github.yeamy:httpclient-apt-gson:1.0'
    // or with jackson
    //implementation 'io.github.yeamy:httpclient-apt-jackson:1.0'
}
```
## How to Use

### Declared the HttpClient for interface

```java
import yeamy.restlite.httpclient.GsonRequestAdapter;
import yeamy.restlite.httpclient.GsonResponseHandler;

@HttpClient(
        serializeAdapter = GsonRequestAdapter.class,// set request serialize adapter
        responseHandler = GsonResponseHandler.class,// set respone handler
        uri = "http://localhost:8080", // set the base uri
        maxTryTimes = 1,
        header = @Values(name = "user-agent", value = "custom-app/1.0"), // add common header
        cookie = {@Values(name = "1", value = "2"), // add common cookie
                @Values(name = "3", value = "4")})
public interface DemoClient {// must be an interface
}
```

### OR create a template

create an annotation (such as `TemplateClient`) with HttpClient

```java
import yeamy.restlite.httpclient.JacksonRequestAdapter;
import yeamy.restlite.httpclient.JacksonResponseHandler;

@HttpClient(
        serializeAdapter = JacksonRequestAdapter.class,
        responseHandler = JacksonResponseHandler.class,
        maxTryTimes = 1,
        uri = "http://localhost:8080",
        header = @Values(name = "user-agent", value = "custom-app/1.0"),
        cookie = {@Values(name = "1", value = "2"),
                @Values(name = "3", value = "4")})
public @interface TemplateClient {
}
```

add `TemplateClient` for the interface

```java
@TemplateClient
// @HttpClient(maxTryTimes = 2) // can alse override attributes of TemplateClient
public interface DemoClient {// must be an interface
}
```

### create request method with HttpClientRequest

```java
public interface DemoClient {

    @HttpClientRequest(uri = "/baidu")
    Object get();
}
```

### Declared variable (method parameter)

1. declared parameters with brackets in annotation.
2. add java method parameters with same name.

```java
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

| variable   | Attribute | Usage                                                                |
|------------|-----------|----------------------------------------------------------------------|
| **{u1}**   | uri       | define a parameter for uri, the param will be encoded.               |
| **{{u2}}** | uri       | double brackets keep the parameter without url encoded (uri only).   |
| **{h1}**   | header    | define a parameter for header value.                                 |
| **{c1}**   | cookie    | define a parameter for cookie value.                                 |
| **{b1}**   | body      | define a body parameter, the body will be serialized by the adapter. |

## How dose the lib choose attributes

### The conflicting attributes will be overridden.

**attributes:** serializeAdapter, responseHandler, maxTryTimes, protocol, method.

**Selection order:** HttpClientRequest > HttpClient > `TemplateClient`

### Default http method:

if http method not defined(empty string), will using "GET" or "POST" (if the request contains body).

### The complete uri will be the base uri append the sub uri:

**base uri:** HttpClient's (or `TemplateClient`'s) uri.

**sub uri:** HttpClientRequest's uri.

**Complete uri:** base uri + sub uri.

### The headers and cookies:

**total:**  HttpClientRequest's values + HttpClient's values + `TemplateClient`'s values.