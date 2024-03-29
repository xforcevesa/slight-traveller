package com.xvesa.stapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Objects;

public class NavigationHandler {

    public static class MyFragment extends Fragment {

        private final int id;

        public MyFragment(int id) {
            this.id = id;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(this.id, container, false);
        }
    }

    private final HashMap<Integer, MyFragment> mapper;
    private final AppCompatActivity activity;

    public NavigationHandler(AppCompatActivity a) {
        this.activity = a;
        this.mapper = new HashMap<>();
        this.mapper.put(R.id.navigation_home, new MyFragment(R.layout.home_layout));
        this.mapper.put(R.id.navigation_discover, new MyFragment(R.layout.discover_layout));
        this.mapper.put(R.id.navigation_dashboard, new MyFragment(R.layout.dashboard_layout));
    }

    public void handle() {
        BottomNavigationView bottom_nav_view = this.activity.findViewById(R.id.main_nav_menu);
        MyFragment default_fragment = Objects.requireNonNull(this.mapper.get(R.id.navigation_home));
        this.activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, default_fragment)
                .commit();
        bottom_nav_view.setOnItemSelectedListener(menuItem -> {
            Fragment f = this.mapper.getOrDefault(menuItem.getItemId(), default_fragment);
            assert f != null;
            this.activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .commit();
            return true;
        });
    }
}

