package com.dicoding.greenquest.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Story")
data class QuestEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo(name = "image")
    val image: String?,

    @field:ColumnInfo(name = "username")
    val username: String?,

    @field:ColumnInfo(name = "description")
    val description: String?,

    @field:ColumnInfo(name = "createdAt")
    val createdAt: String?,

    ) : Parcelable