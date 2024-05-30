package com.example.biljke

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("species/search") // potencijalno dodati search? (srč upitnik)
    suspend fun getPlants(
        @Query("q") query: String,
        @Query("token") apiKey: String = BuildConfig.TREFLE_API_KEY
    ): Response<GetSpeciesItemResponse>

    @GET("species/{id}")
    suspend fun getPlant(
        @Path("id") id: Int,
        @Query("token") api_key: String = BuildConfig.TREFLE_API_KEY
    ): Response<GetSpeciesResponse>
}