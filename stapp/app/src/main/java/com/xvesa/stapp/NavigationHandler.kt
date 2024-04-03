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
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import java.util.Objects

class NavigationHandler(private val activity: AppCompatActivity) {
    open class AppTabFragment(private val id: Int) : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(id, container, false)
    }

    class HomePageFragment(id: Int) : AppTabFragment(id) {
        private val mapper: HashMap<Int, AppTabFragment>
        private var lastViewID: Int

        init {
            lastViewID = R.id.home_tabs_menu_first
            mapper = HashMap()
            mapper[R.id.home_tabs_menu_first] = AppTabFragment(R.layout.experiences_layout)
            mapper[R.id.home_tabs_menu_second] = AppTabFragment(R.layout.adventures_layout)
            mapper[R.id.home_tabs_menu_third] = AppTabFragment(R.layout.activities_layout)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        fun afterCreateHandler() {
            mapper[R.id.home_tabs_menu_first]?.let {
                getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frame, it)
                    .commitNow()
            }
            lastViewID = R.id.home_tabs_menu_first
            val frame = (this.view as RelativeLayout?)!!
            mapper.forEach { (id: Int, _: AppTabFragment?) ->
                val homeTab = frame.findViewById<TextView>(id)
                homeTab.setOnClickListener {
                    val lastTab = frame.findViewById<TextView>(
                        lastViewID
                    )
                    lastTab.setTypeface(null, Typeface.NORMAL)
                    lastTab.setTextColor(
                        ContextCompat.getColor(
                            frame.context,
                            R.color.home_tabs_original
                        )
                    )
                    lastViewID = id
                    val thisTab = frame.findViewById<TextView>(id)
                    thisTab.setTypeface(null, Typeface.BOLD)
                    thisTab.setTextColor(
                        ContextCompat.getColor(
                            frame.context,
                            R.color.home_tabs_selected
                        )
                    )
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

    private val mapper: HashMap<Int, AppTabFragment>
    private val resMap: HashMap<Int, Pair<Int, Int>>
    private var currentID: Int

    init {
        mapper = HashMap()
        mapper[R.id.navigation_home] = HomePageFragment(R.layout.home_layout)
        mapper[R.id.navigation_discover] = AppTabFragment(R.layout.discover_layout)
        mapper[R.id.navigation_dashboard] = AppTabFragment(R.layout.dashboard_layout)
        resMap = HashMap()
        resMap[R.id.navigation_home] = Pair(
            R.drawable.home_original,
            R.drawable.home_clicked
        )
        resMap[R.id.navigation_discover] = Pair(
            R.drawable.search_original,
            R.drawable.search_square
        )
        resMap[R.id.navigation_dashboard] = Pair(
            R.drawable.dashboard_original,
            R.drawable.dashboard_clicked
        )
        currentID = R.id.home_tabs_menu
    }

    private fun handleNavigation() {
        val defaultTab = activity.findViewById<TextView>(R.id.navigation_home)
        val defaultDrawableClicked = ContextCompat.getDrawable(activity, R.drawable.home_clicked)
        defaultTab.setCompoundDrawablesWithIntrinsicBounds(
            null,
            defaultDrawableClicked,
            null,
            null
        )
        currentID = R.id.navigation_home
        val defaultFragment =
            Objects.requireNonNull(mapper[R.id.navigation_home]) as HomePageFragment
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
                    val pair = resMap[currentID]!!
                    val viewOriginal = pair.first
                    val drawableOriginal = ContextCompat.getDrawable(activity, viewOriginal)
                    last.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        drawableOriginal,
                        null,
                        null
                    )
                }
                currentID = key
                val here = activity.findViewById<TextView>(key)
                val pair = resMap[key]!!
                val viewClicked = pair.second
                val drawableClicked = ContextCompat.getDrawable(activity, viewClicked)
                here.setCompoundDrawablesWithIntrinsicBounds(null, drawableClicked, null, null)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, value as Fragment)
                    .commitNow()
                if (key == R.id.navigation_home) {
                    val localDefaultFragment =
                        mapper[R.id.navigation_home]!! as HomePageFragment
                    localDefaultFragment.requireView()
                    localDefaultFragment.afterCreateHandler()
                }
            }
        }
    }

    fun afterCreateHandle() {
        val defaultFragment =
            Objects.requireNonNull(mapper[R.id.navigation_home]) as HomePageFragment
        assert(defaultFragment.view != null)
        defaultFragment.afterCreateHandler()
    }

    fun onCreateHandle() {
        handleNavigation()
    }
}
