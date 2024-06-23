package com.example.biljke

import com.google.gson.annotations.SerializedName

data class GetSpeciesResponse(
    @SerializedName("data") val plantOfSpecies: Species,
)

