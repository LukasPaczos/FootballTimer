package com.lukaspaczos.footballtimer.event;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lukaspaczos.footballtimer.R;

public class EventView extends LinearLayout{
    private TextView timeView;
    private TextView msgView;
    private ImageView iconView;

    public EventView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.event_view_children, this, true);
        setupChildren();
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.event_view_children, this, true);
        setupChildren();
    }

    public EventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.event_view_children, this, true);
        setupChildren();
    }

    public static EventView inflate(ViewGroup parent) {
        EventView eventView = (EventView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_view, parent, false);
        return eventView;
    }

    private void setupChildren() {
        timeView = (TextView) findViewById(R.id.event_time);
        iconView = (ImageView) findViewById(R.id.event_icon);
        msgView = (TextView) findViewById(R.id.event_msg);
    }

    public void setItem(Event item) {
        timeView.setText(item.getTime());
        msgView.setText(item.getMsg());
        iconView.setImageResource(Event.iconsMap.get(item.getType()));
    }
}
