package com.lukaspaczos.footballtimer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyTimer myTimer;
    private ListView listView;

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

        listView = (ListView) findViewById(R.id.event_list);


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
}
