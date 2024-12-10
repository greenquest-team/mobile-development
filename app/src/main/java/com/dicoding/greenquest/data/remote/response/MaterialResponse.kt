package com.dicoding.greenquest.data.remote.response

import com.google.gson.annotations.SerializedName

data class MaterialResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("payload")
	val payload: List<MaterialPayloadItem> = emptyList(),

	@field:SerializedName("message")
	val message: String? = null
)

data class MaterialPayloadItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("type_name")
	val typeName: String? = null,

	@field:SerializedName("description_mat")
	val descriptionMat: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("waste_types_id")
	val wasteTypesId: Int? = null
)
