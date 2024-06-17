package com.example.biljke

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(
    foreignKeys = [ForeignKey(
        entity = Biljka::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idBiljke"),
        onDelete = ForeignKey.CASCADE
    )]
)

class BiljkaBitmap (
    @PrimaryKey(autoGenerate = true)  var id : Long? = null,
    @ColumnInfo("idBiljke")     val idBiljke : Long,
    @ColumnInfo("bitmap")       var bitmap : ByteArray
)

