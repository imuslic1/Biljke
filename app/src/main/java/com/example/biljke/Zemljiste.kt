package com.example.biljke

enum class Zemljiste(val naziv: String) {
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