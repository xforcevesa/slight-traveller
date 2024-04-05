package com.xvesa.stapp


import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class NavigationHandler(private val activity: AppCompatActivity) {

    /**
     * mapper: Store the home tabs and their respective DIY Fragments.
     */
    private val mapper = mapOf(
        R.id.navigation_home to HomePageFragment(R.layout.home_layout),
        R.id.navigation_chat to ChatPageFragment(R.layout.chat_layout),
        R.id.navigation_dashboard to AppTabFragment(R.layout.dashboard_layout)
    )

    /**
     * resMap: Store the home tabs and their respective dual icons
     * for activated and non-activated mode.
     */
    private val resMap = mapOf(
        R.id.navigation_home to (R.drawable.home_original to R.drawable.home_clicked),
        R.id.navigation_chat to (R.drawable.chat_original to R.drawable.chat_clicked),
        R.id.navigation_dashboard to (R.drawable.dashboard_original to R.drawable.dashboard_clicked)
    )

    /**
     * currentID: Store the last activated tab.
     */
    private var currentID = R.id.home_tabs_menu


    /**
     * handleNavigation: It follows the onCreate hook of main activity.
     * 1. Activate the home tab.
     * 2. Setup listeners.
     */
    private fun handleNavigation() {
        // Activate the home tab.
        activity.findViewById<TextView>(R.id.navigation_home)
            .setCompoundDrawablesWithIntrinsicBounds(
                null,
                ContextCompat.getDrawable(activity, R.drawable.home_clicked),
                null, null
            )
        currentID = R.id.navigation_home
        val defaultFragment =
            mapper[R.id.navigation_home] as HomePageFragment
        val supportFragmentManager = activity.supportFragmentManager
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, defaultFragment)
            .commitNow()

        mapper.forEach { (key: Int, value: AppTabFragment?) ->
            val bottomNavView = activity.findViewById<TextView>(key)
            bottomNavView.setOnClickListener {
                // If they equal, there's no need to change the icons.
                if (currentID == key) return@setOnClickListener

                // Clear the last activated flags.
                val last = activity.findViewById<TextView>(currentID)
                val viewOriginal = resMap[currentID]!!.first
                ContextCompat.getDrawable(activity, viewOriginal).let {
                    last.setCompoundDrawablesWithIntrinsicBounds(
                        null, it, null, null)
                }

                currentID = key

                // Activate current fragment.
                val here = activity.findViewById<TextView>(key)
                val viewClicked = resMap[key]!!.second
                val drawableClicked = ContextCompat.getDrawable(activity, viewClicked)
                here.setCompoundDrawablesWithIntrinsicBounds(null, drawableClicked, null, null)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, value as Fragment)
                    .commitNow()
                // Consequent callbacks
                when(key) {
                    R.id.navigation_home ->
                        (mapper[R.id.navigation_home] as HomePageFragment).let {
                            it.requireView()
                            it.afterCreateHandler()
                        }
                    R.id.navigation_chat ->
                        (mapper[R.id.navigation_chat] as ChatPageFragment).let {
                            it.requireView()
                            it.afterCreateHandler()
                        }
                }
            }
        }
    }

    /**
     * SHOULD be ONLY invoked after callbacks of the onCreate hook.
     */
    fun afterCreateHandle() {
        (mapper[R.id.navigation_home] as HomePageFragment).let {
            it.requireView()
            it.afterCreateHandler()
        }
    }

    /**
     * Can be invoked in onCreate hook of main activity.
     */
    fun onCreateHandle() {
        handleNavigation()
    }
}