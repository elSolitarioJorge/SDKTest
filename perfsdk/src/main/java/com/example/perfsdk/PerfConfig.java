package com.example.perfsdk;

public final class PerfConfig {
    public final boolean enableJank;
    public final boolean enableAnr;

    // Jank
    public final long jankThresholdMs;   // 帧间隔超过这个值算“明显卡顿”
    public final long frameIntervalMs;   // 16ms 简化

    // ANR
    public final long anrThresholdMs;    // 主线程多久不响应算疑似 ANR
    public final long anrTickMs;         // watchdog 检测间隔

    public PerfConfig(boolean enableJank, boolean enableAnr,
                      long jankThresholdMs, long frameIntervalMs,
                      long anrThresholdMs, long anrTickMs) {
        this.enableJank = enableJank;
        this.enableAnr = enableAnr;
        this.jankThresholdMs = jankThresholdMs;
        this.frameIntervalMs = frameIntervalMs;
        this.anrThresholdMs = anrThresholdMs;
        this.anrTickMs = anrTickMs;
    }

    public static PerfConfig defaultConfig() {
        return new PerfConfig(
                true,
                true,
                100,  // 演示效果好：>100ms 记录一条卡顿事件
                16,
                5000, // 5s
                1000  // 1s tick
        );
    }
}

