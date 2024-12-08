package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("payload")
	val payload: LoginPayload? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class LoginUser(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any? = null,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class LoginPayload(

	@field:SerializedName("user")
	val user: LoginUser,

	@field:SerializedName("token")
	val token: String? = null
)
