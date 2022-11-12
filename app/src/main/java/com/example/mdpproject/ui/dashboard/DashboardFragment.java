package com.example.mdpproject.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpproject.R;
import com.example.mdpproject.activity.MapsActivity;
import com.example.mdpproject.databinding.FragmentDashboardBinding;
import com.example.mdpproject.db.DBHelper;
import com.example.mdpproject.db.DailyInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private DBHelper dbHelper;
    BarDataSet bardataset;

    private List<BarEntry> dailyInfoList = new ArrayList<>();
    private List<BarEntry> monthlyInfoList = new ArrayList<>();

    private ArrayList<DailyInfo> weekArrayList  = new ArrayList<>();
    private ArrayList<DailyInfo> allArrayList  = new ArrayList<>();

    int week_teps_value = 0;


    int overall_steps_value = 0;



    private TextView week_user_steps ;
    private TextView week_calories_burned ;
    private TextView week_distance ;

    private TextView overall_user_steps ;
    private TextView overall_calories_burned ;
    private TextView overall_distance ;

    private Button LocationHistory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BarChart barChart = (BarChart) root.findViewById(R.id.dashboard_barchart);

        LocationHistory = root.findViewById(R.id.dashboard_location_history);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);

        bardataset = new BarDataSet(dailyInfoList, "Number of Steps past 7 Days");
        dbHelper = new DBHelper(this.getContext());
        weekArrayList.addAll(dbHelper.getWeekRecords());

        int spacing = 200;
        dailyInfoList.clear();
        monthlyInfoList.clear();

        if(weekArrayList.size()>0 && allArrayList.size()>0){
            allArrayList.addAll(dbHelper.getAllRecords());
            for (DailyInfo dailyInfo : allArrayList) {
                overall_steps_value += Integer.parseInt(dailyInfo.getSteps());
            }

            for (DailyInfo dailyInfo : weekArrayList) {
                week_teps_value += Integer.parseInt(dailyInfo.getSteps());
                dailyInfoList.add(new BarEntry(spacing, Float.parseFloat(dailyInfo.getSteps())));
                spacing += 1000;
            }
        }else{
            for(int j=0; j<7; j++){
                dailyInfoList.add(new BarEntry(spacing, 0));
                spacing += 1000;
            }
        }



        bardataset.setValues(dailyInfoList);

        barChart.animateY(5000);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisLeft().setAxisMinValue(0f);
        barChart.getAxisRight().setAxisMinValue(0f);

        BarData data = new BarData(bardataset);
        data.setBarWidth(70);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(data);

        bardataset.setColors(ColorTemplate.MATERIAL_COLORS);

        // setting text color.
        bardataset.setValueTextColor(Color.BLACK);

        // setting text size
        bardataset.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);

         week_user_steps = binding.dashboardWeekStepsValue ;
       week_calories_burned = binding.dashboardWeekCaloriesValue ;
        week_distance = binding.dashboardWeekDistanceValue;

        overall_user_steps = binding.dashboardOverallStepsValue;
        overall_calories_burned = binding.dashboardOverallCaloriesValue ;
     overall_distance = binding.dashboardOverallDistanceValue;



        week_user_steps.setText(Integer.toString(week_teps_value));
        week_calories_burned.setText(Float.toString(week_teps_value/100));
        week_distance.setText(Integer.toString(week_teps_value*10));

        overall_user_steps.setText(Integer.toString(overall_steps_value));
        overall_calories_burned.setText(Float.toString(overall_steps_value/100));
        overall_distance.setText(Float.toString(overall_steps_value*10));

        LocationHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(), MapsActivity.class);
                startActivity(myIntent);
            }
        });

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

//        dbHelper.insertContact("2022-11-01 10:40:50","123","22.45","45.23");
//        dbHelper.insertContact("2022-11-02 10:50:50","154","42.45","34.23");
//        dbHelper.insertContact("2022-11-03 10:40:50","1634","72.45","67.23");
//        dbHelper.insertContact("2022-11-04 10:30:50","1734","42.45","54.23");
//        dbHelper.insertContact("2022-11-05 10:20:50","2034","32.45","34.23");
//        dbHelper.insertContact("2022-11-06 10:10:50","2234","25.45","23.23");
//        dbHelper.insertContact("2022-11-07 10:40:50","6234","34.45","12.23");
//        dbHelper.insertContact("2022-11-08 10:50:50","1234","59.45","23.23");
//        dbHelper.insertContact("2022-11-09 10:40:50","4234","38.45","31.23");
//        dbHelper.insertContact("2022-11-10 10:30:50","7234","75.45","21.23");
//        dbHelper.insertContact("2022-11-11 10:20:50","8234","36.45","43.23");
//        dbHelper.insertContact("2022-11-12 10:10:50","7234","22.45","76.23");
//        dbHelper.insertContact("2022-11-22 10:00:50","4234","73.45","89.23");




//        contactArrayList.addAll(dbHelper.getAllRecords());
//        for(DailyInfo x : contactArrayList) {
//            Log.d("TABLEEEE", "onCreate: content "+x.getSteps());
//        }
//        Log.d("TABLEEEE", "onCreate: Total Size"+contactArrayList.size());

        return root;
    }

    private void setWeekData(){};

    private void setOverallData(){};

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}