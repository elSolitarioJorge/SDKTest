package com.example.sdktest2;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class EventParser {
    private static final SimpleDateFormat FMT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static UiEvent parseLine(String line) {
        try {
            JSONObject o = new JSONObject(line);

            UiEvent e = new UiEvent();
            e.type = o.optString("type");
            e.ts = o.optLong("ts");
            e.durationMs = o.optLong("durationMs");
            e.droppedFrames = o.optInt("droppedFrames");
            e.mainStack = o.optString("mainStack");

            e.timeText = FMT.format(new Date(e.ts));
            String label = "jank".equals(e.type) ? "JANK" : "ANR";
            e.titleText = label + " " + e.durationMs + "ms (" + severity(e.durationMs) + ")";
            e.subText = "jank".equals(e.type) ? ("dropped " + e.droppedFrames) : "main thread blocked";
            e.keyStackLine = pickKeyStackLine(e.mainStack);

            return e;
        } catch (Throwable t) {
            return null;
        }
    }

    private static String severity(long ms) {
        if (ms >= 5000) return "极重";
        if (ms >= 1000) return "重";
        if (ms >= 200) return "中";
        return "轻";
    }

    private static String pickKeyStackLine(String stack) {
        if (stack == null) return "";
        String[] lines = stack.split("\\\\n|\\n");
        for (String s : lines) {
            // 优先挑你自己的业务/SDK栈，避免全是系统栈
            if (s.contains("com.example")) return s;
        }
        // 没找到就返回前几行里非空的一行
        for (String s : lines) {
            if (s != null && s.trim().length() > 0) return s.trim();
        }
        return "";
    }
}
