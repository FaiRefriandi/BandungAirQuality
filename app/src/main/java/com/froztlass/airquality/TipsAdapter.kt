package com.froztlass.airquality

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide

class TipsAdapter(private val tipsList: List<TipsItem>) : RecyclerView.Adapter<TipsAdapter.TipsViewHolder>() {

    private var expandedPosition: Int = RecyclerView.NO_POSITION

    inner class TipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                if (expandedPosition == position) {
                    // biar nutupnya gada transisi
                    contentTextView.visibility = View.GONE
                    expandedPosition = RecyclerView.NO_POSITION
                } else {

                    if (expandedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(expandedPosition)
                    }

                    // transisi buka card
                    TransitionManager.beginDelayedTransition(itemView as ViewGroup, AutoTransition().apply {
                        duration = 300
                    })
                    contentTextView.visibility = View.VISIBLE
                    expandedPosition = position
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expandable_tips, parent, false)
        return TipsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        val tipsItem = tipsList[position]
        holder.titleTextView.text = tipsItem.title
        holder.contentTextView.text = tipsItem.content

        // tamnel buat recycle
        val resourceId = when (position) {
            0 -> R.drawable.ic_tips1
            1 -> R.drawable.ic_tips2
            2 -> R.drawable.ic_tips3
            3 -> R.drawable.ic_tips4
            4 -> R.drawable.ic_tips5
            else -> R.drawable.ic_tips1
        }
        Glide.with(holder.thumbnailImageView.context)
            .load(resourceId)
            .into(holder.thumbnailImageView)

        holder.contentTextView.visibility = if (position == expandedPosition) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = tipsList.size
}

data class TipsItem(val title: String, val content: String)
