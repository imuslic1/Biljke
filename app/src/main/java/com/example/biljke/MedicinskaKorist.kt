package com.example.biljke

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MedicinskaKorist(val opis: String) : Parcelable{
    SMIRENJE("Smirenje - za smirenje i relaksaciju"),
    PROTUUPALNO("Protuupalno - za smanjenje upale"),
    PROTIVBOLOVA("Protivbolova - za smanjenje bolova"),
    REGULACIJAPRITISKA("Regulacija pritiska - za regulaciju visokog/niskog pritiska"),
    REGULACIJAPROBAVE("Regulacija probave"),
    PODRSKAIMUNITETU("Podrška imunitetu");

    companion object {
        fun getFromDescription(description : String) : MedicinskaKorist? {
            return entries.toTypedArray().find { it.opis == description }
        }
    }
}