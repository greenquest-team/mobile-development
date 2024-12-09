package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("payload")
	val payload: List<PayloadItem> = emptyList(),

	@field:SerializedName("message")
	val message: String? = null
)

data class PayloadItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("points")
	val points: String? = null
)
