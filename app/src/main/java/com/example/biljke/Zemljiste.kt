package com.example.biljke

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Zemljiste(val naziv: String) : Parcelable {
    PJESKOVITO("Pjeskovito zemljište"),
    GLINENO("Glinеno zemljište"),
    ILOVACA("Ilovača"),
    CRNICA("Crnica"),
    SLJUNOVITO("Šljunovito zemljište"),
    KRECNJACKO("Krečnjačko zemljište");

    companion object {
        fun getFromName(name:String) : Zemljiste? {
            return entries.toTypedArray().find { it.naziv == name }
        }
    }
}