# Masking Sensitive Authorization Headers in Logback

This is what `Apache HttpClient` logs looks like when we use DEBUG as its settings, and we can see that is some **sensitive** values displayed here, for example `Authorization` Headers.

```
16:41:35.164 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "GET / HTTP/1.1[\r][\n]"
16:41:35.165 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Accept-Encoding: text/plain[\r][\n]"
16:41:35.165 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Accept-Charset: utf-8[\r][\n]"
16:41:35.165 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiS.jwt-sample.cpQcLckrTofjLZtCFFcMfThBNWD[\r][\n]"
16:41:35.165 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Host: localhost:8080[\r][\n]"
16:41:35.165 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Connection: keep-alive[\r][\n]"
16:41:35.165 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "User-Agent: Apache-HttpClient/5.1.4 (Java/17.0.6)[\r][\n]"
16:41:35.165 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "[\r][\n]"
16:41:35.175 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "HTTP/1.1 200 OK[\r][\n]"
16:41:35.176 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Matched-Stub-Id: 584bed8e-a120-46c4-9139-ce3837b47ef6[\r][\n]"
16:41:35.176 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Transfer-Encoding: chunked[\r][\n]"
16:41:35.176 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "[\r][\n]"
16:41:35.176 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "b[\r][\n]"
16:41:35.176 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Hello World[\r][\n]"
16:41:35.179 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "0[\r][\n]"
16:41:35.179 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "[\r][\n]"
```

Above logs is being printed by using below `Logback` configuration, 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE"
              class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <logger name="com.edw" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>

    <logger name="org.apache.hc.client5.http.wire" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>

    <root level="WARN">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```