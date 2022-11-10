package com.example.mdpproject.service;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.mdpproject.receiver.AlarmReceiver;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounter;
    private int user_steps;
    private int user_goal;
    private boolean user_goal_complete = false;
    private int global_goal;

    final Handler handler = new Handler();
    final int delay = 300000; // 1000 milliseconds == 1 second  = 5mins

    MqttAndroidClient client;
    String clientId;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    LocationManager locationManager;


    public final static String ACTION_STEP_VALUE =
            "com.example.mdpproject.service.ACTION_STEP_VALUE";
    public final static String ACTION_USER_GOAL =
            "com.example.mdpproject.service.ACTION_USER_GOAL";
    public final static String ACTION_GLOBAL_GOAL =
            "com.example.mdpproject.service.ACTION_GLOBAL_GOAL";
    public final static String EXTRA_DATA_VALUE =
            "com.example.mdpproject.service.EXTRA_DATA_VALUE";

    public SensorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AlarmReceiver.ACTION_ALARM_SET)) {
                Log.d(TAG, "Alarm Set In Service !");
                user_steps = 0;
                user_goal_complete = false;
                updateSensorValue(user_steps);
            }
        }
    };

    void updateSensorValue(int value) {
        if(user_steps>=user_goal&&!user_goal_complete){
            Location location = getCurrentLocation();
            user_goal_complete = true;

            Log.d(TAG, "GOAL COMPLETE: "+location);

        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("SENSOR_STEPS", value); // Storing integer
        editor.apply(); // commit changes
    }

    @Override
    public void onCreate() {
        sharedpreferences = getSharedPreferences("SENSOR_DATA_PREF", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);


        registerReceiver(broadcastReceiver, new IntentFilter(AlarmReceiver.ACTION_ALARM_SET));


        getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);






        user_steps = sharedpreferences.getInt("SENSOR_STEPS", 0);
        Log.d(TAG, "onCreate: STEPSSSSSSS" + user_steps);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                subscribeToMQTT();
                getUserGoal();
            }
        }, 3000);




        //getCurrentLocation();

        connectToMQTT();
        //createAlarm();






    }

    Location getCurrentLocation() {
        //https://stackoverflow.com/questions/57863500/how-can-i-get-my-current-location-in-android-using-gps
        try {
            @SuppressLint("MissingPermission")
            Location gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "onCreate: Last loc "+ gps_loc);
            return gps_loc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void onDestroy() {

        sensorManager.unregisterListener(this, stepCounter);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    //https://stackoverflow.com/questions/48124195/how-to-schedule-a-task-every-night-at-12-am
    public void createAlarm() {
        int DATA_FETCHER_RC = 123;
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 58);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DATA_FETCHER_RC,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void connectToMQTT(){
        //https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service/
        clientId = MqttClient.generateClientId(); //paho42344242777022
        Log.d(TAG, "connectToMQTT: "+clientId);

        client = new MqttAndroidClient(this.getApplicationContext(),"ssl://98ffc5f1d5b742ea97a55790d35f07da.s1.eu.hivemq.cloud:8883" , clientId);

        try {


//            //MQTT Version
            MqttConnectOptions options = new MqttConnectOptions();
           // options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

//
            //MQTT LWT
            String topic = "users/steps";
            byte[] payload = "offline".getBytes();
            options.setWill(topic, payload ,1,true);


            //MQTT password username
            options.setUserName("miotuser");
            options.setPassword("miotpassword".toCharArray());

            IMqttToken token = client.connect(options);


            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "MQTT CONNECT onSuccess");
                    subscribeToMQTT();


                    handler.postDelayed(new Runnable() {
                        public void run() {
                            System.out.println("myHandler: here!"); // Do your work here
                            publishToMQTT(user_steps);
                            handler.postDelayed(this, delay);
                        }
                    }, delay);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "MQTT CONNECT onFailure");
                    Log.d(TAG, exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    };

    public void subscribeToMQTT(){


        handleBroadcast(ACTION_GLOBAL_GOAL,10000);
        global_goal = 10000;

//        String topic = "users/steps";
//        int qos = 1;
//        try {
//            IMqttToken subToken = client.subscribe(topic, qos);
//            subToken.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    // The message was published
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken,
//                                      Throwable exception) {
//                    // The subscription could not be performed, maybe the user was not
//                    // authorized to subscribe on the specified topic e.g. using wildcards
//
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
    };

    public void publishToMQTT(int value){
        Log.d(TAG, "pushDataToMQTT: Sending data to MQTT:"+value);
        String topic = "users/steps";
        String payload = Integer.toString(value);
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    };

    public void getUserGoal(){
        user_goal =  sharedpreferences.getInt("USER_GOAL", 5000);
        handleBroadcast(ACTION_USER_GOAL,user_goal);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                user_steps+=1;
                updateSensorValue(user_steps);
                //publishToMQTT(user_steps);
                handleBroadcast(ACTION_STEP_VALUE,user_steps);
                Log.d(TAG, "onSensorChanged: "+user_steps);
                break;
        }
    }

    void handleBroadcast(String ACTION,int value){
        final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_DATA_VALUE,value);
        sendBroadcast(intent);
        Log.d(TAG, "handleBroadcast: Action ="+ACTION+" Value:"+value);
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}