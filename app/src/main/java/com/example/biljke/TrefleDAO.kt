package com.example.biljke

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.biljke.Biljka
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.coroutines.CoroutineContext

class TrefleDAO(
    context: Context
) {
    val defaultBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_img)

    private fun getLatinskiNaziv(biljka: Biljka) : String {
        var latinskiNaziv : String
        val input = biljka.naziv
        val pattern = "\\(([^)]+)\\)".toRegex()
        val matchResult = pattern.find(input)
        latinskiNaziv = matchResult!!.value.removeSurrounding("(", ")")
        return latinskiNaziv
    }

    suspend fun getImage(biljka: Biljka) : Bitmap? {
        return withContext(Dispatchers.IO) {
            val latinskiNaziv = getLatinskiNaziv(biljka)
            var speciesIdByLatinList = ApiAdapter.retrofit.getPlants(latinskiNaziv).body()!!.plants

            if(speciesIdByLatinList.isEmpty())
                return@withContext defaultBitmap

            var urlSlikaPretrazeneBiljke: String = speciesIdByLatinList[0].imageUrl

            if(latinskiNaziv.lowercase() != speciesIdByLatinList[0].sciName.lowercase())
                return@withContext defaultBitmap

            val inputStream = URL(urlSlikaPretrazeneBiljke).openStream()
            return@withContext BitmapFactory.decodeStream(inputStream)
        }
    }

    suspend fun fixData(biljka: Biljka): Biljka {
        // TODO: zavrsiti implementaciju
        // BITNO: COROUTINE SE POKREĆE PRIJE NEGO SE POZOVE OVA METODA, TAMO GDJE SE ONA POZIVA
        // CINEASTE LV8/SearchFragment
            return withContext(Dispatchers.IO) {
                val latinskiNaziv = getLatinskiNaziv(biljka)
                var speciesIdByLatinList =
                    ApiAdapter.retrofit.getPlants(latinskiNaziv).body()!!.plants

                if(speciesIdByLatinList.isEmpty())
                    throw IllegalArgumentException("Biljka sa latinskim nazivom nije pronađena.")

                var idOfSearchedPlant: Int = speciesIdByLatinList[0].id
                var speciesThatMatches: Species =
                    ApiAdapter.retrofit.getPlant(idOfSearchedPlant).body()!!.plantOfSpecies

                if(latinskiNaziv.lowercase() != speciesThatMatches.latName.lowercase())
                    throw IllegalArgumentException("Biljka sa datim latinskim nazivom nije pronađena.")

                var fixedPorodica: String = biljka.porodica
                var fixedMedicinskoUpozorenje: String = biljka.medicinskoUpozorenje
                var fixedJela: MutableList<String> = biljka.jela.toMutableList()
                var fixedKlimatskiTipovi: MutableList<KlimatskiTip> =
                    biljka.klimatskiTipovi.toMutableList()
                var fixedZemljisniTipovi: MutableList<Zemljiste> =
                    biljka.zemljisniTipovi.toMutableList()

                if (biljka.porodica != speciesThatMatches.family)
                    fixedPorodica = speciesThatMatches.family

                if(!speciesThatMatches.isEdible) {
                    fixedMedicinskoUpozorenje += " NIJE JESTIVO"
                    fixedJela.clear()
                }

                if(speciesThatMatches.specifications.toxic != null) {
                    if(!biljka.medicinskoUpozorenje.lowercase().contains("toksicno"))
                        fixedMedicinskoUpozorenje += " TOKSIČNO"
                }

                if(speciesThatMatches.growth.lightValue != null && speciesThatMatches.growth.atmosphericHumidity != null) {
                    fixedKlimatskiTipovi.clear()
                    if(speciesThatMatches.growth.lightValue in 6..9 && speciesThatMatches.growth.atmosphericHumidity in 1..5)
                        fixedKlimatskiTipovi.add(KlimatskiTip.SREDOZEMNA)

                    if(speciesThatMatches.growth.lightValue in 6..9 && speciesThatMatches.growth.atmosphericHumidity in 5..8)
                        fixedKlimatskiTipovi.add(KlimatskiTip.SUBTROPSKA)

                    if(speciesThatMatches.growth.lightValue in 8..10 && speciesThatMatches.growth.atmosphericHumidity in 7..10)
                        fixedKlimatskiTipovi.add(KlimatskiTip.TROPSKA)

                    if(speciesThatMatches.growth.lightValue in 4..7 && speciesThatMatches.growth.atmosphericHumidity in 3..7)
                        fixedKlimatskiTipovi.add(KlimatskiTip.UMJERENA)

                    if(speciesThatMatches.growth.lightValue in 7..9 && speciesThatMatches.growth.atmosphericHumidity in 1..2)
                        fixedKlimatskiTipovi.add(KlimatskiTip.SUHA)

                    if(speciesThatMatches.growth.lightValue in 0..5 && speciesThatMatches.growth.atmosphericHumidity in 3..7)
                        fixedKlimatskiTipovi.add(KlimatskiTip.PLANINSKA)
                }

                if(speciesThatMatches.growth.soilTexture != null) {
                    fixedZemljisniTipovi.clear()
                    if(speciesThatMatches.growth.soilTexture == 9)
                        fixedZemljisniTipovi.add(Zemljiste.SLJUNOVITO)

                    else if(speciesThatMatches.growth.soilTexture == 10)
                        fixedZemljisniTipovi.add(Zemljiste.KRECNJACKO)

                    else if(speciesThatMatches.growth.soilTexture in 1..2)
                        fixedZemljisniTipovi.add(Zemljiste.GLINENO)

                    else if(speciesThatMatches.growth.soilTexture in 3..4)
                        fixedZemljisniTipovi.add(Zemljiste.PJESKOVITO)

                    else if(speciesThatMatches.growth.soilTexture in 5..6)
                        fixedZemljisniTipovi.add(Zemljiste.ILOVACA)

                    else if(speciesThatMatches.growth.soilTexture in 7..8)
                        fixedZemljisniTipovi.add(Zemljiste.CRNICA)
                }

                //  Validacija i provjera uslova:
                //      - naziv ostaje isti zajedno sa latinskim koji se parsirao
                //      - porodicu treba updateovati ako ne odgovara
                //      - na medicinska upozorenja treba dodati "NIJE JESTIVO" ako je edible == false
                //      - profilOkusa ostaje isti
                //      - jela se prazne ako je edible == false, inače ostaju kao što su dodana
                //      - dodati substring " TOKSIČNO" ako je toxicity != null
                //      - klimatskiTip update-ati na osnovu tabele, akko nijedan relevantni parametar
                //        nije null. Ako jesu, ostavi sve onako kako stoji
                //      - zemljisniTip u ovisnosti od soil_texture:
                //        * ako je null, ostavi sve kako je uneseno
                //        * ako nije null, kreiraj novu listu s jednim elementom (jedino odgovarajuce
                //          zemljiste

                var fixedBiljka = Biljka(biljka.naziv,
                                         fixedPorodica,
                                         fixedMedicinskoUpozorenje,
                                         biljka.medicinskeKoristi,
                                         biljka.profilOkusa,
                                         fixedJela,
                                         fixedKlimatskiTipovi,
                                         fixedZemljisniTipovi)

                return@withContext fixedBiljka
            }
    }

}