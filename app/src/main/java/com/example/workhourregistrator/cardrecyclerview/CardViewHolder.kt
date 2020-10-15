package com.example.workhourregistrator.cardrecyclerview

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workhourregistrator.R


class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val listLayout: LinearLayout = itemView.findViewById(R.id.list_item_layout)
    var view: View = itemView

    private var duration: Long = 500

    fun bindItems(card: Card) {
        val textView: TextView = listLayout.findViewById(R.id.list_item_textView)
        textView.text = card.title
    }

    fun animationScaleIn(view: View, position: Int) {
        val animationScaleIn = ScaleAnimation(
            0.0f,
            1.0f,
            0.0f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animationScaleIn.duration = duration //+ ((duration*position)/2)
        view.startAnimation(animationScaleIn)
    }
}