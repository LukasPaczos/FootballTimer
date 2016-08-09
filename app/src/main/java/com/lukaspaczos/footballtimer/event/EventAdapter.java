package com.lukaspaczos.footballtimer.event;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context c, List<Event> items) {
        super(c, 0, items);
        Event.setupIcons();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventView eventView = (EventView)convertView;
        if (null == eventView)
            eventView = EventView.inflate(parent);
        eventView.setItem(getItem(position));
        return eventView;
    }


}
