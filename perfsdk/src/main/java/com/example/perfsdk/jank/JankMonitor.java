package com.example.perfsdk.jank;

import android.os.Looper;
import android.view.Choreographer;

import com.example.perfsdk.PerfConfig;
import com.example.perfsdk.store.EventStore;
import com.example.perfsdk.store.PerfEvent;

public final class JankMonitor implements Choreographer.FrameCallback {

    private final EventStore store;
    private final PerfConfig config;

    private boolean running = false;
    private long lastFrameNs = 0;

    public JankMonitor(EventStore store, PerfConfig config) {
        this.store = store;
        this.config = config;
    }

    public void start() {
        if (running) return;
        running = true;
        lastFrameNs = 0;
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void stop() {
        running = false;
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (!running) return;

        if (lastFrameNs != 0) {
            long deltaMs = (frameTimeNanos - lastFrameNs) / 1_000_000;
            if (deltaMs >= config.jankThresholdMs) {
                int dropped = (int) Math.max(0, (deltaMs / config.frameIntervalMs) - 1);
                store.write(PerfEvent.jank(deltaMs, dropped, mainThreadStack()));
            }
        }

        lastFrameNs = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
    }

    private String mainThreadStack() {
        StackTraceElement[] st = Looper.getMainLooper().getThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : st) sb.append(e).append('\n');
        return sb.toString();
    }
}
