package com.dicoding.greenquest.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "UserQuest")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val questId: Int,
    val userId: Int,
    val materialId: Int,
    val typeName: String,
    val descriptionQuest: String,
    val image: String,
    val wasteTypesId: Int,
    val questType: String,
    val progress: Int,
    var isCompleted: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val pointsAwarded: Int,
    ) : Parcelable