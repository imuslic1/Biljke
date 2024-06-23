package com.example.biljke

import com.google.gson.annotations.SerializedName

data class SpeciesItem (
    @SerializedName("id")               val id: Int,
    @SerializedName("scientific_name")  val sciName : String,
    @SerializedName("image_url")        val imageUrl: String,
)