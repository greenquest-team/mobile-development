package com.dicoding.greenquest.ui.quiz

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
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.data.remote.response.QuizPayloadItem
import com.dicoding.greenquest.databinding.ItemLeaderboardBinding
import com.dicoding.greenquest.databinding.ItemQuizBinding
import com.dicoding.greenquest.ui.leaderboard.LeaderboardAdapter.MyViewHolder
import com.dicoding.greenquest.ui.profile.HistoryAdapter.Companion.DIFF_CALLBACK

class QuizAdapter(private val onAnswerSelected: (Int, String) -> Unit) : ListAdapter<QuizPayloadItem, QuizAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(val binding: ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quiz: QuizPayloadItem, position: Int, onAnswerSelected: (Int, String) -> Unit) {
            binding.textPertanyaanQuiz.text = "${position}. ${quiz.question}"
            binding.buttonAnswerA.text = quiz.optionA
            binding.buttonAnswerB.text = quiz.optionB
            binding.buttonAnswerC.text = quiz.optionC
            binding.buttonAnswerD.text = quiz.optionD
            Glide.with(binding.root.context)
                .load(quiz.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.imageGambarQuiz)

            binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val answer = when (checkedId) {
                    R.id.buttonAnswerA -> quiz.optionA
                    R.id.buttonAnswerB -> quiz.optionB
                    R.id.buttonAnswerC -> quiz.optionC
                    R.id.buttonAnswerD -> quiz.optionD
                    else -> ""
                }
                onAnswerSelected(adapterPosition, answer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val quiz = getItem(position)
        holder.bind(quiz, position + 1, onAnswerSelected)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<QuizPayloadItem> =
            object : DiffUtil.ItemCallback<QuizPayloadItem>() {
                override fun areItemsTheSame(oldItem: QuizPayloadItem, newItem: QuizPayloadItem): Boolean {
                    return oldItem == newItem
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: QuizPayloadItem, newItem: QuizPayloadItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}