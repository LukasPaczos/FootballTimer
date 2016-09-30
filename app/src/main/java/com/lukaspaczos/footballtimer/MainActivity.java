package com.lukaspaczos.footballtimer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lukaspaczos.footballtimer.event.Event;
import com.lukaspaczos.footballtimer.event.EventAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//TODO comment code
public class MainActivity extends AppCompatActivity {

    private MyTimer myTimer;
    private ListView listView;
    private ArrayList<Event> events;
    private EventAdapter adapter;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));

        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //TODO remove before store upload
        //adView.loadAd(adRequest);

        myTimer = new MyTimer(this);
        myTimer.setViews((TextView) findViewById(R.id.seconds_low), (TextView) findViewById(R.id.seconds_high),
                (TextView) findViewById(R.id.minutes_low), (TextView) findViewById(R.id.minutes_high));
        myTimer.updateViews();

        events = new ArrayList<>();

        listView = (ListView) findViewById(R.id.event_list);
        adapter = new EventAdapter(this, events);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

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
                        }
                    });
                } else {
                    dialogBuilder.setTitle(R.string.start_timer);
                    dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myTimer.start();
                            dialogInterface.dismiss();
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
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
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
        if (id == R.id.action_reset) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.reset_confirmation))
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            events.clear();
                            adapter.notifyDataSetChanged();
                            myTimer.stop();
                            myTimer.reset();
                            myTimer.updateViews();
                            Toast.makeText(MainActivity.this, getString(R.string.reset_done), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setCancelable(true)
                    .show();
            return true;
        }
        if (id == R.id.action_settings) {
            final SharedPreferences sharedPref = this.getSharedPreferences(this.getResources().getString(R.string.preferences), Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.choose_half_length))
                    .setSingleChoiceItems(getResources()
                            .getStringArray(R.array.settings_half_length), 0, null)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            switch (selectedPosition) {
                                case 0:
                                    editor.putInt(MainActivity.this.getResources()
                                            .getString(R.string.preferences_half_length), 45);
                                    myTimer.setHalfLength(45);
                                    Toast.makeText(MainActivity.this, String.format(getString(
                                            R.string.half_length_changed), 45), Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    editor.putInt(MainActivity.this.getResources()
                                            .getString(R.string.preferences_half_length), 30);
                                    myTimer.setHalfLength(30);
                                    Toast.makeText(MainActivity.this, String.format(getString(
                                            R.string.half_length_changed), 30), Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    editor.putInt(MainActivity.this.getResources()
                                            .getString(R.string.preferences_half_length), 20);
                                    myTimer.setHalfLength(20);
                                    Toast.makeText(MainActivity.this, String.format(getString(
                                            R.string.half_length_changed), 20), Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    editor.putInt(MainActivity.this.getResources()
                                            .getString(R.string.preferences_half_length), 15);
                                    myTimer.setHalfLength(15);
                                    Toast.makeText(MainActivity.this, String.format(getString(
                                            R.string.half_length_changed), 15), Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    editor.putInt(MainActivity.this.getResources()
                                            .getString(R.string.preferences_half_length), 10);
                                    myTimer.setHalfLength(10);
                                    Toast.makeText(MainActivity.this, String.format(getString(
                                            R.string.half_length_changed), 10), Toast.LENGTH_SHORT).show();
                                    break;
                                case 5:
                                    editor.putInt(MainActivity.this.getResources()
                                            .getString(R.string.preferences_half_length), 5);
                                    myTimer.setHalfLength(5);
                                    Toast.makeText(MainActivity.this, String.format(getString(
                                            R.string.half_length_changed), 5), Toast.LENGTH_SHORT).show();
                                    break;
                                case 6:
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                    final EditText inputView = new EditText(MainActivity.this);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    inputView.setLayoutParams(lp);
                                    inputView.setRawInputType(Configuration.KEYBOARD_12KEY);
                                    inputView.setSingleLine(true);
                                    inputView.setMaxLines(1);
                                    inputView.setLines(1);
                                    dialogBuilder.setView(inputView);
                                    dialogBuilder.setCancelable(true);
                                    dialogBuilder.setTitle(getString(R.string.write_half_length));
                                    dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            int time = Double.valueOf(inputView.getText().toString()).intValue();
                                            editor.putInt(MainActivity.this.getResources()
                                                            .getString(R.string.preferences_half_length),
                                                    time);
                                            editor.apply();
                                            myTimer.setHalfLength(time);
                                            Toast.makeText(MainActivity.this, String.format(getString(
                                                    R.string.half_length_changed), time), Toast.LENGTH_SHORT).show();
                                            Log.i("half length preference", String.valueOf(
                                                    sharedPref.getInt(getString(R.string.preferences_half_length), 45)));
                                        }
                                    });
                                    AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                    alertDialog.show();
                                    break;
                            }
                            editor.apply();
                            Log.i("half length preference", String.valueOf(
                                    sharedPref.getInt(getString(R.string.preferences_half_length), 45)));
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
            return true;
        }
        if (id == R.id.action_about) {
            final TextView textView = (TextView) getLayoutInflater().inflate(R.layout.about_view, null, false);
            textView.setText(getText(R.string.about_text));
            textView.setMovementMethod(new ScrollingMovementMethod());
            new AlertDialog.Builder(this)
                    .setView(textView)
                    .setTitle(getString(R.string.action_about))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
            return true;
        }
        if (id == R.id.action_export) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.export_confirmation))
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                            String date = df.format(Calendar.getInstance().getTime());
                            Log.i("date", date);
                            String filename = date + ".json";
                            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/FootballTimerReports");

                            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                            if (isSDPresent) {
                                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                                long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
                                long megAvailable = bytesAvailable / 1048576;
                                if (megAvailable >= 1) {
                                    //directory = new File(Environment.getExternalStorageDirectory().getPath() + "/FootballTimerReports/");
                                } else {
                                    Toast.makeText(MainActivity.this, getString(
                                            R.string.export_failed_no_space), Toast.LENGTH_SHORT).show();
                                }
                            }
                            try {
                                //TODO thise shitte
                                Log.i("save_path", directory.getPath());
                                boolean success = true;
                                if (!directory.exists()) {
                                    Log.i("save_directory", "creating");
                                    success = directory.mkdirs();
                                }
                                if (success) {
                                    File outputFile = new File(directory, filename);
                                    FileWriter writer = new FileWriter(outputFile);
                                    Gson gson = new GsonBuilder().create();
                                    gson.toJson(events, writer);
                                    writer.flush();
                                    writer.close();
                                    Log.i("report", "saved on SD");
                                    Toast.makeText(MainActivity.this, getString(
                                            R.string.export_saved), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, getString(
                                            R.string.export_failed_no_directory), Toast.LENGTH_SHORT).show();
                                    Log.i("save_directory", "couldnt create");
                                }
                            } catch (IOException e) {
                                Log.i("IOException", e.getMessage());
                                Toast.makeText(MainActivity.this, getString(
                                        R.string.export_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setCancelable(true)
                    .show();
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
        inputView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.event_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_event_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Event event = adapter.getItem(info.position);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog alertDialog;
        switch (item.getItemId()) {
            case R.id.menu_event_list_edit:
                final EditText inputView = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputView.setLayoutParams(lp);
                inputView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
                dialogBuilder.setCancelable(true);
                dialogBuilder.setTitle(getResources().getString(R.string.event_edit));
                dialogBuilder.setView(inputView);
                dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        event.setMsg(inputView.getText().toString());
                        adapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });
                alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.menu_event_list_delete:
                dialogBuilder.setCancelable(true);
                dialogBuilder.setTitle(getResources().getString(R.string.event_delete));
                dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        events.remove(info.position);
                        adapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });
                alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alertDialog.show();
                break;
        }
        return true;
    }
}
