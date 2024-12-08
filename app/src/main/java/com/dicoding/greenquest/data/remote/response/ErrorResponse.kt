package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("payload")
	val payload: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
