# Masking Sensitive Authorization Headers in Logback

## Initial Condition

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

## Logs with Masked Condition Configured by using XML files

With below custom logging pattern, adding `CUSTOM-HTTP-CONSOLE` that will do pattern `replace` functionality
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

    <appender name="CUSTOM-HTTP-CONSOLE"
              class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${mask.pattern}</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <logger name="com.edw" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>

    <logger name="org.apache.hc.client5.http.wire" level="DEBUG" additivity="false">
        <appender-ref ref="CUSTOM-HTTP-CONSOLE" />
    </logger>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

Run the test, passing the value of `mask.pattern` variable, using the command 

```
mvn -Dmask.pattern='%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %replace(%msg){"(.*Authorization:) .*\"", "$1 xxxxx\""}%n' test|tee out.log
```

which will resulting in below logs result where `Authorization` header is being masked.
```
08:56:23.046 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "GET / HTTP/1.1[\r][\n]"
08:56:23.046 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Accept-Encoding: text/plain[\r][\n]"
08:56:23.047 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Accept-Charset: utf-8[\r][\n]"
08:56:23.047 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Authorization: xxxxx"
08:56:23.047 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Host: localhost:8080[\r][\n]"
08:56:23.047 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Connection: keep-alive[\r][\n]"
08:56:23.047 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "User-Agent: Apache-HttpClient/5.1.4 (Java/17.0.6)[\r][\n]"
08:56:23.047 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "[\r][\n]"
08:56:23.064 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "HTTP/1.1 200 OK[\r][\n]"
08:56:23.064 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Matched-Stub-Id: eccc2faf-5b55-4850-981e-a0ef26219c87[\r][\n]"
08:56:23.064 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Transfer-Encoding: chunked[\r][\n]"
08:56:23.064 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "[\r][\n]"
08:56:23.069 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "b[\r][\n]"
08:56:23.069 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Hello World[\r][\n]"
08:56:23.069 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "0[\r][\n]"
08:56:23.069 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "[\r][\n]"
```

You can add more patterns. For example, if the `Content-Type` needs to be masked together with the `Authorization`, then you can modify the pattern like this:

```
mvn -Dmask.pattern='%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %replace(%msg){"(.*Authorization:|.*Content-Type:) .*\"", "$1 xxxxx\""}%n' test|tee out.log
```

Verify that the `Content-Type` and the `Authorization` headers are masked:

```
$ grep xxx out.log
15:09:50.184 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> "Content-Type: xxxxx"
15:09:50.296 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 << "Content-Type: xxxxx"
15:09:50.309 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Authorization: xxxxx"
15:09:50.324 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-2 >> "Content-Type: xxxxx"
15:09:50.330 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-2 << "Content-Type: xxxxx"
```

## Logs with Masked Condition Configured by using Java files
With below custom logging pattern, adding `CUSTOM-HTTP-CONSOLE-2` that will do masking functionality
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

    <appender name="CUSTOM-HTTP-CONSOLE-2"
              class="com.edw.config.CustomConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </layout>
    </appender>

    <logger name="com.edw" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>

    <logger name="org.apache.hc.client5.http.wire" level="DEBUG" additivity="false">
        <appender-ref ref="CUSTOM-HTTP-CONSOLE-2" />
    </logger>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

With this custom Java class,
```java
public class CustomConsoleAppender extends AppenderBase<ILoggingEvent> {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    String defaultPattern = "(.*Authorization:|.*Content-Type:) .*\"";
    String replacement = "$1 xxx\"";

    public CustomConsoleAppender() {
        super();
        start();
    }

    @Override
    public void append(ILoggingEvent iLoggingEvent) {
	String value = System.getenv("MASK_PATTERN");
        String pattern =  value != null ? value : defaultPattern;
        String modifiedFormattedMessage = iLoggingEvent.getFormattedMessage();
        modifiedFormattedMessage = modifiedFormattedMessage.replaceAll(pattern, replacement);

        System.out.println(
                simpleDateFormat.format(new Date()) + " [" +
                iLoggingEvent.getThreadName() + "] " +
                iLoggingEvent.getLevel() + " " +
                iLoggingEvent.getLoggerName() + " - " +
                modifiedFormattedMessage
        );
    }
}
```

The java class will get the value of the pattern from the environment variable. To run the test, first export the variable `MASK_PATTERN`

```
export MASK_PATTERN='(.*Authorization:) .*"'
mvn test |tee out.log
```

The result will be something like this, where `Authorization` header content is being masked
```
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "GET / HTTP/1.1[\r][\n]"
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Accept-Encoding: text/plain[\r][\n]"
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Accept-Charset: utf-8[\r][\n]"
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Authorization: xxxxx"
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Host: localhost:8080[\r][\n]"
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "Connection: keep-alive[\r][\n]"
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "User-Agent: Apache-HttpClient/5.1.4 (Java/17.0.6)[\r][\n]"
10:50:10.254 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 >> "[\r][\n]"
10:50:10.268 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "HTTP/1.1 200 OK[\r][\n]"
10:50:10.269 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Matched-Stub-Id: b2a2f65f-9b0a-4bdf-8736-0d6f80de7adb[\r][\n]"
10:50:10.269 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Transfer-Encoding: chunked[\r][\n]"
10:50:10.269 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "[\r][\n]"
10:50:10.273 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "b[\r][\n]"
10:50:10.273 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "Hello World[\r][\n]"
10:50:10.273 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "0[\r][\n]"
10:50:10.273 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-1 << "[\r][\n]"
```

You can also add more patterns. For example, if Content-Type is going to be masked, then:

```
export MASK_PATTERN='(.*Authorization:|.*Content-Type:) .*"'
```
