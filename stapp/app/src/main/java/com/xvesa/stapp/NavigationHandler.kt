package com.xvesa.stapp

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class NavigationHandler(private val activity: AppCompatActivity) {

    /**
     * Base class for DIY Fragments
     * @constructor Passed the id so that it makes the specific layout to be loaded.
     */
    open class AppTabFragment(private val id: Int) : Fragment() {

        /**
         * @return Fragment that is loaded for specified layout.
         */
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(id, container, false)
    }

    /**
     * Inherited from AppTabFragment
     * @constructor Inherited from the super class and perform the same.
     */
    class HomePageFragment(id: Int) : AppTabFragment(id) {

        /**
         * mapper: Store the home tabs and their respective DIY Fragments.
         */
        private val mapper = mapOf(
            R.id.home_tabs_menu_first to AppTabFragment(R.layout.experiences_layout),
            R.id.home_tabs_menu_second to AppTabFragment(R.layout.adventures_layout),
            R.id.home_tabs_menu_third to AppTabFragment(R.layout.activities_layout)
        )

        /**
         * lastViewID: Stored last activated tab id,
         * hence convenient for disable the highlight icons.
         */
        private var lastViewID = R.id.home_tabs_menu_first


        /**
         * afterCreateHandler: It SHOULD be invoked AFTER the creation hook of activities.
         * 1. Register the homepage to be the first activated fragment.
         * 2. Set the OnClickListeners of every one of tabs through the context of current Fragment.
         */
        fun afterCreateHandler() {
            mapper[R.id.home_tabs_menu_first]?.let {
                getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frame, it)
                    .commitNow()
            }
            lastViewID = R.id.home_tabs_menu_first

            val frame = (this.view as RelativeLayout?)!!
            mapper.forEach { (id: Int, _) ->
                val homeTab = frame.findViewById<TextView>(id)
                homeTab.setOnClickListener {
                    // If they equal, there's no need to change the icons.
                    if (id == lastViewID) return@setOnClickListener

                    // Clear highlights of the last activated tabs.
                    val lastTab = frame.findViewById<TextView>(lastViewID)
                    lastTab.setTypeface(null, Typeface.NORMAL)
                    ContextCompat.getColor(
                        frame.context,
                        R.color.home_tabs_original
                    ).let {
                        lastTab.setTextColor(it)
                    }

                    // Make highlights of new clicked tabs.
                    lastViewID = id
                    val thisTab = frame.findViewById<TextView>(id)
                    thisTab.setTypeface(null, Typeface.BOLD)
                    ContextCompat.getColor(
                        frame.context,
                        R.color.home_tabs_selected
                    ).let {
                        thisTab.setTextColor(it)
                    }

                    // Actually switch the layout.
                    mapper[id]?.let {
                        getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_frame, it)
                            .commitNow()
                    }
                }
            }
        }
    }

    /**
     * mapper: Store the home tabs and their respective DIY Fragments.
     */
    private val mapper = mapOf(
        R.id.navigation_home to HomePageFragment(R.layout.home_layout),
        R.id.navigation_discover to AppTabFragment(R.layout.discover_layout),
        R.id.navigation_dashboard to AppTabFragment(R.layout.dashboard_layout)
    )

    /**
     * resMap: Store the home tabs and their respective dual icons
     * for activated and non-activated mode.
     */
    private val resMap = mapOf(
        R.id.navigation_home to (R.drawable.home_original to R.drawable.home_clicked),
        R.id.navigation_discover to (R.drawable.search_original to R.drawable.search_square),
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
        activity.findViewById<TextView>(R.id.navigation_home).setCompoundDrawablesWithIntrinsicBounds(
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
                val drawableOriginal = ContextCompat.getDrawable(activity, viewOriginal)
                last.setCompoundDrawablesWithIntrinsicBounds(
                    null, drawableOriginal,
                    null, null
                )
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
                if (key == R.id.navigation_home) {
                    val localDefaultFragment =
                        mapper[R.id.navigation_home] as HomePageFragment
                    localDefaultFragment.requireView()
                    localDefaultFragment.afterCreateHandler()
                }
            }
        }
    }

    /**
     * SHOULD be ONLY invoked after callbacks of the onCreate hook.
     */
    fun afterCreateHandle() = (mapper[R.id.navigation_home] as HomePageFragment).let {
        it.requireView()
        it.afterCreateHandler()
    }

    /**
     * Can be invoked in onCreate hook of main activity.
     */
    fun onCreateHandle() {
        handleNavigation()
    }
}
