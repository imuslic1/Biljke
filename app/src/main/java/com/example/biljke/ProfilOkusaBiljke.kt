package com.example.biljke

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ProfilOkusaBiljke(val opis: String) : Parcelable {
    MENTA("Mentol - osvježavajući, hladan ukus"),
    CITRUSNI("Citrusni - osvježavajući, aromatičan"),
    SLATKI("Sladak okus"),
    BEZUKUSNO("Obični biljni okus - travnat, zemljast ukus"),
    LJUTO("Ljuto ili papreno"),
    KORIJENASTO("Korenast - drvenast i gorak ukus"),
    AROMATICNO("Začinski - topli i aromatičan ukus"),
    GORKO("Gorak okus");

    companion object {
        fun getFromDescription(description : String) : ProfilOkusaBiljke? {
            return entries.toTypedArray().find { it.opis == description }
        }
    }
}