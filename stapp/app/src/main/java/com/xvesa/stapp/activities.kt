package com.xvesa.stapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    /**
     * handler: action handler class initialized in the onCreate hook.
     */
    private lateinit var handler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        // Setup main activity layout
        this.setContentView(R.layout.activity_main)

        // Setup normal handlers.
        handler = NavigationHandler(this).also {
            it.onCreateHandle()
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        // Setup after create handlers.
        handler.afterCreateHandle()
    }
}