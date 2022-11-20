package com.example.mdpproject.service;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.mdpproject.db.DBHelper;
import com.example.mdpproject.db.DailyInfo;
import com.example.mdpproject.receiver.AlarmReceiver;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounter;
    private int user_steps;
    private int user_goal;
    private boolean user_goal_complete = false;

    MqttAndroidClient client;
    DBHelper dbHelper;
    String clientId;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    LocationManager locationManager;

    int text_to_speak_threshold = 20;

    TextToSpeech t1;

    public final static String ACTION_STEP_VALUE = "com.example.mdpproject.service.ACTION_STEP_VALUE";
    public final static String ACTION_USER_GOAL = "com.example.mdpproject.service.ACTION_USER_GOAL";
    public final static String ACTION_GLOBAL_GOAL = "com.example.mdpproject.service.ACTION_GLOBAL_GOAL";
    public final static String ACTION_ALARM = "com.example.mdpproject.service.ACTION_ALARM";
    public final static String EXTRA_DATA_VALUE = "com.example.mdpproject.service.EXTRA_DATA_VALUE";

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
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (AlarmReceiver.ACTION_ALARM_SET.equals(action)) {
                persistData();
                Log.d(TAG, "Alarm Set In Service!");
                user_steps = 0;
                user_goal_complete = false;
                text_to_speak_threshold = 20;
                updateSensorValue(user_steps);
                handleBroadcast(ACTION_ALARM, 0);
            }
        }
    };

    private void persistData() {
        DailyInfo dailyInfo;
        try {
            dailyInfo = dbHelper.getDailyInfoByDate(new Date());
            if (dailyInfo != null) {
                dailyInfo.setSteps(user_steps);
                dbHelper.updateDailyInfo(dailyInfo);
            } else {
                dailyInfo = new DailyInfo();
                dailyInfo.setSteps(user_steps);
                dailyInfo.setDate(new Date());
                dbHelper.addDailyInfo(dailyInfo);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            dailyInfo = new DailyInfo();
            dailyInfo.setSteps(user_steps);
            dailyInfo.setDate(new Date());
            dbHelper.addDailyInfo(dailyInfo);
        }
    }

    void updateSensorValue(int value) {
        if (user_steps >= user_goal && !user_goal_complete) {
            Location location = getCurrentLocation();
            user_goal_complete = true;
            t1.speak("Congratulations on Completing your Goal", TextToSpeech.QUEUE_FLUSH, null);
            Log.d(TAG, "GOAL COMPLETE: " + location);

            DailyInfo dailyInfo = new DailyInfo();
            dailyInfo.setGoalReached(true);
            dailyInfo.setDate(new Date());
            dailyInfo.setLongitude(String.valueOf(location.getLongitude()));
            dailyInfo.setLatitude(String.valueOf(location.getLatitude()));
            dailyInfo.setSteps(user_steps);
            dbHelper.addDailyInfo(dailyInfo);
        }

        int percentage = (int) Math.floor(((float) user_steps / (float) user_goal) * 100f);
        Log.d(TAG, "updateSensorValue: PERCENT " + percentage);
        if (percentage % 20 == 0 && percentage < 100) {
            text_to_speak_threshold = percentage + 20;
            editor.putInt("text_to_speak_threshold", text_to_speak_threshold); // Storing integer
            Log.d(TAG, "updateSensorValue: TEXT TO SPEECH " + percentage);
            t1.speak("You have reached " + percentage + " %", TextToSpeech.QUEUE_FLUSH, null);
        }
        editor.putInt("sensor_step", value); // Storing integer
        editor.apply(); // commit changes
    }

    @Override
    public void onCreate() {
        sharedpreferences = getSharedPreferences("shared-pref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        dbHelper = new DBHelper(this.getApplicationContext());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        registerReceiver(broadcastReceiver, new IntentFilter(AlarmReceiver.ACTION_ALARM_SET));

        getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        clientId = MqttClient.generateClientId(); //paho42344242777022

        user_steps = sharedpreferences.getInt("sensor_step", 0);
        user_goal = sharedpreferences.getInt("goal", 0);
        text_to_speak_threshold = sharedpreferences.getInt("text_to_speak_threshold", 20);

        Log.d(TAG, "onCreate: STEPS" + user_steps);

        connectToMQTT();
        createAlarm();

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

    }

    Location getCurrentLocation() {
        //https://stackoverflow.com/questions/57863500/how-can-i-get-my-current-location-in-android-using-gps
        try {
            @SuppressLint("MissingPermission")
            Location gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "onCreate: Last loc " + gps_loc);
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
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void connectToMQTT() {
        //https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service/
        Log.d(TAG, "connectToMQTT: " + clientId);

        client = new MqttAndroidClient(this.getApplicationContext(), "ssl://98ffc5f1d5b742ea97a55790d35f07da.s1.eu.hivemq.cloud:8883", clientId);

        try {
            //MQTT Version
            MqttConnectOptions options = new MqttConnectOptions();

            //MQTT LWT
            String topic = "users/steps";
            byte[] payload = "offline".getBytes();
            options.setWill(topic, payload, 1, true);

            //MQTT password username
            options.setUserName("miotuser");
            options.setPassword("miotpassword".toCharArray());
            client.setCallback(new MqttCallbackHandler(this.getApplicationContext()));

            IMqttToken token = client.connect(options);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "MQTT CONNECT onSuccess");
                    subscribeToMQTT();
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
    }

    public class MqttCallbackHandler implements MqttCallbackExtended {
        public MqttCallbackHandler(Context applicationContext) {
        }

        @Override
        public void connectComplete(boolean b, String s) {
            Log.w("mqtt", s);
        }

        @Override
        public void connectionLost(Throwable throwable) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            Log.d(TAG, "MQTT Message: " + mqttMessage.toString());
            int data = Integer.parseInt(mqttMessage.toString());
            handleBroadcast(ACTION_GLOBAL_GOAL, data);
            editor.putInt("global_goal", data);
            editor.putBoolean("global_goal_set", true);
            editor.apply(); // commit changes
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            Log.d(TAG, "deliveryComplete: ");
        }
    }

    public void subscribeToMQTT() {
        String topic = "users/global_goal";
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: SCRIBED TO GLOBAL");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishToMQTT(int value) {
        Log.d(TAG, "pushDataToMQTT: Sending data to MQTT:" + value);
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
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                updateSensorValue(user_steps);
                publishToMQTT(user_steps);
                handleBroadcast(ACTION_STEP_VALUE, user_steps);
                Log.d(TAG, "onSensorChanged: " + user_steps);
                break;
        }
    }

    void handleBroadcast(String ACTION, int value) {
        final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_DATA_VALUE, value);
        sendBroadcast(intent);
        Log.d(TAG, "handleBroadcast: Action =" + ACTION + " Value:" + value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}