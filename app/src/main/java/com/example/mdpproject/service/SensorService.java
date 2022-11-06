package com.example.mdpproject.service;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.example.mdpproject.receiver.AlarmReceiver;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Calendar;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounter;
    private int user_steps;
    private int user_goal;
    private int global_goal;

    SharedPreferences sharedpreferences ;
    SharedPreferences.Editor editor;

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
                updateSensorValue(user_steps);
            }
        }
    };

    void updateSensorValue(int value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("SENSOR_STEPS", value); // Storing integer
        editor.apply(); // commit changes
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate SERVICE ! ");

        sharedpreferences = getSharedPreferences("SENSOR_DATA_PREF", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        user_steps =  sharedpreferences.getInt("SENSOR_STEPS", 0);
        Log.d(TAG, "onCreate: STEPSSSSSSS"+user_steps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        //connectToMQTT();
        //createAlarm();
        getDataFromMQTT();
        getUserGoal();


        registerReceiver(broadcastReceiver, new IntentFilter(AlarmReceiver.ACTION_ALARM_SET));



    }



    @Override
    public void onDestroy() {

        sensorManager.unregisterListener(this, stepCounter);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    //https://stackoverflow.com/questions/48124195/how-to-schedule-a-task-every-night-at-12-am
    public void createAlarm() {
        //System request code
        int DATA_FETCHER_RC = 123;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //Create the time of day you would like it to go off. Use a calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 58);

        //Create an intent that points to the receiver. The system will notify the app about the current time, and send a broadcast to the app
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DATA_FETCHER_RC,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //initialize the alarm by using inexactrepeating. This allows the system to scheduler your alarm at the most efficient time around your
        //set time, it is usually a few seconds off your requested time.
        // you can also use setExact however this is not recommended. Use this only if it must be done then.

        //Also set the interval using the AlarmManager constants
        mAlarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    public void connectToMQTT(){
        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();

//            //MQTT Version
//            MqttConnectOptions options = new MqttConnectOptions();
//            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
//            IMqttToken token = client.connect(options);
//
//            //MQTT LWT
//            String topic = "users/last/will";
//            byte[] payload = "some payload".getBytes();
//            options.setWill(topic, payload ,1,false);
//            IMqttToken token = client.connect(options);
//
//            //MQTT password username
//            options.setUserName("USERNAME");
//            options.setPassword("PASSWORD".toCharArray());
//
//            IMqttToken token = client.connect(options);


            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    };

    public void getDataFromMQTT(){
        handleBroadcast(ACTION_GLOBAL_GOAL,10000);
    };

    public void pushDataToMQTT(int value){
        Log.d(TAG, "pushDataToMQTT: Sending data to MQTT:"+value);
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
                pushDataToMQTT(user_steps);
                handleBroadcast(ACTION_STEP_VALUE,user_steps);
                Log.d(TAG, "onSensorChanged: "+user_steps);
                break;
        }
    }

    void handleBroadcast(String ACTION,int value){
        final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_DATA_VALUE,value);
        sendBroadcast(intent);
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}