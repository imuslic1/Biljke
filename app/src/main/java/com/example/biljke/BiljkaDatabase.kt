package com.example.biljke

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Biljka::class, BiljkaBitmap::class], version = 1)
@TypeConverters(
    MyConverter.JelaConverter::class,
    MyConverter.KlimTipConverter::class,
    MyConverter.ZemljTipConverter::class,
    MyConverter.MedKoristConverter::class,
    MyConverter.BitmapConverter::class,
)
abstract class BiljkaDatabase : RoomDatabase() {
    abstract fun biljkaDao(): BiljkaDAO
    abstract fun roomDao(): RoomDAO
    companion object {
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
            ).build()
    }
}