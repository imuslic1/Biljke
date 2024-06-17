package com.example.biljke

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.room.Dao
import com.example.biljke.MyConverter.BitmapConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.stream.Collectors.toList

@Dao
interface BiljkaDAO {
    suspend fun saveBiljka(biljka: Biljka) : Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val context = ContextProvider.getContext()
                val db = BiljkaDatabase.getInstance(context)
                val daLiImaUBazi : List<Biljka> = db.roomDao().getPlantByName(biljka.naziv)

                if(daLiImaUBazi.isEmpty())
                    biljka.id = db.roomDao().saveBiljka(biljka)

                true
            } catch(e : Exception) {
                false
            }
        }
    }

    suspend fun fixOfflineBiljka() : Int {
        var count : Int = 0
        return withContext(Dispatchers.IO) {
            try {
                val context = ContextProvider.getContext()
                val trefle = TrefleDAO()
                trefle.setContext(context)

                val db = BiljkaDatabase.getInstance(context)
                var plantsToUpdate : MutableList<Biljka> = db.roomDao().getUnchecked().toMutableList()
                var updatedPlants : MutableList<Biljka> = mutableListOf()

                lateinit var temp : Biljka
                for(i in plantsToUpdate.indices) {
                    try {
                        temp = trefle.fixData(plantsToUpdate[i])
                    } catch(e : Exception) {
                        continue
                    }

                    if(temp != plantsToUpdate[i]) {
                        ++count
                        temp.onlineChecked = true
                        //plantsToUpdate[i] = temp
                        updatedPlants.add(temp)
                    }
                }

                db.roomDao().updateChecked(updatedPlants.toList())
                return@withContext count
            } catch(e : Exception) {
                throw e
            }
        }
    }

    suspend fun addImage(idBiljke:Long, bitmap : Bitmap) : Boolean{
        return withContext(Dispatchers.IO) {
            try {
                val context = ContextProvider.getContext()
                val db = BiljkaDatabase.getInstance(context)

                var biljkaId : List<Biljka> = db.roomDao().getById(idBiljke)
                if(biljkaId.isEmpty())
                    return@withContext false


                var bitmapDB : List<BiljkaBitmap> = db.roomDao().doesBitmapExist(idBiljke)
                if(bitmapDB.isNotEmpty())
                    return@withContext false

                var byteArrayFromBitmap : ByteArray = BitmapConverter().fromBitmap(bitmap)

                db.roomDao().saveBitmap(BiljkaBitmap(idBiljke= idBiljke, bitmap = byteArrayFromBitmap))

                true

            } catch(e : Exception) {
                Log.d("MainActivity", "exception: " + e.message)
                false
            }
        }
    }

    suspend fun getImageFromDB(idBiljke: Long) : Bitmap {
        return withContext(Dispatchers.IO) {
            lateinit var bitmap : Bitmap
            try {
                val context = ContextProvider.getContext()
                val db = BiljkaDatabase.getInstance(context)

                bitmap = BitmapConverter().toBitmap(db.roomDao().getBitmapById(idBiljke))
            } catch(e : Exception) {
                throw e
            }

            return@withContext bitmap;
        }
    }

    suspend fun getAllBiljkas() : List<Biljka> {
        return withContext(Dispatchers.IO) {
            try {
                val context = ContextProvider.getContext()
                val db = BiljkaDatabase.getInstance(context)
                return@withContext db.roomDao().getAll()

            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun clearData(){
        return withContext(Dispatchers.IO) {
            try {
                val context = ContextProvider.getContext()
                val db = BiljkaDatabase.getInstance(context)
                return@withContext db.roomDao().deleteAll()

            } catch (e: Exception) {
                throw e
            }
        }
    }

}