package com.dicoding.greenquest

import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.data.remote.response.QuestMaterialItem
import com.dicoding.greenquest.data.remote.response.QuestQuizItem
import com.dicoding.greenquest.data.remote.response.QuestReminderItem
import com.dicoding.greenquest.data.remote.response.QuestScanItem
import java.util.Calendar

inline fun <reified T> mapQuestResponseToEntity(
    questList: List<T?>?,
    userId: Int
): List<QuestEntity> {
    return questList?.mapNotNull { quest ->
        when (quest) {
            is QuestScanItem -> QuestEntity(
                id = 0,
                questId = quest.id ?: 0,
                userId = userId,
                materialId = 0, // Default untuk `QuestScanItem`
                typeName = quest.typeName ?: "",
                descriptionQuest = quest.descriptionQuest ?: "",
                image = quest.image ?: "",
                wasteTypesId = quest.wasteTypesId ?: 0,
                questType = quest.questType ?: "scan",
                progress = 0,
                isCompleted = false,
                createdAt = System.currentTimeMillis(),
                pointsAwarded = 15
            )
            is QuestReminderItem -> QuestEntity(
                id = 0,
                questId = quest.id ?: 0,
                userId = userId,
                materialId = 0, // Default untuk `QuestReminderItem`
                typeName = "Reminder", // Tetapkan jika tidak ada di respons
                descriptionQuest = quest.descriptionQuest ?: "",
                image = quest.image ?: "",
                wasteTypesId = quest.wasteTypesId ?: 0,
                questType = quest.questType ?: "reminder",
                progress = 0,
                isCompleted = false,
                createdAt = System.currentTimeMillis(),
                pointsAwarded = 10
            )
            is QuestMaterialItem -> QuestEntity(
                id = 0,
                questId = quest.id ?: 0,
                userId = userId,
                materialId = quest.wasteTypesId ?: 0,
                typeName = quest.typeName ?: "material",
                descriptionQuest = quest.descriptionQuest ?: "",
                image = quest.image ?: "",
                wasteTypesId = quest.wasteTypesId ?: 0,
                questType = quest.questType ?: "material",
                progress = 0,
                isCompleted = false,
                createdAt = System.currentTimeMillis(),
                pointsAwarded = 20
            )
            is QuestQuizItem -> QuestEntity(
                id = 0,
                questId = quest.id ?: 0,
                userId = userId,
                materialId = 0, // Default untuk `QuestQuizItem`
                typeName = "Quiz",
                descriptionQuest = quest.descriptionQuest ?: "",
                image = quest.image ?: "",
                wasteTypesId = quest.wasteTypesId ?: 0,
                questType = quest.questType ?: "quiz",
                progress = 0,
                isCompleted = false,
                createdAt = System.currentTimeMillis(),
                pointsAwarded = 40
            )
            else -> null // Jika tipe tidak sesuai, abaikan
        }
    } ?: emptyList()
}

fun getStartOfLocalDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun getEndOfLocalDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}


