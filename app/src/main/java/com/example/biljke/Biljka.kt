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
    @PrimaryKey(autoGenerate = true)        var id: Long? = null,

    @ColumnInfo(name = "name")              val naziv: String,
    @ColumnInfo(name = "family")            val porodica: String,
    @ColumnInfo(name = "med_warning")       val medicinskoUpozorenje: String,
    @ColumnInfo(name = "med_usages")        val medicinskeKoristi: List<MedicinskaKorist>,
    @ColumnInfo(name = "taste_profile")     val profilOkusa: ProfilOkusaBiljke,
    @ColumnInfo(name = "dishes")            val jela: List<String>,
    @ColumnInfo(name = "climates")          val klimatskiTipovi: List<KlimatskiTip>,
    @ColumnInfo(name = "soils")             val zemljisniTipovi: List<Zemljiste>,
    @ColumnInfo(name = "onlineChecked")     var onlineChecked : Boolean


) : Parcelable