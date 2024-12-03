package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class WasteTypeResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("payload")
	val payload: List<PayloadItem>,

	@field:SerializedName("message")
	val message: String
)

data class PayloadItem(

	@field:SerializedName("type_name")
	val typeName: String,

	@field:SerializedName("craft")
	val craft: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("waste_type_id")
	val wasteTypeId: Int,

	@field:SerializedName("id")
	val id: Int
)
