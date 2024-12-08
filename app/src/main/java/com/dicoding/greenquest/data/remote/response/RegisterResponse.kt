package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("payload")
	val payload: RegisterPayload? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class RegisterPayload(

	@field:SerializedName("user")
	val user: RegisterUser? = null
)

data class RegisterUser(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
