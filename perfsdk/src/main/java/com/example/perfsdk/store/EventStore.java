package com.example.perfsdk.store;

import static com.example.perfsdk.PerfSDK.getEventListener;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class EventStore {
    private final File file;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    public EventStore(Context ctx) {
        File dir = new File(ctx.getFilesDir(), "perf");
        if (!dir.exists()) dir.mkdirs();
        file = new File(dir, "events.jsonl");
    }

    public void write(final PerfEvent e) {
        io.execute(() -> {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(JsonUtil.toJson(e));
                bw.newLine();
            } catch (Throwable ignored) {}
        });

        PerfEventListener l = getEventListener();
        if (l != null) {
            mainHandler.post(() -> l.onEvent(e));
        }

    }

    public File getFile() {
        return file;
    }
}
