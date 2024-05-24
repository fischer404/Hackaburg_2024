package com.example.anti_sms_scam

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sender: TextView = itemView.findViewById(R.id.sender)
        val messagePreview: TextView = itemView.findViewById(R.id.message_preview)
        val infoButton: ImageButton = itemView.findViewById(R.id.info_button)
        val messageContainer: CardView = itemView.findViewById(R.id.message_container)
        val expandableLayout: LinearLayout = itemView.findViewById(R.id.expandable_layout)
        val scamExplanation: TextView = itemView.findViewById(R.id.scam_explanation)
        val precautions: TextView = itemView.findViewById(R.id.precautions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.sender.text = message.sender
        holder.messagePreview.text = message.content.take(50)

        if (message.flag != null) {
            holder.messageContainer.setCardBackgroundColor(Color.RED)
            holder.infoButton.visibility = View.VISIBLE // Nur für geflaggte Nachrichten sichtbar
            holder.scamExplanation.text = message.scamExplanation
            holder.precautions.text = message.precautions
        } else {
            holder.messageContainer.setCardBackgroundColor(Color.WHITE)  // Or any other default color
            holder.infoButton.visibility = View.GONE
        }

        holder.infoButton.setOnClickListener {
            if (holder.expandableLayout.visibility == View.VISIBLE) {
                holder.expandableLayout.visibility = View.GONE
            } else {
                holder.expandableLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = messages.size
}
