package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class QuestResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("payload")
	val payload: QuestPayload? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class QuestScanItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("type_name")
	val typeName: String? = null,

	@field:SerializedName("description_quest")
	val descriptionQuest: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("waste_types_id")
	val wasteTypesId: Int? = null,

	@field:SerializedName("quest_type")
	val questType: String? = null
)

data class QuestQuizItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("type_name")
	val typeName: String? = null,

	@field:SerializedName("description_quest")
	val descriptionQuest: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("waste_types_id")
	val wasteTypesId: Int? = null,

	@field:SerializedName("quest_type")
	val questType: String? = null
)

data class QuestPayload(

	@field:SerializedName("questScan")
	val questScan: List<QuestScanItem?>? = null,

	@field:SerializedName("questReminder")
	val questReminder: List<QuestReminderItem?>? = null,

	@field:SerializedName("questMaterial")
	val questMaterial: List<QuestMaterialItem?>? = null,

	@field:SerializedName("questQuiz")
	val questQuiz: List<QuestQuizItem?>? = null
)

data class QuestReminderItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("description_quest")
	val descriptionQuest: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("waste_types_id")
	val wasteTypesId: Int? = null,

	@field:SerializedName("quest_type")
	val questType: String? = null
)

data class QuestMaterialItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("type_name")
	val typeName: String? = null,

	@field:SerializedName("description_quest")
	val descriptionQuest: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("waste_types_id")
	val wasteTypesId: Int? = null,

	@field:SerializedName("quest_type")
	val questType: String? = null
)
