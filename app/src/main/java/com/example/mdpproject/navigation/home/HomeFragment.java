package com.example.mdpproject.navigation.home;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpproject.R;
import com.example.mdpproject.databinding.FragmentHomeBinding;
import com.example.mdpproject.service.SensorService;
import com.example.mdpproject.utils.StepUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;

    private TextView nameTextView;
    private TextView percentTextView;

    private TextView stepsTextView;
    private TextView caloriesTextView;
    private TextView distanceTextView;
    private TextView userGoalTextView;
    private TextView globalGoalTextView;

    ProgressBar globalGoalProgressBar;
    CircularProgressBar userGoalProgressBar;
    HomeViewModel homeViewModel;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mSensorUpdateReceiver, sensorIntentFilter());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = this.getActivity().getSharedPreferences("shared-pref", Context.MODE_PRIVATE);

        globalGoalProgressBar = root.findViewById(R.id.home_global_progress);
        userGoalProgressBar = root.findViewById(R.id.home_user_progress);

        nameTextView = binding.homeUsername;
        percentTextView = binding.homeUserPercent;
        stepsTextView = binding.homeUserStepsValue;
        caloriesTextView = binding.homeUserCaloriesValue;
        distanceTextView = binding.homeUserDistanceValue;
        userGoalTextView = binding.homeUserGoalValue;
        globalGoalTextView = binding.homeGlobalGoalValue;

        getUserData();

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
        getActivity().unregisterReceiver(mSensorUpdateReceiver);
    }

    private final BroadcastReceiver mSensorUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case SensorService.ACTION_STEP_VALUE:
                    homeViewModel.steps = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE, 0);
                    userGoalProgressBar.setProgress(homeViewModel.steps);
                    percentTextView.setText(StepUtils.getPercentToString(homeViewModel.steps, homeViewModel.user_goal));
                    if (homeViewModel.global_goal_set) {
                        globalGoalProgressBar.setProgress(homeViewModel.steps);
                    }
                    stepsTextView.setText(Integer.toString(homeViewModel.steps));
                    distanceTextView.setText(StepUtils.getDistanceToString(homeViewModel.steps, homeViewModel.user_height));
                    caloriesTextView.setText(StepUtils.getCaloriesBurntToString(homeViewModel.steps));
                    break;

                case SensorService.ACTION_GLOBAL_GOAL:
                    homeViewModel.global_goal_set = true;
                    homeViewModel.global_goal = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE, 0);
                    globalGoalTextView.setText(Integer.toString(homeViewModel.global_goal));
                    globalGoalProgressBar.setMax(homeViewModel.global_goal);
                    Log.d(TAG, "handleBroadcast: Action =" + action + " Value:" + homeViewModel.global_goal);
                    break;

                case SensorService.ACTION_ALARM:
                    homeViewModel.steps = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE, 0);
                    userGoalProgressBar.setProgress(0);
                    percentTextView.setText(StepUtils.getPercentToString(homeViewModel.steps, homeViewModel.user_goal));
                    if (homeViewModel.global_goal_set) {
                        globalGoalProgressBar.setProgress(homeViewModel.steps);
                    }
                    stepsTextView.setText(Integer.toString(homeViewModel.steps));
                    distanceTextView.setText(StepUtils.getDistanceToString(homeViewModel.steps, homeViewModel.user_height));
                    caloriesTextView.setText(StepUtils.getCaloriesBurntToString(homeViewModel.steps));
            }
        }
    };


    private static IntentFilter sensorIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SensorService.ACTION_STEP_VALUE);
        intentFilter.addAction(SensorService.ACTION_USER_GOAL);
        intentFilter.addAction(SensorService.ACTION_GLOBAL_GOAL);
        intentFilter.addAction(SensorService.ACTION_ALARM);
        return intentFilter;
    }

    private void getUserData() {
        nameTextView.setText(sharedPreferences.getString("first-name", null));
        homeViewModel.steps = sharedPreferences.getInt("sensor_step", 0);
        homeViewModel.user_goal = sharedPreferences.getInt("goal", 0);
        homeViewModel.user_height = sharedPreferences.getInt("height", 0);
        homeViewModel.global_goal_set = sharedPreferences.getBoolean("global_goal_set", false);
        homeViewModel.global_goal = sharedPreferences.getInt("global_goal", 0);

        userGoalProgressBar.setProgressMax(homeViewModel.user_goal);
        userGoalProgressBar.setProgress(homeViewModel.steps);

        if (homeViewModel.global_goal_set) {
            globalGoalProgressBar.setMax(homeViewModel.global_goal);
            globalGoalProgressBar.setProgress(homeViewModel.steps);
        }

        percentTextView.setText(StepUtils.getPercentToString(homeViewModel.steps, homeViewModel.user_goal));
        stepsTextView.setText(Integer.toString(homeViewModel.steps));
        caloriesTextView.setText(StepUtils.getCaloriesBurntToString(homeViewModel.steps));
        distanceTextView.setText(StepUtils.getDistanceToString(homeViewModel.steps, homeViewModel.user_height));
        userGoalTextView.setText(Integer.toString(homeViewModel.user_goal));
        globalGoalTextView.setText(Integer.toString(homeViewModel.global_goal));
    }

}