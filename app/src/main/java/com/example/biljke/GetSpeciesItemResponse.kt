package com.example.biljke

import com.google.gson.annotations.SerializedName

data class GetSpeciesItemResponse (
    @SerializedName("data") val plants: List<SpeciesItem>,
)