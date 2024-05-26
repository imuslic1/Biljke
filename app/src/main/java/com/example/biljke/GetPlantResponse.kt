package com.example.biljke

import com.google.gson.annotations.SerializedName

data class GetPlantResponse (
    @SerializedName("page") val page: Int,
    @SerializedName("results") val plants: List<Biljka>,
    @SerializedName("total_pages") val pages: Int
)