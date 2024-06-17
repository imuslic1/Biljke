package com.example.biljke

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface RoomDAO {

    @Insert
    suspend fun saveBiljka(biljka : Biljka) : Long

    @Insert
    suspend fun saveBitmap(biljkaBitmap : BiljkaBitmap) : Long

    @Query("SELECT * FROM Biljka")
    suspend fun getAll() : List<Biljka>

    @Query("SELECT * FROM Biljka WHERE id = :id LIMIT 1")
    suspend fun getById(id : Long) : List<Biljka>

    @Query("DELETE FROM Biljka")
    suspend fun deleteAll()

    @Query("SELECT * FROM BILJKA WHERE onlineChecked = 0")
    suspend fun getUnchecked() : List<Biljka>

    @Query("SELECT * FROM BiljkaBitmap WHERE idBiljke = :id LIMIT 1")
    suspend fun doesBitmapExist(id: Long) : List<BiljkaBitmap>

    @Query("SELECT bitmap FROM BiljkaBitmap WHERE idBiljke = :id")
    suspend fun getBitmapById(id : Long) : ByteArray

    @Query("SELECT * FROM Biljka WHERE naziv = :naziv LIMIT 1")
    suspend fun getPlantByName(naziv: String) : List<Biljka>

    //provjeriti da li radi kako treba, možda da se ipak koristi query
    @Update
    suspend fun updateChecked(updatedBiljke : List<Biljka>)




}