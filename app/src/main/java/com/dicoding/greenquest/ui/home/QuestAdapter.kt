package com.dicoding.greenquest.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.data.CardData
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.databinding.ItemCardQuestGreenBinding
import com.dicoding.greenquest.databinding.ItemCardQuestRedBinding

class QuestAdapter: ListAdapter<QuestEntity, RecyclerView.ViewHolder>(DIFF_CALLBACK){

    companion object {
        const val ITEM_GREEN = 0
        const val ITEM_RED = 1

        val DIFF_CALLBACK: DiffUtil.ItemCallback<QuestEntity> =
            object : DiffUtil.ItemCallback<QuestEntity>() {
                override fun areItemsTheSame(oldItem: QuestEntity, newItem: QuestEntity): Boolean {
                    return oldItem == newItem
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: QuestEntity, newItem: QuestEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) ITEM_GREEN else ITEM_RED
    }

    class GreenViewHolder(val binding: ItemCardQuestGreenBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quest: QuestEntity) {
            binding.cardTitleQuest.text = quest.username
            binding.cardButtonQuest.setOnClickListener {
                Intent()
            }
             Glide.with(binding.root.context)
                .load(quest.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.cardImageQuest)
        }
    }

    class RedViewHolder(val binding: ItemCardQuestRedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quest: QuestEntity) {
            binding.cardTitleQuest.text = quest.username
            Glide.with(binding.root.context)
                .load(quest.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.cardImageQuest)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_GREEN -> {
                val binding = ItemCardQuestGreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GreenViewHolder(binding)
            }
            ITEM_RED -> {
                val binding = ItemCardQuestRedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RedViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val quest = getItem(position)
        when (holder) {
            is GreenViewHolder -> holder.bind(quest)
            is RedViewHolder -> holder.bind(quest)
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(quest)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: QuestEntity)
    }
}