package com.example.sdktest2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {

    public interface OnItemClick {
        void onClick(UiEvent e);
    }

    private final List<UiEvent> list;
    private final OnItemClick click;

    public EventAdapter(List<UiEvent> list, OnItemClick click) {
        this.list = list;
        this.click = click;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        UiEvent e = list.get(position);
        h.title.setText(e.timeText + "  " + e.titleText);
        h.sub.setText(e.subText);
        h.stack.setText(e.keyStackLine);

        h.itemView.setOnClickListener(v -> {
            if (click != null) click.onClick(e);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, sub, stack;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            sub = itemView.findViewById(R.id.tv_sub);
            stack = itemView.findViewById(R.id.tv_stack);
        }
    }
}
