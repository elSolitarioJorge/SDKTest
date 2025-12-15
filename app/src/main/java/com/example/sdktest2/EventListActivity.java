package com.example.sdktest2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfsdk.PerfSDK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private TextView tvSummary;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        tvSummary = findViewById(R.id.tv_summary);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<UiEvent> list = loadEvents();
        EventSummary summary = calcSummary(list);

        tvSummary.setText(buildSummaryText(summary));

        rv.setAdapter(new EventAdapter(list, e -> {
            Intent it = new Intent(this, EventDetailActivity.class);
            it.putExtra("title", e.titleText);
            it.putExtra("stack", e.mainStack);
            startActivity(it);
        }));
    }

    private List<UiEvent> loadEvents() {
        List<UiEvent> out = new ArrayList<>();
        File f = PerfSDK.getStore().getFile();
        if (f == null || !f.exists()) return out;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                UiEvent e = EventParser.parseLine(line);
                if (e != null) out.add(e);
            }
        } catch (Throwable ignored) {}
        return out;
    }

    private EventSummary calcSummary(List<UiEvent> list) {
        EventSummary s = new EventSummary();
        for (UiEvent e : list) {
            if ("jank".equals(e.type)) s.jankCount++;
            if ("anr".equals(e.type)) s.anrCount++;
            if (s.maxDurationEvent == null || e.durationMs > s.maxDurationEvent.durationMs) {
                s.maxDurationEvent = e;
            }
        }
        return s;
    }

    private String buildSummaryText(EventSummary s) {
        String max = (s.maxDurationEvent == null) ? "无" :
                (("jank".equals(s.maxDurationEvent.type) ? "JANK" : "ANR")
                        + " " + s.maxDurationEvent.durationMs + "ms"
                        + (s.maxDurationEvent.droppedFrames > 0 ? (" / dropped " + s.maxDurationEvent.droppedFrames) : "")
                );
        return "Summary\n"
                + "JANK: " + s.jankCount + "\n"
                + "ANR: " + s.anrCount + "\n"
                + "Max: " + max;
    }
}
