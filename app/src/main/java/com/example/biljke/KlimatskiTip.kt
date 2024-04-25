package com.example.biljke

enum class KlimatskiTip(val opis: String) {
    SREDOZEMNA("Mediteranska klima - suha, topla ljeta i blage zime"),
    TROPSKA("Tropska klima - topla i vlažna tokom cijele godine"),
    SUBTROPSKA("Subtropska klima - blage zime i topla do vruća ljeta"),
    UMJERENA("Umjerena klima - topla ljeta i hladne zime"),
    SUHA("Sušna klima - niske padavine i visoke temperature tokom cijele godine"),
    PLANINSKA("Planinska klima - hladne temperature i kratke sezone rasta");

    companion object {
        fun getFromDescription(description : String) : KlimatskiTip? {
            return entries.toTypedArray().find { it.opis == description }
        }
    }
}