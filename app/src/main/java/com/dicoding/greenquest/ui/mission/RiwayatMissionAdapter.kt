package com.dicoding.greenquest.ui.mission

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.databinding.ItemMisiBerlangsungGreenBinding
import com.dicoding.greenquest.databinding.ItemMisiBerlangsungRedBinding

class RiwayatMissionAdapter: ListAdapter<QuestEntity, RecyclerView.ViewHolder>(DIFF_CALLBACK){

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

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) ITEM_GREEN else ITEM_RED
    }

    class GreenViewHolder(val binding: ItemMisiBerlangsungGreenBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quest: QuestEntity) {
            binding.tvMisiTitle.text = quest.descriptionQuest
            Glide.with(binding.root.context)
                .load(quest.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.ivMisi)
        }
    }

    class RedViewHolder(val binding: ItemMisiBerlangsungRedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quest: QuestEntity) {
            binding.tvMisiTitle.text = quest.descriptionQuest
            Glide.with(binding.root.context)
                .load(quest.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.ivMisi)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_GREEN -> {
                val binding = ItemMisiBerlangsungGreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GreenViewHolder(binding)
            }
            ITEM_RED -> {
                val binding = ItemMisiBerlangsungRedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    }
}