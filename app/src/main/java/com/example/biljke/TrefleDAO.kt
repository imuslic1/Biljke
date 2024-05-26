package com.example.biljke

import android.graphics.Bitmap
import com.example.biljke.Biljka
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
class TrefleDAO{
    private lateinit var defaultBitmap: Bitmap
    private val trefle_api_key : String = BuildConfig.TREFLE_API_KEY


    suspend fun getImage(
        biljka: Biljka) : GetPlantResponse? {

        var latinskiNaziv : String? = null
        val input = biljka.naziv
        val pattern = "\\((.*?)\\)".toRegex()
        val matchResult = pattern.find(input)
        latinskiNaziv = matchResult?.value?.removeSurrounding("(", ")")

        return withContext(Dispatchers.IO) {
            var response = ApiAdapter.retrofit.getPlant(latinskiNaziv)
            val responseBody = response.body()
            return@withContext responseBody
        }
        //TODO: podesiti u klasi Api da vraća URL slike iz web baze
    }

    fun fixData(biljka: Biljka):Biljka {
        //TODO: zavrsiti implementaciju
    }






}