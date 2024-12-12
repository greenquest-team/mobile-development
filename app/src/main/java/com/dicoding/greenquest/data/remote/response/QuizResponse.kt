package com.dicoding.greenquest.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class QuizResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("payload")
	val payload: List<QuizPayloadItem>,

	@field:SerializedName("message")
	val message: String
)

data class QuizPayloadItem(

	@field:SerializedName("option_d")
	val optionD: String,

	@field:SerializedName("type_name")
	val typeName: String,

	@field:SerializedName("option_b")
	val optionB: String,

	@field:SerializedName("option_c")
	val optionC: String,

	@field:SerializedName("question")
	val question: String,

	@field:SerializedName("correct_answer")
	val correctAnswer: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("option_a")
	val optionA: String,

	@field:SerializedName("waste_types_id")
	val wasteTypesId: Int,

	@field:SerializedName("image")
	val image: String
)
