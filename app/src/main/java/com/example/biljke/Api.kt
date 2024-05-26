package com.example.biljke

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("species/search")
    suspend fun getPlant(
        @Query("query") query: String?,
        @Query("api_key") apiKey: String = BuildConfig.TREFLE_API_KEY
    ): Response<GetPlantResponse>
}