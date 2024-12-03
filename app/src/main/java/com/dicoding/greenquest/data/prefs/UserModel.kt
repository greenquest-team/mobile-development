package com.dicoding.greenquest.data.prefs

data class UserModel(
    val user_id: Int,
    val name: String,
    val email: String,
    val password: String,
    val token: String,
    val isLogin: Boolean = false
)
