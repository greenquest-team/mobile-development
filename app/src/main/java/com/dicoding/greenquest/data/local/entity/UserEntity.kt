package com.dicoding.greenquest.data.local.entity

data class UserEntity(
    val user_id: Int,
    val name: String,
    val email: String,
    val password: String,
    val token: String,
    val isLogin: Boolean = false
)
