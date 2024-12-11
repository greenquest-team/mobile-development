package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("payload")
	val payload: Payload,

	@field:SerializedName("message")
	val message: String
)

data class UserPayload(

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("avatar")
	val avatar: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String,

	@field:SerializedName("username")
	val username: String,

	@field:SerializedName("points")
	val points: String
)
