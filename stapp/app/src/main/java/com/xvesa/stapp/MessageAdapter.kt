package com.xvesa.stapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MessageAdapter(private val messages: List<String>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MessageViewHolder(view)
    }


    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 1) R.layout.item_message_left else R.layout.item_message
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        holder.textViewMessage.also {
            it.text = messages[position]
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewMessage: TextView

        init {
            textViewMessage = itemView.findViewById(R.id.text_view_message)
        }
    }
}

