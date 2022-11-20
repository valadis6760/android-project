package com.example.mdpproject.navigation.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpproject.R;
import com.example.mdpproject.databinding.FragmentNotificationsBinding;
import com.example.mdpproject.http.Downloader;

public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    final static String URL_ADDRESS = "https://www.runtastic.com/blog/en/feed/";

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.notification_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new Downloader(getContext(), URL_ADDRESS, recyclerView).execute();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}