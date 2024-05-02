package com.edw.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 *     com.edw.config.CustomConsoleAppender
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 02 Mei 2024 08:39
 */
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
