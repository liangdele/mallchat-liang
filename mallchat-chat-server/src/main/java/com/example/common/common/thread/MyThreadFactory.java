package com.example.common.common.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {
    private final static MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();
    private ThreadFactory original;

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = original.newThread(runnable);
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
