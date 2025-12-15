package com.example.perfsdk.store;

public final class JsonUtil {
    private JsonUtil() {}

    public static String toJson(PerfEvent e) {
        return "{"
                + "\"type\":\"" + safe(e.type) + "\","
                + "\"ts\":" + e.ts + ","
                + "\"durationMs\":" + e.durationMs + ","
                + "\"droppedFrames\":" + e.droppedFrames + ","
                + "\"mainStack\":\"" + escape(e.mainStack) + "\""
                + "}";
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}

