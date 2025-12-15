package com.example.perfsdk.anr;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.example.perfsdk.PerfConfig;
import com.example.perfsdk.store.EventStore;
import com.example.perfsdk.store.PerfEvent;

public final class AnrWatchdog {

    private final EventStore store;
    private final PerfConfig config;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private volatile long lastTickMs = 0;

    private Thread worker;
    private volatile boolean running = false;

    public AnrWatchdog(EventStore store, PerfConfig config) {
        this.store = store;
        this.config = config;
    }

    public void start() {
        if (running) return;
        running = true;
        lastTickMs = SystemClock.uptimeMillis();

        worker = new Thread(this::loop, "perf-anr-watchdog");
        worker.start();
    }

    public void stop() {
        running = false;
        if (worker != null) worker.interrupt();
    }

    private void loop() {
        while (running) {
            long now = SystemClock.uptimeMillis();

            // 往主线程投递“心跳”
            mainHandler.post(() -> lastTickMs = SystemClock.uptimeMillis());

            long gap = now - lastTickMs;
            if (gap >= config.anrThresholdMs) {
                store.write(PerfEvent.anr(gap, mainThreadStack()));
                waitRecover();
            }

            sleep(config.anrTickMs);
        }
    }

    private void waitRecover() {
        long start = SystemClock.uptimeMillis();
        while (running) {
            long gap = SystemClock.uptimeMillis() - lastTickMs;
            if (gap < 200) return;
            if (SystemClock.uptimeMillis() - start > 10_000) return;
            sleep(200);
        }
    }

    private String mainThreadStack() {
        StackTraceElement[] st = Looper.getMainLooper().getThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : st) sb.append(e).append('\n');
        return sb.toString();
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
