package com.example.anti_sms_scam

import android.graphics.Color
import android.graphics.Typeface
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
        val descriptionHeading: TextView = itemView.findViewById(R.id.description_heading)
        val scamExplanation: TextView = itemView.findViewById(R.id.scam_explanation)
        val precautionsHeading: TextView = itemView.findViewById(R.id.precautions_heading)
        val precautions: TextView = itemView.findViewById(R.id.precautions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.sender.text = message.sender

        // Set text preview based on whether the message is flagged and expanded
        if (message.flag != null && holder.expandableLayout.visibility == View.VISIBLE) {
            holder.messagePreview.text = message.content // Show full content
        } else {
            holder.messagePreview.text = message.content.take(50) // Show preview
        }

        if (message.flag != null) {
            holder.messageContainer.setCardBackgroundColor(Color.RED)
            holder.infoButton.visibility = View.VISIBLE // Nur für geflaggte Nachrichten sichtbar
            holder.messagePreview.setTypeface(null, Typeface.ITALIC)
            holder.scamExplanation.text = message.scamExplanation
            holder.precautions.text = message.precautions
            holder.descriptionHeading.visibility = View.VISIBLE
            holder.scamExplanation.visibility = View.VISIBLE
            holder.precautionsHeading.visibility = View.VISIBLE
            holder.precautions.visibility = View.VISIBLE
        } else {
            holder.messageContainer.setCardBackgroundColor(Color.WHITE)  // Or any other default color
            holder.infoButton.visibility = View.GONE
            holder.messagePreview.setTypeface(null, Typeface.NORMAL)
            holder.descriptionHeading.visibility = View.GONE
            holder.scamExplanation.visibility = View.GONE
            holder.precautionsHeading.visibility = View.GONE
            holder.precautions.visibility = View.GONE
        }

        holder.infoButton.setOnClickListener {
            if (holder.expandableLayout.visibility == View.VISIBLE) {
                holder.expandableLayout.visibility = View.GONE
                holder.messagePreview.text = message.content.take(50) // Restore preview
            } else {
                holder.expandableLayout.visibility = View.VISIBLE
                holder.messagePreview.text = message.content // Show full content
            }
        }
    }

    override fun getItemCount() = messages.size
}
