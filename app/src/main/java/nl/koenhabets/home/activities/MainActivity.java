package nl.koenhabets.home.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import nl.koenhabets.home.api.ConfigApi;
import nl.koenhabets.home.api.Fish;
import nl.koenhabets.home.api.Lights;
import nl.koenhabets.home.R;
import nl.koenhabets.home.Receivers.AlarmReceiver;
import nl.koenhabets.home.api.RefillFood;
import nl.koenhabets.home.api.SurvurApi;
import nl.koenhabets.home.api.WebSockets;
import nl.koenhabets.home.Wol;
import nl.koenhabets.home.events.ConnectionEvent;
import nl.koenhabets.home.models.APIResponse;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    TextView textView5;
    TextView textViewWol;
    RequestQueue requestQueue;
    View parentLayout;

    Switch switch1;
    Switch switch2;
    Switch switch3;

    Button button;
    Button buttonWol;
    Button buttonRefillFood;
    private WebSockets websocket = new WebSockets();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //websocket.stopWebsocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //websocket.connectToServer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        parentLayout = findViewById(android.R.id.content);
        EventBus.getDefault().register(this);

        if (!WebSockets.returnConnected()) {
            websocket.connectToServer();
        }

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        requestQueue = Volley.newRequestQueue(this);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView5 = (TextView) findViewById(R.id.textView5);
        textViewWol = (TextView) findViewById(R.id.textViewWol);

        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);

        button = (Button) findViewById(R.id.button);
        buttonWol = (Button) findViewById(R.id.button2);
        buttonRefillFood = (Button) findViewById(R.id.buttonRefillFood);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!WebSockets.returnConnected()) {
                    websocket.reconnect();
                }
            }
        }, 5000, 10 * 1000);

        final SurvurApi request = new SurvurApi(new Response.Listener<APIResponse>() {
            @Override
            public void onResponse(APIResponse response) {
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);

        /*
        APIGraph apiGraphRequest = new APIGraph(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(apiGraphRequest);*/


        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch1.isChecked()) {
                    setLight("Aon");
                } else {
                    setLight("Aoff");
                }
            }
        });

        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch2.isChecked()) {
                    setLight("Bon");
                } else {
                    setLight("Boff");
                }
            }
        });


        switch3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch3.isChecked()) {
                    setLight("Con");
                } else {
                    setLight("Coff");
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fish fishRequest = new Fish(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.getMessage());
                    }
                });
                requestQueue.add(fishRequest);
            }
        });

        buttonWol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Wol wolRequest = new Wol(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.getMessage());
                    }
                });
                requestQueue.add(wolRequest);
            }
        });

        buttonRefillFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefillFood fishRefillRequest = new RefillFood(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.getMessage());
                    }
                });
                requestQueue.add(fishRefillRequest);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onData(APIResponse response) {
        parseData(response);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent response) {
        if (response.connected) {
            Snackbar.make(parentLayout, R.string.connected, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(parentLayout, R.string.disconnected, Snackbar.LENGTH_LONG).show();
        }
    }

    public void setLight(String code) {
        Lights lightRequest = new Lights(code, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(lightRequest);
    }

    public void setConfig(String thing, String status) {
        ConfigApi configApi = new ConfigApi(thing, status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(configApi);
    }

    private void parseData(APIResponse response) {
        textView.setText(getString(R.string.inside) + String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureInside()));
        textView2.setText(getString(R.string.outside) + String.format(Locale.getDefault(), "%.2f °C", response.getTemperatureOutside()));
        switch1.setChecked(response.getLightA());
        switch2.setChecked(response.getLightB());
        switch3.setChecked(response.getLightC());
        textView5.setText(getString(R.string.food) + response.getFishLastFed() + " Days left: " + response.getFoodDaysLeft());
        if (response.getPcOn()) {
            textViewWol.setText(R.string.ComputerAan);
        } else {
            textViewWol.setText(R.string.ComputerUit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}