package com.example.mdpproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.mdpproject.MainActivity;
import com.example.mdpproject.R;
import com.example.mdpproject.service.SensorService;
import com.example.mdpproject.utils.Gender;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    String name;
    Gender gender;
    int age;
    int height;
    int weight;
    int goal;
    int prev_goal;

    // Indicates whether it is the first time in the app or not
    boolean firstTime = true;

    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Settings");

        sharedPreferences = getSharedPreferences("shared-pref", Context.MODE_PRIVATE);
        String firstName = sharedPreferences.getString("first-name", null);
        if (firstName != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            firstTime = false;
            name = firstName;
            String strGender = sharedPreferences.getString("gender", null);
            gender = strGender != null ? Gender.valueOf(strGender) : null;
            age = sharedPreferences.getInt("age", 0);
            height = sharedPreferences.getInt("height", 0);
            weight = sharedPreferences.getInt("weight", 0);
            goal = sharedPreferences.getInt("goal", 0);
            prev_goal = goal;
            initializeForm();
        }
        addListenerOnButton();
    }

    private void initializeForm() {
        ((EditText) findViewById(R.id.name_input)).setText(name);
        if (gender != null) {
            if (Gender.MALE.equals(gender)) {
                ((RadioButton) findViewById(R.id.male_radio_button)).setChecked(true);
            } else if (Gender.FEMALE.equals(gender)) {
                ((RadioButton) findViewById(R.id.female_radio_button)).setChecked(true);
            }
        }
        ((EditText) findViewById(R.id.age_input)).setText(age > 0 ? "" + age : null);
        ((EditText) findViewById(R.id.height_input)).setText(height > 0 ? "" + height : null);
        ((EditText) findViewById(R.id.weight_input)).setText(weight > 0 ? "" + weight : null);
        ((EditText) findViewById(R.id.goal_input)).setText(goal > 0 ? "" + goal : null);
    }

    public void submitPersonalInfoHandler(View view) {
        if (validateForm()) {
            sharedPreferences.edit().putString("first-name", name).apply();
            sharedPreferences.edit().putString("gender", gender != null ? gender.toString() : null).apply();
            sharedPreferences.edit().putInt("age", age).apply();
            sharedPreferences.edit().putInt("height", height).apply();
            sharedPreferences.edit().putInt("weight", weight).apply();
            sharedPreferences.edit().putInt("goal", goal).apply();
            if(isMyServiceRunning(SensorService.class)&&prev_goal!=goal){
                stopService(new Intent(this, SensorService.class));
                startService(new Intent(this, SensorService.class));
            };
            Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(myIntent);
        }
    }

    public void addListenerOnButton() {
        genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male_radio_button:
                        gender = Gender.MALE;
                        break;
                    case R.id.female_radio_button:
                        gender = Gender.FEMALE;
                        break;
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        EditText firstNameEt = (EditText) findViewById(R.id.name_input);
        if (TextUtils.isEmpty(firstNameEt.getText())) {
            firstNameEt.setError("Enter Name !");
            valid = false;
        } else {
            name = firstNameEt.getText().toString();
        }

        EditText ageEt = (EditText) findViewById(R.id.age_input);
        if (TextUtils.isEmpty(ageEt.getText())) {
            age = 0;
        } else {
            age = Integer.parseInt(ageEt.getText().toString());
        }

        EditText heightEt = (EditText) findViewById(R.id.height_input);
        if (TextUtils.isEmpty(heightEt.getText())) {
            height = 0;
        } else {
            height = Integer.parseInt(heightEt.getText().toString());
        }

        EditText weightEt = (EditText) findViewById(R.id.weight_input);
        if (TextUtils.isEmpty(weightEt.getText())) {
            weight = 0;
        } else {
            weight = Integer.parseInt(weightEt.getText().toString());
        }

        EditText goalEt = (EditText) findViewById(R.id.goal_input);
        if (TextUtils.isEmpty(goalEt.getText())) {
            goalEt.setError("Enter Goal !");
            valid = false;
        } else {
            goal = Integer.parseInt(goalEt.getText().toString());
        }
        return valid;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}