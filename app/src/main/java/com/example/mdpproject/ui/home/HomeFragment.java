package com.example.mdpproject.ui.home;


import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private Button buttonStartStop;

    private TextView textView ;
    private TextView textUser ;
    private TextView TextGlobal ;

    ProgressBar simpleProgressBar;
    CircularProgressBar circularProgressBar;

    int steps;
    int user_goal;
    int global_goal;


    private final BroadcastReceiver mSensorUpdateReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action){
                case SensorService.ACTION_STEP_VALUE:
                    steps = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE,0);
                    Log.d(TAG, "handleBroadcast: Action ="+action+" Value:"+steps);

                    circularProgressBar.setProgress(steps);

                    textView.setText((int)Math.floor(((float)steps/(float)user_goal)* 100f)+"%");
                    TextGlobal.setText(steps+" / "+global_goal);

                    simpleProgressBar.setProgress(steps);
                    textUser.setText(steps+" / "+user_goal);

                    break;
                case SensorService.ACTION_USER_GOAL:
                    user_goal = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE,0);
                    circularProgressBar.setProgressMax(user_goal);
                    Log.d(TAG, "handleBroadcast: Action ="+action+" Value:"+user_goal);
                    break;

                case SensorService.ACTION_GLOBAL_GOAL:
                    global_goal = intent.getIntExtra(SensorService.EXTRA_DATA_VALUE,0);
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
      //  getActivity().startService(new Intent(getActivity(), SensorService.class));
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        simpleProgressBar=(ProgressBar) root.findViewById(R.id.home_global_progress); // initiate the progress bar
         circularProgressBar = root.findViewById(R.id.home_user_progress);

        textView = binding.homeUserPercent;
        textUser = binding.homeUserSteps ;
        TextGlobal = binding.homeGlobalSteps;

        getActivity().startService(new Intent(getActivity(), SensorService.class));

        //0. 04 calories per step
        //user height * steps = distance




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