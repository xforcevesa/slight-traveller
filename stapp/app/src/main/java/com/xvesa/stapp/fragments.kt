package com.xvesa.stapp

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

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
 * Abstract class inherited from AppTabFragment
 */
abstract class AbstractPageFragment(id: Int) : AppTabFragment(id) {

    abstract fun afterCreateHandler()
}

/**
 * Inherited from AbstractPageFragment
 * @constructor Inherited from the super class and perform the same.
 */
class HomePageFragment(id: Int) : AbstractPageFragment(id) {

    /**
     * mapper: Store the home tabs and their respective DIY Fragments.
     */
    private val mapper = mapOf(
        R.id.home_tabs_menu_first to ExperiencesTabPageFragment(R.layout.experiences_layout),
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
    override fun afterCreateHandler() {
        // Commit the first tab
        mapper[R.id.home_tabs_menu_first]?.let {
            childFragmentManager
                .beginTransaction()
                .add(R.id.main_frame, it)
                .commitNow()
        }
        lastViewID = R.id.home_tabs_menu_first

        // Loading the first tab
        (mapper[R.id.home_tabs_menu_first] as ExperiencesTabPageFragment).also {
            it.requireView()
            it.afterCreateHandler()
        }

        val frame = (this.view as RelativeLayout?)!!

        frame.findViewById<HorizontalScrollView>(R.id.home_tabs_menu)
            .fullScroll(View.FOCUS_LEFT)

        mapper.forEach { (id: Int, _) ->
            val homeTab = frame.findViewById<TextView>(id)
            if (homeTab.hasOnClickListeners()) return@forEach
            homeTab.setOnClickListener {
                // If they equal, there's no need to change the icons.
                if (id == lastViewID) return@setOnClickListener

                // Clear highlights of the last activated tabs.
                val lastTab = frame.findViewById<TextView>(lastViewID)
                lastTab.setTypeface(null, Typeface.NORMAL)
                ContextCompat.getColor(
                    frame.context,
                    R.color.home_tabs_original
                ).also {
                    lastTab.setTextColor(it)
                }

                // Make highlights of new clicked tabs.
                lastViewID = id
                val thisTab = frame.findViewById<TextView>(id)
                thisTab.setTypeface(null, Typeface.BOLD)
                ContextCompat.getColor(
                    frame.context,
                    R.color.home_tabs_selected
                ).also {
                    thisTab.setTextColor(it)
                }

                // Actually switch the layout.
                mapper[id]?.also {
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_frame, it)
                        .commitNow()
                }

                // Auto Scroll
                frame.findViewById<HorizontalScrollView>(R.id.home_tabs_menu)?.also {
                    when (id) {
                        mapper.keys.first() -> it.fullScroll(View.FOCUS_LEFT)
                        mapper.keys.last() -> it.fullScroll(View.FOCUS_RIGHT)
                    }
                }

                // Auto load
                when (id) {
                    R.id.home_tabs_menu_first ->
                        (mapper[id] as ExperiencesTabPageFragment).also {
                            it.requireView()
                            it.afterCreateHandler()
                        }
                }
            }
        }
    }
}

/**
 * Inherited from AbstractPageFragment
 * @constructor Inherited from the super class and perform the same.
 */
class ChatPageFragment(id: Int) : AbstractPageFragment(id) {
    // Recycler View to enable rollback scroll operation.
    private lateinit var recyclerViewChat: RecyclerView
    // Input text box
    private lateinit var editTextMessage: EditText
    // Send button
    private lateinit var buttonSend: Button
    // Adapter to render the messages
    private lateinit var messageAdapter: MessageAdapter
    // Messages history
    private var messages: ArrayList<String> = ArrayList()

    override fun afterCreateHandler() {
        // Obtain the frame
        val frame = (this.view as RelativeLayout?)!!

        // Late initialization
        recyclerViewChat = frame.findViewById(R.id.recycler_view_chat)
        editTextMessage = frame.findViewById(R.id.edit_text_message)
        buttonSend = frame.findViewById(R.id.button_send)

        // Setup adapters and managers.
        messageAdapter = MessageAdapter(messages)
        recyclerViewChat.adapter = messageAdapter
        recyclerViewChat.layoutManager = LinearLayoutManager(this.context)

        if (buttonSend.hasOnClickListeners()) return

        // Callback
        buttonSend.setOnClickListener {
            // Process the text box
            editTextMessage.text.toString().trim{ it <= ' ' }.let {
                if (it.isNotEmpty()) {
                    viewMessage(it)
                    viewMessage("You sent: $it")
                }
            }
        }
    }

    /**
     * View the messages
     */
    private fun viewMessage(message: String) {
        messages.add(message)
        messageAdapter.notifyItemInserted(messages.size - 1)
        recyclerViewChat.scrollToPosition(messages.size - 1)
        editTextMessage.text.clear()
    }
}

/**
 * Fragment for home/experiences
 */
class ExperiencesTabPageFragment(id: Int) : AbstractPageFragment(id) {

    override fun afterCreateHandler() {

        // Obtain the frame
        val frame = (this.view as LinearLayout?)!!

        frame.findViewById<ViewPager2>(R.id.view_pager).let {v ->

            if (v.adapter is CarouselAdapter) return@let

            // Carousel images
            listOf(
                R.drawable.avatar to "My Avatar A",
                R.drawable.avatar to "My Avatar B"
            ).let {
                v.adapter = CarouselAdapter(it)
            }
        }
    }

}
