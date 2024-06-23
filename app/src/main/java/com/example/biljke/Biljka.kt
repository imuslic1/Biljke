package com.example.biljke

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
@Entity
@Parcelize
data class Biljka (
    @PrimaryKey(autoGenerate = true)            var id:Long?=null,
    @ColumnInfo(name = "naziv")                 val naziv: String,
    @ColumnInfo(name = "family")                val porodica: String,
    @ColumnInfo(name = "medicinskoUpozorenje")  val medicinskoUpozorenje: String,
    @ColumnInfo(name = "medicinskeKoristi")     val medicinskeKoristi: List<MedicinskaKorist>,
    @ColumnInfo(name = "profilOkusa")           val profilOkusa: ProfilOkusaBiljke,
    @ColumnInfo(name = "jela")                  val jela: List<String>,
    @ColumnInfo(name = "klimatskiTipovi")       val klimatskiTipovi: List<KlimatskiTip>,
    @ColumnInfo(name = "zemljisniTipovi")       val zemljisniTipovi: List<Zemljiste>,
    @ColumnInfo(name = "onlineChecked")         var onlineChecked : Boolean = false


) : Parcelable