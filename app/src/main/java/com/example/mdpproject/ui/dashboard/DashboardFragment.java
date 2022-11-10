package com.example.mdpproject.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdpproject.R;
import com.example.mdpproject.databinding.FragmentDashboardBinding;
import com.example.mdpproject.db.DBHelper;
import com.example.mdpproject.db.DailyInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private DBHelper dbHelper;

    private List<BarEntry> dailyInfoList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BarChart barChart = (BarChart) root.findViewById(R.id.dashboard_barchart);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);

        initializeGraph(7);

        BarDataSet bardataset = new BarDataSet(dailyInfoList, "Number of Steps");

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

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public List<DailyInfo> onRangeSelect(int range) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -range);
        Date today = new Date();
        Date oneWeekAgo = cal.getTime();
        return dbHelper.getDailyInfoByDateRange(oneWeekAgo, today);
    }

    private void initializeGraph(int days) {
        List<DailyInfo> localDailyInfoList = onRangeSelect(days);
        int spacing = 100;
        for (DailyInfo dailyInfo : localDailyInfoList) {
            dailyInfoList.add(new BarEntry(spacing, dailyInfo.getSteps()));
            spacing += 100;
        }
    }
}