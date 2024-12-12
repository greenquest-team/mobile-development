package com.dicoding.greenquest.data.prefs

data class UserModel(
    val user_id: Int,
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val token: String,
    val image: String,
    val tgl: String,
    val points: Int,
    val isLogin: Boolean = false
)
