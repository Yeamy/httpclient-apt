# HttpClient-APT
[![](https://img.shields.io/badge/platform-Java1.8+-red)](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase) [![](https://img.shields.io/github/license/Yeamy/httpclient-apt)](https://github.com/Yeamy/httpclient-apt/blob/master/LICENSE)  
English | [中文](README-CN.md)

Generate simple http clients with Java APT. Base on Apache HttpClient5, more efficient than the dynamic proxy (such as: OpenFeign and Forest).

## How to Use
### 1. Dependencies
Take gradle as an example
```gradle
dependencies {
    // with gson
    implementation 'io.github.yeamy:httpclient-apt-gson:1.1.0'
    // or with jackson
    //implementation 'io.github.yeamy:httpclient-apt-jackson:1.1.0'
    // or with jackson xml
    //implementation 'io.github.yeamy:httpclient-apt-jacksonxml:1.1.0'
}
```

### 2. Declared the HttpClient for interface

```java
import yeamy.restlite.httpclient.GsonRequestHandler;
import yeamy.restlite.httpclient.GsonResponseHandler;

@HttpClient(uri = "http://localhost:8080", // set the base uri
        requestBodyHandler = GsonRequestHandler.class,// set request serialize adapter
        responseHandler = GsonResponseHandler.class,// set respone handler
        maxTryTimes = 1,
        header = @Values(name = "user-agent", value = "custom-app/1.0"), // add common header
        cookie = {@Values(name = "1", value = "2"), // add common cookie
                @Values(name = "3", value = "4")})
public interface DemoClient {// must be an interface
}
```

### 3. create request method with HttpClientRequest

```java
public interface DemoClient {// interface with @HttpClient

    @HttpClientRequest(uri = "/hello")
    Object get();
}
```

### 4. Declared variable (method parameter)

1. declared parameters with brackets in annotation.
2. add java method parameters with same name.

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

| variable   | Attribute | Usage                                                             |
|------------|-----------|-------------------------------------------------------------------|
| **{u1}**   | uri       | define a parameter for uri, the param will be encoded.            |
| **{{u2}}** | uri       | double brackets keep the parameter without url encoded (uri only). |
| **{h1}**   | header    | define a parameter for header value.                              |
| **{c1}**   | cookie    | define a parameter for cookie value.                              |
| **{m1}**   | headerMap | define header parameters in a Map<>                               |
| **{m2}**   | cookieMap | define cookie parameters in a Map<>                               |
| **{b1}**   | body      | define a body parameter, the body create httpclient Entity.       |

### *5. Make @HttpClient into a template for multiple classes

create an annotation with HttpClient

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

add template for the interface

```java
@TemplateClient
// @HttpClient(maxTryTimes = 2) // can also override attributes of TemplateClient
public interface DemoClient {// must be an interface
}
```

## How dose the lib choose attributes

### The conflicting attributes will be overridden.

**attributes:** requestBodyHandler, responseHandler, maxTryTimes, protocol, method.

**Selection order:** HttpClientRequest > HttpClient > `TemplateClient`

### Default http method:

if http method not defined(empty string), will using "GET" or "POST" (if the request contains body).

### The complete uri will be the base uri append the sub uri:

**base uri:** HttpClient's (or `TemplateClient`'s) uri.

**sub uri:** HttpClientRequest's uri.

**Complete uri:** base uri + sub uri.

### The headers and cookies:

**total:**  HttpClientRequest's values + HttpClient's values + `TemplateClient`'s values.