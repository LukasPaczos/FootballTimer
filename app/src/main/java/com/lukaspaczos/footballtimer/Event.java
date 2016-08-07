package com.lukaspaczos.footballtimer;

public class Event {
    public static final int YELLOW_CARD = 0;
    public static final int RED_CARD = 1;
    public static final int GOAL = 2;
    public static final int OTHER = 3;

    private String time;
    private int type;
    private String msg;

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
}
