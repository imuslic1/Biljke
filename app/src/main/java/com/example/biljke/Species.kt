package com.example.biljke

import com.google.gson.annotations.SerializedName

data class Species(
    @SerializedName("id")                       val id: Int,
    @SerializedName("common_name")              val name: String?,
    @SerializedName("scientific_name")          val latName: String,
    @SerializedName("family")                   val family: String,
    @SerializedName("edible")                   val isEdible: Boolean,
    @SerializedName("specifications")           val specifications: Specifications,
    @SerializedName("growth")                   val growth: Growth,

) {
    data class Specifications(
        @SerializedName("toxicity")             val toxic: String?,
        )

    data class Growth(
        @SerializedName("soil_texture")         val soilTexture: Int?,
        @SerializedName("light")                val lightValue: Int?,
        @SerializedName("atmospheric_humidity") val atmosphericHumidity: Int?,
        )
}