package com.github.grzegorzekkk.serverstatusnotifierbukkit.server;

import lombok.Data;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class ConsoleAppender extends AbstractAppender {
    private ConsoleObservable observer;
    private static final String ANSI_ESCAPE_CODE_REGEX = "\\x1B\\[[0-9;]*[JKmsu]";

    public ConsoleAppender() {
        super("consoleAppender", null, null);
    }

    @Override
    public void append(LogEvent event) {
        StringBuilder sb = new StringBuilder();
        String message = event.toImmutable().getMessage().getFormattedMessage().replaceAll(ANSI_ESCAPE_CODE_REGEX, "");
        String date = new SimpleDateFormat("HH:mm:ss").format(new Date(event.toImmutable().getTimeMillis()));
        String outputMessage = sb.append("[").append(date).append("]: ").append(message).toString();
        observer.onLogPublish(outputMessage);
    }

    public interface ConsoleObservable {
        void onLogPublish(String logMessage);
    }
}
