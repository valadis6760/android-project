package com.example.mdpproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mdpproject.utils.Gender;

public class PersonalInfoActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    String name;
    Gender gender;
    int age;
    int height;
    int weight;
    int goal;

    // Indicates whether it is the first time in the app or not
    boolean firstTime = true;

    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Personal Info");

        sharedPreferences = getSharedPreferences("shared-pref", Context.MODE_PRIVATE);
        String firstName = sharedPreferences.getString("first-name", null);
        if (firstName != null) {
            firstTime = false;
            name = firstName;
            String strGender = sharedPreferences.getString("gender", null);
            gender = strGender != null ? Gender.valueOf(strGender) : null;
            age = sharedPreferences.getInt("age", 0);
            height = sharedPreferences.getInt("height", 0);
            weight = sharedPreferences.getInt("weight", 0);
            goal = sharedPreferences.getInt("goal", 0);
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
            Intent myIntent = new Intent(PersonalInfoActivity.this, MainActivity.class);
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
            firstNameEt.setError("We asked for your name, not for your bank account. Don't be a dick!");
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
            goalEt.setError("If you don't have a goal it's better to download Glovo!");
            valid = false;
        } else {
            goal = Integer.parseInt(goalEt.getText().toString());
        }
        return valid;
    }
}
