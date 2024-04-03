package com.xvesa.stapp;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Objects;

public class NavigationHandler {

    public static class AppTabFragment extends Fragment {

        private final int id;

        public AppTabFragment(int id) {
            this.id = id;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(this.id, container, false);
        }
    }

    public static class HomePageFragment extends AppTabFragment {

        private final HashMap<Integer, AppTabFragment> mapper;

        private Integer lastViewID;

        public HomePageFragment(int id) {
            super(id);
            this.lastViewID = R.id.home_tabs_menu_first;

            this.mapper = new HashMap<>();
            this.mapper.put(R.id.home_tabs_menu_first, new AppTabFragment(R.layout.experiences_layout));
            this.mapper.put(R.id.home_tabs_menu_second, new AppTabFragment(R.layout.adventures_layout));
            this.mapper.put(R.id.home_tabs_menu_third, new AppTabFragment(R.layout.activities_layout));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        public void afterCreateHandler() {
            this.getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frame, Objects.requireNonNull(this.mapper.get(R.id.home_tabs_menu_first)))
                    .commitNow();

            RelativeLayout frame = (RelativeLayout) this.getView();
            assert frame != null;
            this.mapper.forEach((id, fragment) -> {
                TextView home_tab = frame.findViewById(id);
                home_tab.setOnClickListener(a -> {
                    TextView last_tab = frame.findViewById(this.lastViewID);
                    last_tab.setTypeface(null, Typeface.NORMAL);
                    last_tab.setTextColor(ContextCompat.getColor(frame.getContext(), R.color.home_tabs_original));

                    this.lastViewID = id;
                    TextView this_tab = frame.findViewById(id);
                    this_tab.setTypeface(null, Typeface.BOLD);
                    this_tab.setTextColor(ContextCompat.getColor(frame.getContext(), R.color.home_tabs_selected));

                    this.getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_frame, Objects.requireNonNull(this.mapper.get(id)))
                            .commitNow();
                });
            });
        }

    }

    private final HashMap<Integer, AppTabFragment> mapper;
    private final HashMap<Integer, Pair<Integer, Integer>> res_map;
    private final AppCompatActivity activity;

    private Integer currentID;

    public NavigationHandler(AppCompatActivity a) {
        this.activity = a;

        this.mapper = new HashMap<>();
        this.mapper.put(R.id.navigation_home, new HomePageFragment(R.layout.home_layout));
        this.mapper.put(R.id.navigation_discover, new AppTabFragment(R.layout.discover_layout));
        this.mapper.put(R.id.navigation_dashboard, new AppTabFragment(R.layout.dashboard_layout));

        this.res_map = new HashMap<>();
        this.res_map.put(R.id.navigation_home, new Pair<>(R.drawable.home_original, R.drawable.home_clicked));
        this.res_map.put(R.id.navigation_discover, new Pair<>(R.drawable.search_original, R.drawable.search_square));
        this.res_map.put(R.id.navigation_dashboard, new Pair<>(R.drawable.dashboard_original, R.drawable.dashboard_clicked));

        this.currentID = R.id.home_tabs_menu;
    }

    private void handleNavigation() {
        TextView default_tab = this.activity.findViewById(R.id.navigation_home);
        Drawable default_drawable_clicked = ContextCompat.getDrawable(this.activity, R.drawable.home_clicked);
        default_tab.setCompoundDrawablesWithIntrinsicBounds(null, default_drawable_clicked, null, null);
        this.currentID = R.id.navigation_home;

        HomePageFragment default_fragment = (HomePageFragment) Objects.requireNonNull(this.mapper.get(R.id.navigation_home));
        FragmentManager supportFragmentManager = this.activity.getSupportFragmentManager();
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, default_fragment)
                .commitNow();

        this.mapper.forEach((key, value) -> {
            TextView bottom_nav_view = this.activity.findViewById(key);
            bottom_nav_view.setOnClickListener(a -> {
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

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, value)
                        .commitNow();
            });
        });
    }

    public void afterCreateHandle() {
        HomePageFragment default_fragment = (HomePageFragment) Objects.requireNonNull(this.mapper.get(R.id.navigation_home));
        assert default_fragment.getView() != null;
        default_fragment.afterCreateHandler();
    }

    public void onCreateHandle() {
        this.handleNavigation();
    }
}

