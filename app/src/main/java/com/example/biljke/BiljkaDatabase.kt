package com.example.biljke

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Database(entities = [Biljka::class, BiljkaBitmap::class], version = 2)
@TypeConverters(
    MyConverter.JelaConverter::class,
    MyConverter.KlimTipConverter::class,
    MyConverter.ZemljTipConverter::class,
    MyConverter.MedKoristConverter::class,
    MyConverter.BitmapConverter::class,
)
abstract class BiljkaDatabase : RoomDatabase() {
    abstract fun biljkaDao() : BiljkaDAO


    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS BiljkaBitmap")

                db.execSQL("""
                CREATE TABLE BiljkaBitmap(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idBiljke INTEGER NOT NULL,
                bitmap TEXT NOT NULL,
                FOREIGN KEY(idBiljke) REFERENCES Biljka(id) ON DELETE CASCADE)
                """)
            }
        }
        private var INSTANCE: BiljkaDatabase? = null
        fun getInstance(context: Context): BiljkaDatabase {
            if (INSTANCE == null) {
                synchronized(BiljkaDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BiljkaDatabase::class.java,
                "biljke-db"
            ).addMigrations(MIGRATION_1_2).build()
    }

    @Dao
    interface BiljkaDAO {
        @Insert
        suspend fun insertBiljka(biljka : Biljka) : Long

        @Insert
        suspend fun insertBiljkas(biljkeList : List<Biljka>)

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
        suspend fun getBitmapById(id : Long) : String

        @Query("SELECT * FROM Biljka WHERE naziv = :naziv LIMIT 1")
        suspend fun getPlantByName(naziv: String) : List<Biljka>

        //provjeriti da li radi kako treba, možda da se ipak koristi query
        @Update
        suspend fun updateChecked(updatedBiljke : List<Biljka>) : Int

        @Update
        suspend fun updateOne(biljka : Biljka)


        suspend fun saveBiljka(biljka: Biljka) : Boolean {
            return withContext(Dispatchers.IO) {
                try {
                    val daLiImaUBazi : List<Biljka> = getPlantByName(biljka.naziv)

                    if(daLiImaUBazi.isEmpty())
                        biljka.id = insertBiljka(biljka)

                    true
                } catch(e : Exception) {
                    false
                }
            }
        }

        suspend fun fixOfflineBiljka() : Int{
            val plantsToUpdate : List<Biljka> = getUnchecked()
            val updatedPlants : MutableList<Biljka> = mutableListOf()

            Log.e("POVUKAO LISTU IZ BAZE", plantsToUpdate.size.toString())

            val context = ContextProvider.getContext()
            val trefle = TrefleDAO()
            trefle.setContext(context)

            for(biljka in plantsToUpdate){
                try{
                    val fixedBiljka = trefle.fixData(biljka)
                    if(!compareBiljke(biljka, fixedBiljka)){
                        fixedBiljka.onlineChecked = true
                        updatedPlants.add(fixedBiljka)
                    }
                }
                catch(e: Exception){
                    continue
                }
            }

            return updateChecked(updatedPlants)
        }

        suspend fun addImage(idBiljke:Long, bitmap : Bitmap) : Boolean{
            return withContext(Dispatchers.IO) {
                try {
                    var biljkaId : List<Biljka> = getById(idBiljke)
                    if(biljkaId.isEmpty())
                        return@withContext false


                    var bitmapDB : List<BiljkaBitmap> = doesBitmapExist(idBiljke)
                    if(bitmapDB.isNotEmpty())
                        return@withContext false

                    var byteArrayFromBitmap : String = MyConverter.BitmapConverter().fromBitmap(bitmap)

                    saveBitmap(BiljkaBitmap(idBiljke = idBiljke, bitmap = byteArrayFromBitmap))

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
                    bitmap = MyConverter.BitmapConverter().toBitmap(getBitmapById(idBiljke))
                } catch(e : Exception) {
                    throw e
                }

                return@withContext bitmap;
            }
        }

        suspend fun getAllBiljkas() : List<Biljka> {
            return withContext(Dispatchers.IO) {
                try {
                    return@withContext getAll()
                } catch (e: Exception) {
                    throw e
                }
            }
        }

        suspend fun clearData(){
            return withContext(Dispatchers.IO) {
                try {
                    return@withContext deleteAll()
                } catch (e: Exception) {
                    throw e
                }
            }
        }

        fun compareBiljke(biljka : Biljka, fixedBiljka: Biljka) : Boolean{
            return biljka.porodica==fixedBiljka.porodica
                    && biljka.medicinskoUpozorenje==fixedBiljka.medicinskoUpozorenje
                    && biljka.jela == fixedBiljka.jela
                    && biljka.klimatskiTipovi == fixedBiljka.klimatskiTipovi
                    && biljka.zemljisniTipovi == fixedBiljka.zemljisniTipovi
        }
    }
}