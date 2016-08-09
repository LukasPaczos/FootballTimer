package com.lukaspaczos.footballtimer.event;

import com.lukaspaczos.footballtimer.R;

import java.util.HashMap;
import java.util.Map;

public class Event {
    public static final int YELLOW_CARD = 0;
    public static final int RED_CARD = 1;
    public static final int GOAL = 2;
    public static final int OTHER = 3;

    private String time;
    private int type;
    private String msg;
    public static Map<Integer, Integer> iconsMap = new HashMap<>();

    public Event(String time, int type, String msg) {
        this.time = time;
        this.type = type;
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static void setupIcons() {
        iconsMap.put(YELLOW_CARD, R.mipmap.ic_yellow_card);
        iconsMap.put(RED_CARD, R.mipmap.ic_red_card);
        iconsMap.put(GOAL, R.mipmap.ic_goal);
        iconsMap.put(OTHER, R.mipmap.ic_other);
    }
}
