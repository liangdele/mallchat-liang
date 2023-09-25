package com.example.common.common.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Exception in thread {} ", thread.getName(), throwable);
    }
}
