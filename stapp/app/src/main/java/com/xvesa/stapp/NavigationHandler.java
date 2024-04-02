package com.xvesa.stapp;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InsertGesture;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
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
    private final HashMap<Integer, Pair<Integer, Integer>> res_map;
    private final AppCompatActivity activity;

    private Integer currentID;

    public NavigationHandler(AppCompatActivity a) {
        this.activity = a;

        this.mapper = new HashMap<>();
        this.mapper.put(R.id.navigation_home, new MyFragment(R.layout.home_layout));
        this.mapper.put(R.id.navigation_discover, new MyFragment(R.layout.discover_layout));
        this.mapper.put(R.id.navigation_dashboard, new MyFragment(R.layout.dashboard_layout));

        this.res_map = new HashMap<>();
        this.res_map.put(R.id.navigation_home, new Pair<>(R.drawable.home_original, R.drawable.home_clicked));
        this.res_map.put(R.id.navigation_discover, new Pair<>(R.drawable.search_original, R.drawable.search_square));
        this.res_map.put(R.id.navigation_dashboard, new Pair<>(R.drawable.dashboard_original, R.drawable.dashboard_clicked));

        this.currentID = R.id.home_tabs_menu;
    }

    public void handle() {
        TextView default_tab = this.activity.findViewById(R.id.navigation_home);
        Drawable default_drawable_clicked = ContextCompat.getDrawable(this.activity, R.drawable.home_clicked);
        default_tab.setCompoundDrawablesWithIntrinsicBounds(null, default_drawable_clicked, null, null);
        this.currentID = R.id.navigation_home;

        MyFragment default_fragment = Objects.requireNonNull(this.mapper.get(R.id.navigation_home));
        this.activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, default_fragment)
                .commit();

        this.mapper.forEach((key, value) -> {
            TextView bottom_nav_view = this.activity.findViewById(key);
            bottom_nav_view.setOnClickListener(menuItem -> {
                if (this.mapper.containsKey(this.currentID)) {
                    TextView last = this.activity.findViewById(this.currentID);
                    Pair<Integer, Integer> pair = this.res_map.get(this.currentID);
                    assert pair != null;
                    Integer view_original = pair.first;
                    Drawable drawable_original = ContextCompat.getDrawable(this.activity, view_original);
                    last.setCompoundDrawablesWithIntrinsicBounds(null, drawable_original, null, null);
                }

                this.currentID = key;
                TextView here = this.activity.findViewById(key);
                Pair<Integer, Integer> pair = this.res_map.get(key);
                assert pair != null;
                Integer view_clicked = pair.second;
                Drawable drawable_clicked = ContextCompat.getDrawable(this.activity, view_clicked);
                here.setCompoundDrawablesWithIntrinsicBounds(null, drawable_clicked, null, null);

                this.activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, value)
                        .commit();
                }
            );
        });
    }
}

