package com.dicoding.greenquest.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.databinding.ActivityQuizBinding
import com.dicoding.greenquest.ui.main.MainActivity

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    companion object{
        const val QUEST_EXTRA = "quest_extra"
    }

    private lateinit var quest: QuestEntity

    private var userID = 0
    private var points = 0

    private val userAnswers = mutableMapOf<Int, String>()

    private val quizViewModel: QuizViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quest = intent.getParcelableExtra(QUEST_EXTRA)!!

        quizViewModel.getSession().observe(this) { user ->
            userID = user.user_id
            points = user.points
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvQuiz.layoutManager = layoutManager

        showQuiz(quest.typeName)

        binding.fabQuizSelesai.setOnClickListener { submitQuiz() }
    }

    private fun showQuiz(typeName: String) {
        quizViewModel.getQuiz(typeName).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("QuizActivity", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("QuizActivity", "Success: ${result.data}")

                    val data = result.data
                    val adapter = QuizAdapter { position, answer ->
                        userAnswers[position] = answer
                    }
                    adapter.submitList(data)
                    binding.rvQuiz.adapter = adapter

                }
                is Result.Error -> {
                    showLoading(false)
                    Log.e("QuizActivity", "Error: ${result.error}")
                    if (result.error.contains("401")) {
                        showToast("Unauthorized, please re-login.")
                        quizViewModel.logout()
                    } else {
                        showToast(result.error)
                    }
                }
                else -> {
                    showLoading(false)
                    Log.e("QuizActivity", "Unknown")
                    showToast("Unknown")
                }
            }
        }
    }

    private fun submitQuiz() {
        if (userAnswers.size < binding.rvQuiz.adapter?.itemCount ?: 0) {
            Toast.makeText(this, "Harap jawab semua pertanyaan!", Toast.LENGTH_SHORT).show()
            return
        }

        // Ambil jawaban dan kirim ke ViewModel
        val typeName = quest.typeName
        val answers = userAnswers.values.toList()

        if (answers.size >= 2) { // Contoh minimal dua jawaban
            postQuiz(typeName, answers[0], answers[1])
        }
    }

    private fun postQuiz(typeName: String, firstAnswer: String, secondAnswer: String) {
        quizViewModel.postQuiz(typeName, firstAnswer, secondAnswer).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("QuizActivity", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("QuizActivity", "Success: ${result.data}")
                    val points = result.data.payload.pointsAwarded
                    val correctAnswer = result.data.payload.correctAnswers
                     if (points == 0) {
                         showAlert("Coba lagi ya", "semua jawabanmu salah. Tapi tenang, masih ada hari esok :)", points)
                     } else {
                         showAlert("Yeah", " jawaban anda benar $correctAnswer Kamu berhasil mendapatkan $points poin", points)
                     }

                }
                is Result.Error -> {
                    showLoading(false)
                    Log.e("QuizActivity", "Error: ${result.error}")
                    if (result.error.contains("401")) {
                        showToast("Unauthorized, please re-login.")
                        quizViewModel.logout()
                    } else {
                        showToast(result.error)
                    }
                }
                else -> {
                    showLoading(false)
                    Log.e("QuizActivity", "Unknown")
                    showToast("Unknown")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAlert(title: String, message: String, pointsGet: Int) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Ya") { _, _ ->
                quizViewModel.updateQuest(quest)
                updateUserPoints(userID, points, pointsGet)
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            create()
            show()
        }
    }

    private fun updateUserPoints(userId: Int, points: Int, newPoints: Int) {
        quizViewModel.updateUserPoints(userId, points, newPoints)
    }
}