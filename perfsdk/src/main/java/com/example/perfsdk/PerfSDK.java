package com.example.perfsdk;

import android.content.Context;

import com.example.perfsdk.anr.AnrWatchdog;
import com.example.perfsdk.jank.JankMonitor;
import com.example.perfsdk.store.EventStore;
import com.example.perfsdk.store.PerfEventListener;

public final class PerfSDK {
    private static volatile boolean inited = false;

    private static PerfConfig config;
    private static EventStore store;
    private static JankMonitor jankMonitor;
    private static AnrWatchdog anrWatchdog;
    private static volatile PerfEventListener listener;

    private PerfSDK() {}

    public static synchronized void init(Context context, PerfConfig cfg) {
        if (inited) return;
        inited = true;

        Context appCtx = context.getApplicationContext();
        config = (cfg == null) ? PerfConfig.defaultConfig() : cfg;

        store = new EventStore(appCtx);
        jankMonitor = new JankMonitor(store, config);
        anrWatchdog = new AnrWatchdog(store, config);
    }

    public static void start() {
        if (!inited) return;
        if (config.enableJank) jankMonitor.start();
        if (config.enableAnr) anrWatchdog.start();
    }

    public static void stop() {
        if (!inited) return;
        if (config.enableJank) jankMonitor.stop();
        if (config.enableAnr) anrWatchdog.stop();
    }

    public static EventStore getStore() {
        return store;
    }

    public static void setEventListener(PerfEventListener l) {
        listener = l;
    }

    public static PerfEventListener getEventListener() {
        return listener;
    }
}

