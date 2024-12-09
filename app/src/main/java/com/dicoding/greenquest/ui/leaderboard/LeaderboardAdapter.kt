package com.dicoding.greenquest.ui.leaderboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.data.local.entity.LeaderboardEntity
import com.dicoding.greenquest.databinding.ItemLeaderboardBinding

class LeaderboardAdapter : ListAdapter<LeaderboardEntity, LeaderboardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder (val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leaderboard: LeaderboardEntity) {
            binding.textUsernameLeaderboard.text = leaderboard.username.toString()
            binding.textPointsEarned.text = leaderboard.points.toString()
            Glide.with(binding.root.context)
                .load(R.drawable.test_image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.ivAvatarLeaderboard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val leaderboard = getItem(position)
        holder.bind(leaderboard)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<LeaderboardEntity> =
            object : DiffUtil.ItemCallback<LeaderboardEntity>() {
                override fun areItemsTheSame(oldItem: LeaderboardEntity, newItem: LeaderboardEntity): Boolean {
                    return oldItem == newItem
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: LeaderboardEntity, newItem: LeaderboardEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}