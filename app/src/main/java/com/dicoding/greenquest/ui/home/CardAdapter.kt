package com.dicoding.greenquest.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.greenquest.R
import com.dicoding.greenquest.data.CardData

class CardAdapter(private val cardList: List<CardData>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.card_image)
        val cardTitle: TextView = itemView.findViewById(R.id.card_title)
        val cardButton: Button = itemView.findViewById(R.id.card_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = cardList[position]
        holder.cardImage.setImageResource(currentItem.imageResId)
        holder.cardTitle.text = currentItem.title
        // ... set other UI elements ...
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}