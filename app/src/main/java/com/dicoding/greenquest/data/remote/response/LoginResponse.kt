package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("payload")
	val payload: Payload,

	@field:SerializedName("message")
	val message: String
)

data class UserLogin(

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: String,

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

data class Payload(

	@field:SerializedName("user")
	val user: UserLogin,

	@field:SerializedName("token")
	val token: String
)
