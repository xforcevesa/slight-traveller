package com.xvesa.stapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var handler: NavigationHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        this.setContentView(R.layout.activity_main)
        handler = NavigationHandler(this)
        handler!!.onCreateHandle()
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        handler!!.afterCreateHandle()
    }
}