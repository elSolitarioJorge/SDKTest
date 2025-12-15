package com.example.sdktest2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.perfsdk.PerfConfig;
import com.example.perfsdk.PerfSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PerfSDK.init(getApplicationContext(), PerfConfig.defaultConfig());
        PerfSDK.start();

        PerfSDK.setEventListener(event -> {
            String label = "jank".equals(event.type) ? "JANK" : "ANR";
            String sev = severity(event.durationMs);
            android.widget.Toast.makeText(
                    this,
                    label + " " + event.durationMs + "ms"
                            + (event.droppedFrames > 0 ? (" / dropped " + event.droppedFrames) : "")
                            + " (" + sev + ")",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
        });

        findViewById(R.id.btn_jank).setOnClickListener(v -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        });

        findViewById(R.id.btn_anr).setOnClickListener(v -> {
            v.postDelayed(() -> {
                long start = android.os.SystemClock.uptimeMillis();
                while (android.os.SystemClock.uptimeMillis() - start < 6000) {
                    // busy loop
                }
            }, 1000);
        });

        findViewById(R.id.btn_show).setOnClickListener(v -> {
            startActivity(new Intent(this, EventListActivity.class));
        });
    }

    private String severity(long ms) {
        if (ms >= 5000) return "极重";
        if (ms >= 1000) return "重";
        if (ms >= 200) return "中";
        return "轻";
    }
}