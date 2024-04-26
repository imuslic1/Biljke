package com.example.biljke

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelable
import androidx.versionedparcelable.VersionedParcelize
import java.io.Serial
import kotlinx.parcelize.Parcelize

@Parcelize
data class Biljka (val naziv: String,
              val porodica: String,
              val medicinskoUpozorenje: String,
              val medicinskeKoristi: List<MedicinskaKorist>,
              val profilOkusa: ProfilOkusaBiljke,
              val jela: List<String>,
              val klimatskiTipovi: List<KlimatskiTip>,
              val zemljisniTipovi: List<Zemljiste>
    ) : Parcelable

