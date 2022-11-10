package com.example.mdpproject.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpproject.R;
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

    private List<BarEntry> dailyInfoList = new ArrayList<>();
    private List<BarEntry> monthlyInfoList = new ArrayList<>();

    private Button week;
    private Button month;
    private Button year;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BarChart barChart = (BarChart) root.findViewById(R.id.dashboard_barchart);
        week = root.findViewById(R.id.dashboard_button_week);
        month = root.findViewById(R.id.dashboard_button_month);
        year = root.findViewById(R.id.dashboard_button_year);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);

        bardataset = new BarDataSet(dailyInfoList, "Number of Steps");
        dbHelper = new DBHelper(this.getContext());
        initializeGraphByDays(7);


        barChart.animateY(5000);
        BarData data = new BarData(bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(data);

        bardataset.setColors(ColorTemplate.MATERIAL_COLORS);

        // setting text color.
        bardataset.setValueTextColor(Color.BLACK);

        // setting text size
        bardataset.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);

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

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
        Date oneWeekAgo = cal.getTime();
        try {
            return dbHelper.getDailyInfoByDateRange(oneWeekAgo, today);
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
        for (DailyInfo dailyInfo : info) {
            MonthlyInfo monthlyInfo = monthlyInfoMap.get(dailyInfo.getDate().getMonth() + 1);
            if (monthlyInfo != null) {
                monthlyInfo.setSteps(monthlyInfo.getSteps() + dailyInfo.getSteps());
            } else {
                MonthlyInfo tempMonthlyInfo = new MonthlyInfo();
                tempMonthlyInfo.setMonth(dailyInfo.getDate().getMonth() + 1);
                tempMonthlyInfo.setSteps(dailyInfo.getSteps());
            }
        }
        TreeMap<Integer, MonthlyInfo> sorted = new TreeMap<>();
        sorted.putAll(monthlyInfoMap);
        int spacing = 100;
        dailyInfoList.clear();
        monthlyInfoList.clear();
        for (Map.Entry<Integer, MonthlyInfo> entry : monthlyInfoMap.entrySet()) {
            monthlyInfoList.add(new BarEntry(spacing, entry.getValue().getSteps()));
            spacing += 100;
        }
        bardataset.setValues(monthlyInfoList);
    }

}