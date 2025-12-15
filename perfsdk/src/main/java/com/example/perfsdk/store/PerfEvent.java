package com.example.perfsdk.store;

public final class PerfEvent {
    public String type; // "jank" / "anr"
    public long ts;
    public long durationMs;
    public int droppedFrames;
    public String mainStack;

    public static PerfEvent jank(long durationMs, int droppedFrames, String mainStack) {
        PerfEvent e = new PerfEvent();
        e.type = "jank";
        e.ts = System.currentTimeMillis();
        e.durationMs = durationMs;
        e.droppedFrames = droppedFrames;
        e.mainStack = mainStack;
        return e;
    }

    public static PerfEvent anr(long durationMs, String mainStack) {
        PerfEvent e = new PerfEvent();
        e.type = "anr";
        e.ts = System.currentTimeMillis();
        e.durationMs = durationMs;
        e.droppedFrames = 0;
        e.mainStack = mainStack;
        return e;
    }
}
