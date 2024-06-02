package com.example.biljke

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("species/search")
    suspend fun getPlants(
        @Query("q") query: String,
        @Query("token") apiKey: String = BuildConfig.TREFLE_API_KEY
    ): Response<GetSpeciesItemResponse>

    @GET("species/{id}")
    suspend fun getPlant(
        @Path("id") id: Int,
        @Query("token") api_key: String = BuildConfig.TREFLE_API_KEY
    ): Response<GetSpeciesResponse>

    //TODO: implementirati search po common_name pa zatim lokalno pretraziti iz dobivene liste sve biljke koje imaju trazenu boju cvijeta

    @GET("species/search")
    suspend fun getPlantsWithColor(
        @Query("filter[flower_color]") flowerColor : String,
        @Query("q") query : String,
        @Query("token") apiKey : String = BuildConfig.TREFLE_API_KEY,
    ): Response<GetPlantsWithColorResponse>

}