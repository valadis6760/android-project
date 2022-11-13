package com.example.mdpproject.ui.dashboard;

import android.content.Intent;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
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
    BarData data;
    BarChart barChart;

    private List<BarEntry> dailyInfoList = new ArrayList<>();
    private List<BarEntry> monthlyInfoList = new ArrayList<>();

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        week = root.findViewById(R.id.dashboard_button_week);
        month = root.findViewById(R.id.dashboard_button_month);
        year = root.findViewById(R.id.dashboard_button_year);
        LocationHistory = root.findViewById(R.id.dashboard_location_history);

        barChart = (BarChart) root.findViewById(R.id.dashboard_barchart);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setFitBars(true);
        barChart.setDrawBarShadow(true);
        barChart.setDrawGridBackground(false);
        barChart.animateY(2000);
        barChart.setAutoScaleMinMaxEnabled(true);

        bardataset = new BarDataSet(dailyInfoList, "Number of Steps");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        bardataset.setColors(ColorTemplate.MATERIAL_COLORS);
        bardataset.setValueTextColor(Color.BLACK);
        bardataset.setValueTextSize(16f);

        dbHelper = new DBHelper(this.getContext());
        // TODO: Comment the above line of code after the first execution
        insertDummyData();
        initializeGraphByDays(7);

        data = new BarData(bardataset);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);

        week_user_steps = binding.dashboardWeekStepsValue;
        week_calories_burned = binding.dashboardWeekCaloriesValue;
        week_distance = binding.dashboardWeekDistanceValue;

        overall_user_steps = binding.dashboardOverallStepsValue;
        overall_calories_burned = binding.dashboardOverallCaloriesValue;
        overall_distance = binding.dashboardOverallDistanceValue;

        week_user_steps.setText(Integer.toString(week_steps_value));
        week_calories_burned.setText(Float.toString(week_steps_value / 100));
        week_distance.setText(Integer.toString(week_steps_value * 10));

        overall_user_steps.setText(Integer.toString(overall_steps_value));
        overall_calories_burned.setText(Float.toString(overall_steps_value / 100));
        overall_distance.setText(Float.toString(overall_steps_value * 10));

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
        for (DailyInfo dailyInfo : localDailyInfoList) {
            dailyInfoList.add(new BarEntry(spacing, dailyInfo.getSteps()));
            spacing += 100;
        }
        bardataset.setValues(dailyInfoList);
        barChart.notifyDataSetChanged();
        barChart.refreshDrawableState();
        barChart.invalidate();
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
        bardataset.setValues(monthlyInfoList);
        barChart.refreshDrawableState();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
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
        Date today = new Date();
        dbHelper.addDailyInfo(new DailyInfo(january1st, 1000));
        dbHelper.addDailyInfo(new DailyInfo(january15th, 2000));
        dbHelper.addDailyInfo(new DailyInfo(february1st, 3000));
        dbHelper.addDailyInfo(new DailyInfo(february15th, 4000));
        dbHelper.addDailyInfo(new DailyInfo(july1st, 5000));
        dbHelper.addDailyInfo(new DailyInfo(july15th, 6000));
        dbHelper.addDailyInfo(new DailyInfo(yesterday, 7000));
        dbHelper.addDailyInfo(new DailyInfo(today, 8000));
    }

}