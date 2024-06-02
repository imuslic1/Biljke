package com.example.biljke

import com.google.gson.annotations.SerializedName


data class GetPlantsWithColorResponse(
    @SerializedName("data") val plantIDs : List<SpeciesItem>
)