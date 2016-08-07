package com.lukaspaczos.footballtimer;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Lukas Paczos on 07-Aug-16.
 */
public class MyTimer extends Handler{
    private int secondsLow;
    private int secondsHigh;
    private int minutesLow;
    private int minutesHigh;
    private int halfLength;
    private int minutesPassed;
    private TextView secondsLowView;
    private TextView secondsHighView;
    private TextView minutesLowView;
    private TextView minutesHighView;
    private Context mainContext;

    private final int MSG_START = 1;
    private final int MSG_CONTINUE = 2;
    private final int MSG_STOP = 3;

    public MyTimer(int halfLength, Context context) {
        super();
        secondsLow = -1;
        secondsHigh = 0;
        minutesLow = 0;
        minutesHigh = 0;
        this.halfLength = halfLength;
        minutesPassed = 0;
        mainContext = context;
        View rootView = ((Activity)mainContext).getWindow().getDecorView().findViewById(android.R.id.content);
        secondsLowView = (TextView) rootView.findViewById(R.id.seconds_low);
        secondsHighView = (TextView) rootView.findViewById(R.id.seconds_high);
        minutesLowView = (TextView) rootView.findViewById(R.id.minutes_low);
        minutesHighView = (TextView) rootView.findViewById(R.id.minutes_high);
        updateViews();
    }

    public void start() {
        this.sendEmptyMessage(MSG_START);
    }

    public void stop() {
        this.sendEmptyMessage(MSG_STOP);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_START:
                Log.i("Timer", "Started");
                this.sendEmptyMessageDelayed(MSG_CONTINUE, 1000);
                break;
            case MSG_CONTINUE:
                this.sendEmptyMessageDelayed(MSG_CONTINUE, 1000);
                updateViews();
                break;
            case MSG_STOP:
                Log.i("Timer", "Stopped");
                this.removeMessages(MSG_CONTINUE);
                minutesPassed = 0;
                break;
        }
    }

    private void updateViews() {
        secondsLow++;
        if (secondsLow > 9) {
            secondsLow = 0;
            secondsHigh++;
            if (secondsHigh > 5) {
                secondsHigh = 0;
                minutesLow++;
                minutesPassed++;
                if (minutesLow > 9) {
                    minutesLow = 0;
                    minutesHigh++;
                }
            }
        }
        ((Activity) mainContext).runOnUiThread(new Runnable() {
            public void run() {
                secondsLowView.setText(String.valueOf(secondsLow));
                secondsHighView.setText(String.valueOf(secondsHigh));
                minutesLowView.setText(String.valueOf(minutesLow));
                minutesHighView.setText(String.valueOf(minutesHigh));
            }
        });
        /*int time = Integer.valueOf(minutesHighView.getText().toString()
                + minutesLowView.getText().toString());*/
        if (minutesPassed == halfLength) {
            stop();
        }
    }
}
