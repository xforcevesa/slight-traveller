package com.xvesa.stapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private NavigationHandler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        this.setContentView(R.layout.activity_main);
        h = new NavigationHandler(this);
        h.onCreateHandle();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        h.afterCreateHandle();
    }
}