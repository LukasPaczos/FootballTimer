package com.lukaspaczos.footballtimer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lukaspaczos.footballtimer.event.Event;
import com.lukaspaczos.footballtimer.event.EventAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyTimer myTimer;
    private ListView listView;
    private ArrayList<Event> events;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myTimer = new MyTimer(2, this);
        myTimer.setViews((TextView) findViewById(R.id.seconds_low), (TextView) findViewById(R.id.seconds_high),
                (TextView) findViewById(R.id.minutes_low), (TextView) findViewById(R.id.minutes_high));
        myTimer.updateViews();

        events = new ArrayList<>();

        listView = (ListView) findViewById(R.id.event_list);
        adapter = new EventAdapter(this, events);
        listView.setAdapter(adapter);

        LinearLayout timerLayout = (LinearLayout) findViewById(R.id.timer_layout);
        timerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                if (myTimer.isRunning()) {
                    dialogBuilder.setTitle(R.string.stop_timer);
                    dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myTimer.stop();
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Timer stopped.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialogBuilder.setTitle(R.string.start_timer);
                    dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myTimer.start();
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Timer started.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        ImageView buttonYellowCard = (ImageView) findViewById(R.id.button_yellow_card);
        buttonYellowCard.setOnClickListener(new buttonOnClickListener());

        ImageView buttonRedCard = (ImageView) findViewById(R.id.button_red_card);
        buttonRedCard.setOnClickListener(new buttonOnClickListener());

        ImageView buttonGoal = (ImageView) findViewById(R.id.button_goal);
        buttonGoal.setOnClickListener(new buttonOnClickListener());

        ImageView buttonOther = (ImageView) findViewById(R.id.button_other);
        buttonOther.setOnClickListener(new buttonOnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class buttonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.button_yellow_card) {
                addToEventList(myTimer.getTime(), Event.YELLOW_CARD);
            } else if (view.getId() == R.id.button_red_card) {
                addToEventList(myTimer.getTime(), Event.RED_CARD);
            } else if (view.getId() == R.id.button_goal) {
                addToEventList(myTimer.getTime(), Event.GOAL);
            } else if (view.getId() == R.id.button_other) {
                addToEventList(myTimer.getTime(), Event.OTHER);
            }
        }
    }

    private void addToEventList(final String time, final int type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final EditText inputView = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputView.setLayoutParams(lp);
        switch (type) {
            case Event.YELLOW_CARD:
                dialogBuilder.setTitle(R.string.dialog_yellow_card_title);
                break;
            case Event.RED_CARD:
                dialogBuilder.setTitle(R.string.dialog_red_card_title);
                break;
            case Event.GOAL:
                dialogBuilder.setTitle(R.string.dialog_goal_title);
                break;
            case Event.OTHER:
                dialogBuilder.setTitle(R.string.dialog_other_title);
                break;
        }
        dialogBuilder.setView(inputView);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                events.add(0, new Event(time, type, inputView.getText().toString()));
                adapter.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
}
