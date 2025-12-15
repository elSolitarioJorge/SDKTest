package com.example.sdktest2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setTextSize(12f);
        tv.setPadding(16,16,16,16);
        setContentView(tv);

        String title = getIntent().getStringExtra("title");
        String stack = getIntent().getStringExtra("stack");
        tv.setText((title == null ? "" : title) + "\n\n" + (stack == null ? "" : stack));
    }
}
