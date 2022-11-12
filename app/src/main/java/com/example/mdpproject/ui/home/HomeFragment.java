package com.example.mdpproject.ui.home;


import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpproject.R;
import com.example.mdpproject.databinding.FragmentHomeBinding;
import com.example.mdpproject.service.SensorService;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;

    private TextView nameTextView ;
    private TextView percentTextView ;

    private TextView stepsTextView ;
    private TextView caloriesTextView ;
    private TextView distanceTextView ;
    private TextView userGoalTextView ;
    private TextView globalGoalTextView ;

    ProgressBar simpleProgressBar;
    CircularProgressBar circularProgressBar;

    int steps;
    int user_goal;
    int user_height;
    int global_goal;
    boolean isGlobalSet = false;

    private final BroadcastReceiver mSensorUpdateReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action){
                case SensorService.ACTION_STEP_VALUE:
                    steps = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE,0);
                    circularProgressBar.setProgress(steps);
                    percentTextView.setText((int)Math.floor(((float)steps/(float)user_goal)* 100f)+"%");
                    if(isGlobalSet)simpleProgressBar.setProgress(steps);
                    stepsTextView.setText(Integer.toString(steps));
                    distanceTextView.setText(Integer.toString((user_height*steps)/1000));
                    caloriesTextView.setText(Double.toString((0.04*steps)/1000.00));
                    break;

                case SensorService.ACTION_GLOBAL_GOAL:
                    isGlobalSet = true;
                    global_goal = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE,0);
                    globalGoalTextView.setText(Integer.toString(global_goal));
                    simpleProgressBar.setMax(global_goal);
                    Log.d(TAG, "handleBroadcast: Action ="+action+" Value:"+global_goal);
                    break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mSensorUpdateReciver, sensorIntentFilter());
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       // getActivity().startService(new Intent(getActivity(), SensorService.class));
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = this.getActivity().getSharedPreferences("shared-pref", Context.MODE_PRIVATE);


        simpleProgressBar=(ProgressBar) root.findViewById(R.id.home_global_progress); // initiate the progress bar
         circularProgressBar = root.findViewById(R.id.home_user_progress);

         nameTextView = binding.homeUsername;
        percentTextView = binding.homeUserPercent;
        stepsTextView = binding.homeUserStepsValue;
        caloriesTextView = binding.homeUserCaloriesValue;
        distanceTextView = binding.homeUserDistanceValue;
        userGoalTextView = binding.homeUserGoalValue;
        globalGoalTextView = binding.homeGlobalGoalValue;

//        user_steps = sharedpreferences.getInt("sensor_step", 0);
//        global_goal = sharedpreferences.getInt("global_goal", 0);
//        user_goal = sharedpreferences.getInt("goal",0);



        nameTextView.setText(sharedPreferences.getString("first-name", null));
        user_goal = sharedPreferences.getInt("goal", 0);
        user_height =   sharedPreferences.getInt("height", 0);
        global_goal = sharedPreferences.getInt("global_goal", 0);
        circularProgressBar.setProgressMax(user_goal);

        userGoalTextView.setText(Integer.toString(user_goal));
        globalGoalTextView.setText(Integer.toString(global_goal));
        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mSensorUpdateReciver);
    }



    private static IntentFilter sensorIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SensorService.ACTION_STEP_VALUE);
        intentFilter.addAction(SensorService.ACTION_USER_GOAL);
        intentFilter.addAction(SensorService.ACTION_GLOBAL_GOAL);
        return intentFilter;
    }


}