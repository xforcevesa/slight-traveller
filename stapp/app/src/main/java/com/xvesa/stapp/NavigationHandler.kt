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
    open class AppTabFragment(private val id: Int) : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(id, container, false)
    }

    class HomePageFragment(id: Int) : AppTabFragment(id) {
        private val mapper = mapOf(
            R.id.home_tabs_menu_first to AppTabFragment(R.layout.experiences_layout),
            R.id.home_tabs_menu_second to AppTabFragment(R.layout.adventures_layout),
            R.id.home_tabs_menu_third to AppTabFragment(R.layout.activities_layout)
        )
        private var lastViewID = R.id.home_tabs_menu_first

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
                    val lastTab = frame.findViewById<TextView>(lastViewID)
                    lastTab.setTypeface(null, Typeface.NORMAL)
                    ContextCompat.getColor(
                        frame.context,
                        R.color.home_tabs_original
                    ).let {
                        lastTab.setTextColor(it)
                    }

                    lastViewID = id
                    val thisTab = frame.findViewById<TextView>(id)
                    thisTab.setTypeface(null, Typeface.BOLD)
                    ContextCompat.getColor(
                        frame.context,
                        R.color.home_tabs_selected
                    ).let {
                        thisTab.setTextColor(it)
                    }
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

    private val mapper = mapOf(
        R.id.navigation_home to HomePageFragment(R.layout.home_layout),
        R.id.navigation_discover to AppTabFragment(R.layout.discover_layout),
        R.id.navigation_dashboard to AppTabFragment(R.layout.dashboard_layout)
    )
    private val resMap = mapOf(
        R.id.navigation_home to (R.drawable.home_original to R.drawable.home_clicked),
        R.id.navigation_discover to (R.drawable.search_original to R.drawable.search_square),
        R.id.navigation_dashboard to (R.drawable.dashboard_original to R.drawable.dashboard_clicked)
    )
    private var currentID = R.id.home_tabs_menu

    private fun handleNavigation() {
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
                if (mapper.containsKey(currentID)) {
                    val last = activity.findViewById<TextView>(currentID)
                    val viewOriginal = resMap[currentID]!!.first
                    val drawableOriginal = ContextCompat.getDrawable(activity, viewOriginal)
                    last.setCompoundDrawablesWithIntrinsicBounds(
                        null, drawableOriginal,
                        null, null
                    )
                }
                currentID = key
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

    fun afterCreateHandle() = (mapper[R.id.navigation_home] as HomePageFragment).let {
        it.requireView()
        it.afterCreateHandler()
    }

    fun onCreateHandle() {
        handleNavigation()
    }
}
