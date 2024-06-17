package com.example.biljke

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Dao
import com.example.biljke.MyConverter.BitmapConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.stream.Collectors.toList

@Dao
interface BiljkaDAO {

    var _context : Context

    private fun setContext(context: Context) {
        this._context = context
    }

    suspend fun saveBiljka(biljka: Biljka) : Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val db = BiljkaDatabase.getInstance(_context)
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
                val trefle = TrefleDAO()
                trefle.setContext(_context)

                val db = BiljkaDatabase.getInstance(_context)
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
                val db = BiljkaDatabase.getInstance(_context)

                var biljkaId : List<Biljka> = db.roomDao().getById(idBiljke)
                if(biljkaId.isEmpty())
                    return@withContext false
                lateinit var converter : BitmapConverter

                var bitmapDB : List<ByteArray> = db.roomDao().getBitmap(converter.fromBitmap(bitmap))
                if(bitmapDB.isNotEmpty())
                    return@withContext false

                db.roomDao().saveBitmap(BiljkaBitmap(idBiljke= idBiljke, bitmap = bitmap))

                true

            } catch(e : Exception) {
                false
            }
        }
    }

    suspend fun getAllBiljkas() : List<Biljka> {
        return withContext(Dispatchers.IO) {
            try {
                val db = BiljkaDatabase.getInstance(_context)
                return@withContext db.roomDao().getAll()

            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun clearData(){
        return withContext(Dispatchers.IO) {
            try {
                val db = BiljkaDatabase.getInstance(_context)
                return@withContext db.roomDao().deleteAll()

            } catch (e: Exception) {
                throw e
            }
        }
    }



}