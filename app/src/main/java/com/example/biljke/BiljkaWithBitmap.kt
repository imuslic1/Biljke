package com.example.biljke

import androidx.room.Embedded
import androidx.room.Relation

data class BiljkaWithBiljkaBitmap(
    @Embedded val biljka : Biljka,
    @Relation(
        parentColumn = "id",
        entityColumn = "idBiljke"
    )
    val biljkaBitmap: BiljkaBitmap
) {
}