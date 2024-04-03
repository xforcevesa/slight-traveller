package com.xvesa.stapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    /**
     * handler: action handler class initialized in the onCreate hook.
     */
    private var handler: NavigationHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        // Setup main activity layout
        this.setContentView(R.layout.activity_main)

        // Setup normal handlers.
        handler = NavigationHandler(this).let {
            it.onCreateHandle()
            it
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        // Setup after create handlers.
        handler!!.afterCreateHandle()
    }
}