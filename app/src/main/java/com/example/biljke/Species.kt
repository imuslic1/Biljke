package com.example.biljke

import com.google.gson.annotations.SerializedName

data class Species(
    @SerializedName("id")                           val id: Int,
    @SerializedName("family")                       val family: String,
    @SerializedName("edible")                       val isEdible: Boolean,
    @SerializedName("specifications.toxicity")      val toxic: String?,
    @SerializedName("growth.soil_texture")          val soilTexture: Int?,
    @SerializedName("growth.light")                 val lightValue: Int?,
    @SerializedName("growth.atmospheric_humidity")  val atmosphericHumidity: Int?,
)