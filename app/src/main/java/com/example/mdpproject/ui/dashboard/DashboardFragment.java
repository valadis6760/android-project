package com.example.mdpproject.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.mdpproject.db.MonthlyInfo;
import com.example.mdpproject.utils.StepUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private DBHelper dbHelper;
    BarDataSet bardataset;
    BarData data;
    BarChart barChart;

    private List<BarEntry> dailyInfoList = new ArrayList<>();
    private List<BarEntry> monthlyInfoList = new ArrayList<>();
    private List<DailyInfo> weeklyDailyInfo = new ArrayList<>();

    private Button week;
    private Button month;
    private Button year;

    int week_steps_value = 0;
    int overall_steps_value = 0;

    private TextView week_user_steps;
    private TextView week_calories_burned;
    private TextView week_distance;

    private TextView overall_user_steps;
    private TextView overall_calories_burned;
    private TextView overall_distance;

    private Button LocationHistory;

    SharedPreferences sharedPreferences;
    int user_height;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = this.getActivity().getSharedPreferences("shared-pref", Context.MODE_PRIVATE);
        user_height =   sharedPreferences.getInt("height", 0);

        week = root.findViewById(R.id.dashboard_button_week);
        month = root.findViewById(R.id.dashboard_button_month);
        year = root.findViewById(R.id.dashboard_button_year);
        LocationHistory = root.findViewById(R.id.dashboard_location_history);

        barChart = root.findViewById(R.id.dashboard_barchart);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setFitBars(true);
        barChart.setDrawBarShadow(true);
        barChart.setDrawGridBackground(false);
        barChart.animateY(1000);
        barChart.setAutoScaleMinMaxEnabled(true);
        barChart.getAxisRight().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setEnabled(false);

        bardataset = new BarDataSet(dailyInfoList, "Number of Steps");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        bardataset.setColors(ColorTemplate.MATERIAL_COLORS);
        bardataset.setValueTextColor(Color.BLACK);
        bardataset.setValueTextSize(16f);
        bardataset.setAxisDependency(YAxis.AxisDependency.RIGHT);

        dbHelper = new DBHelper(this.getContext());
        setWeeklyDailyInfoList();
        if (weeklyDailyInfo.isEmpty()) {
            insertDummyData();
            setWeeklyDailyInfoList();
        }
        weekSelected();

        data = new BarData(bardataset);
        data.setBarWidth(30f);
        barChart.getXAxis().setAxisMaximum(data.getXMax() + 35f);
        barChart.getXAxis().setAxisMinimum(data.getXMin() - 35f);
        barChart.setData(data);

        week_user_steps = binding.dashboardWeekStepsValue;
        week_calories_burned = binding.dashboardWeekCaloriesValue;
        week_distance = binding.dashboardWeekDistanceValue;

        overall_user_steps = binding.dashboardOverallStepsValue;
        overall_calories_burned = binding.dashboardOverallCaloriesValue;
        overall_distance = binding.dashboardOverallDistanceValue;

        calculateWeekStats();
        calculateOverallStats();

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weekSelected();
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthSelected();
            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearSelected();
            }
        });
        LocationHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(), MapsActivity.class);
                startActivity(myIntent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<DailyInfo> onRangeSelect(int range) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -range);
        Date today = new Date();
        Date fromDate = cal.getTime();
        try {
            return dbHelper.getDailyInfoByDateRange(fromDate, today);
        } catch (ParseException e) {
            return new ArrayList<>();
        }
    }

    public void initializeGraphByDays(int days) {
        List<DailyInfo> localDailyInfoList = onRangeSelect(days);

        int spacing = 100;
        dailyInfoList.clear();
        monthlyInfoList.clear();

        // If the list is empty (it's the first time using the app)
        if (localDailyInfoList.size() == 0) {
            for (int i = 1; i <= days; i++) {
                dailyInfoList.add(new BarEntry(spacing, 0));
                spacing += 100;
            }
        } else {
            // If there are records on the database
            Map<Integer, DailyInfo> dailyInfoMap = new HashMap<>();
            Calendar cal = Calendar.getInstance();
            for (int i = 1; i <= days; i++) {
                cal.add(Calendar.DATE, -i);
                Date tempDate = cal.getTime();
                dailyInfoMap.put(i, new DailyInfo());
                for (DailyInfo info : localDailyInfoList) {
                    if (tempDate.getDate() == info.getDate().getDate() && tempDate.getMonth() == info.getDate().getMonth()) {
                        dailyInfoMap.put(i, info);
                        break;
                    }
                }
                cal.setTime(new Date());
            }
            Map<Integer, DailyInfo> sorted = new TreeMap<>(Collections.reverseOrder());
            sorted.putAll(dailyInfoMap);
            for (Map.Entry<Integer, DailyInfo> entry : sorted.entrySet()) {
                dailyInfoList.add(new BarEntry(spacing, entry.getValue().getSteps()));
                spacing += 100;
            }
        }
        refreshChart(dailyInfoList);
    }

    public void weekSelected() {
        initializeGraphByDays(7);
    }

    public void monthSelected() {
        initializeGraphByDays(30);
    }

    public void yearSelected() {
        List<DailyInfo> info = onRangeSelect(365);
        Map<Integer, MonthlyInfo> monthlyInfoMap = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyInfoMap.put(i, new MonthlyInfo(i));
        }
        for (DailyInfo dailyInfo : info) {
            if (dailyInfo.getDate().getYear() == new Date().getYear()) {
                int month = dailyInfo.getDate().getMonth() + 1;
                MonthlyInfo monthlyInfo = monthlyInfoMap.get(month);
                monthlyInfo.setSteps(monthlyInfo.getSteps() + dailyInfo.getSteps());
            }
        }
        TreeMap<Integer, MonthlyInfo> sorted = new TreeMap<>();
        sorted.putAll(monthlyInfoMap);
        int spacing = 100;
        dailyInfoList.clear();
        monthlyInfoList.clear();
        for (Map.Entry<Integer, MonthlyInfo> entry : sorted.entrySet()) {
            monthlyInfoList.add(new BarEntry(spacing, entry.getValue().getSteps()));
            spacing += 100;
        }
        refreshChart(monthlyInfoList);
    }

    private void refreshChart(List<BarEntry> monthlyInfoList) {
        bardataset.setValues(monthlyInfoList);
        if (barChart.getData() != null) {
            barChart.getData().notifyDataChanged();
            barChart.getXAxis().setAxisMaximum(barChart.getData().getXMax() + 35f);
            barChart.getXAxis().setAxisMinimum(barChart.getData().getXMin() - 35f);
        }
        barChart.notifyDataSetChanged();
        barChart.refreshDrawableState();
        barChart.invalidate();
    }

    private void calculateWeekStats() {
        int steps = 0;
        for (DailyInfo info : weeklyDailyInfo) {
            steps += info.getSteps();
        }
        week_user_steps.setText(Integer.toString(steps));
        week_calories_burned.setText(StepUtils.getCaloriesBurntToString(steps));
        week_distance.setText(StepUtils.getDistanceToString(steps,user_height));
    }

    private void calculateOverallStats() {
        int steps = 0;
        List<DailyInfo> dailyInfoList = new ArrayList<>();
        try {
            dailyInfoList = dbHelper.getAllRecords();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (DailyInfo dailyInfo : dailyInfoList) {
            steps += dailyInfo.getSteps();
        }
        overall_user_steps.setText(Integer.toString(steps));
        overall_calories_burned.setText(StepUtils.getCaloriesBurntToString(steps));
        overall_distance.setText(StepUtils.getDistanceToString(steps,user_height));
    }

    private void setWeeklyDailyInfoList() {
        weeklyDailyInfo = onRangeSelect(7);
    }

    private void insertDummyData() {
        Date january1st = new Date(122, 0, 1);
        Date january15th = new Date(122, 0, 15);
        Date february1st = new Date(122, 1, 1);
        Date february15th = new Date(122, 1, 15);
        Date july1st = new Date(122, 6, 1);
        Date july15th = new Date(122, 6, 15);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        dbHelper.addDailyInfo(new DailyInfo(january1st, 1000));
        dbHelper.addDailyInfo(new DailyInfo(january15th, 2000));
        dbHelper.addDailyInfo(new DailyInfo(february1st, 3000));
        dbHelper.addDailyInfo(new DailyInfo(february15th, 4000));
        dbHelper.addDailyInfo(new DailyInfo(july1st, 5000, "40.388863", "-3.627624", true));
        dbHelper.addDailyInfo(new DailyInfo(july15th, 6000, "40.399191", "-3.621874", true));
        dbHelper.addDailyInfo(new DailyInfo(yesterday, 7000));
    }

}