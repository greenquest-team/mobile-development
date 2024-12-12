package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class QuizPostResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("payload")
	val payload: QuizPostPayload,

	@field:SerializedName("message")
	val message: String
)

data class QuizPostPayload(

	@field:SerializedName("points_awarded")
	val pointsAwarded: Int,

	@field:SerializedName("correct_answers")
	val correctAnswers: Int
)
