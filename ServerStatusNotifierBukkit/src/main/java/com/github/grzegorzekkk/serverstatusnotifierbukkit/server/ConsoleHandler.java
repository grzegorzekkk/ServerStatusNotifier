package com.github.grzegorzekkk.serverstatusnotifierbukkit.server;

import lombok.Data;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@Data
public class ConsoleHandler extends Handler {
    private ConsoleObservable observer;

    @Override
    public void publish(LogRecord record) {
        observer.onRecordPublish(record);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {
        setLevel(Level.OFF);
    }

    public interface ConsoleObservable {
        void onRecordPublish(LogRecord record);
    }
}
