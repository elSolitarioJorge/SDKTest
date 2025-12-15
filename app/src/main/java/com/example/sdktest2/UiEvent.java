package com.example.sdktest2;

public class UiEvent {
    public String type;
    public long ts;
    public long durationMs;
    public int droppedFrames;
    public String mainStack;

    public String timeText;      // 12:03:24
    public String titleText;     // JANK 158ms (中)
    public String subText;       // dropped 8
    public String keyStackLine;  // 关键堆栈行
}
