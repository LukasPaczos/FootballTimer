package com.lukaspaczos.footballtimer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyTimer extends Handler{
    private int secondsLow;
    private int secondsHigh;
    private int minutesLow;
    private int minutesHigh;
    private int halfLength;
    private int minutesPassed;
    private boolean isSecondHalf;
    private TextView secondsLowView;
    private TextView secondsHighView;
    private TextView minutesLowView;
    private TextView minutesHighView;
    private Context mainContext;
    private boolean isRunning;

    private final int MSG_START = 1;
    private final int MSG_CONTINUE = 2;
    private final int MSG_STOP = 3;

    public MyTimer(Context context) {
        super();
        secondsLow = -1;
        secondsHigh = 0;
        minutesLow = 0;
        minutesHigh = 0;
        minutesPassed = 0;
        mainContext = context;
        isRunning = false;
        isSecondHalf = false;
    }

    public void setHalfLength(int halfLength) {
        this.halfLength = halfLength;
    }

    public int getHalfLength() {
        return halfLength;
    }

    public int getMinutesPassed() {
        return minutesPassed;
    }

    public void start() {
        SharedPreferences sharedPreferences = mainContext.getSharedPreferences(
                mainContext.getString(R.string.preferences), Context.MODE_PRIVATE);
        this.halfLength = sharedPreferences.getInt(mainContext.getString(R.string.preferences_half_length), 45);

        Toast.makeText(mainContext, R.string.timer_started, Toast.LENGTH_SHORT).show();
        Toast.makeText(mainContext, String.format(mainContext
                .getString(R.string.change_of_half_length), halfLength),
                Toast.LENGTH_SHORT).show();

        Log.i("half length preference", String.valueOf(
                sharedPreferences.getInt(mainContext.getString(R.string.preferences_half_length), 45)));

        this.sendEmptyMessage(MSG_START);
    }

    public void stop() {
        Toast.makeText(mainContext, R.string.timer_stopped, Toast.LENGTH_SHORT).show();

        this.sendEmptyMessage(MSG_STOP);
    }

    public void reset() {
        secondsLow = -1;
        secondsHigh = 0;
        minutesLow = 0;
        minutesHigh = 0;
        minutesPassed = 0;
        isRunning = false;
        isSecondHalf = false;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_START:
                Log.i("Timer", "Started");
                isRunning = true;
                this.sendEmptyMessageDelayed(MSG_CONTINUE, 1000);
                break;
            case MSG_CONTINUE:
                this.sendEmptyMessageDelayed(MSG_CONTINUE, 1000);
                updateViews();
                break;
            case MSG_STOP:
                Log.i("Timer", "Stopped");
                isRunning = false;
                this.removeMessages(MSG_CONTINUE);
                minutesPassed = 0;
                break;
        }
    }

    public void updateViews() {
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
            if (isSecondHalf && isRunning) {
                stop();
                isSecondHalf = false;
                View rootView = ((Activity) mainContext).getWindow().getDecorView()
                        .findViewById(android.R.id.content);
                LinearLayout timerLayout = (LinearLayout) rootView.findViewById(R.id.timer_layout);
                timerLayout.setClickable(false);
                Toast.makeText(mainContext, R.string.match_ended, Toast.LENGTH_LONG).show();
            } else if (isRunning) {
                stop();
                isSecondHalf = true;
                Toast.makeText(mainContext, R.string.half_ended, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setViews(TextView secondsLowView, TextView secondsHighView,
                          TextView minutesLowView, TextView minutesHighView) {
        this.secondsLowView = secondsLowView;
        this.secondsHighView = secondsHighView;
        this.minutesLowView = minutesLowView;
        this.minutesHighView = minutesHighView;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public String getTime() {
        String time = minutesHighView.getText().toString()
                + minutesLowView.getText().toString() + ":" + secondsHighView.getText().toString()
        + secondsLowView.getText().toString();
        return time;
    }
}
