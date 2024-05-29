package com.example.biljke

import android.content.Context
import android.graphics.Bitmap
import com.example.biljke.Biljka
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class TrefleDAO(
    context: CoroutineContext
){
    private lateinit var defaultBitmap: Bitmap
    private val trefle_api_key : String = BuildConfig.TREFLE_API_KEY

    private fun getLatinskiNaziv(
        biljka: Biljka
    ) : String {
        var latinskiNaziv : String
        val input = biljka.naziv
        val pattern = "\\((.*?)\\)".toRegex()
        val matchResult = pattern.find(input)
        latinskiNaziv = matchResult!!.value.removeSurrounding("(", ")")
        return latinskiNaziv
    }

    /*
    suspend fun getImage(
        biljka: Biljka) : Bitmap? {
        return withContext(Dispatchers.IO) {
            var response = ApiAdapter.retrofit.getPlant(latinskiNaziv)
            val responseBody = response.body()
            return@withContext responseBody
        }
        //TODO: podesiti u klasi Api da vraća URL slike iz web baze
    }
    */


    suspend fun fixData(biljka: Biljka): Biljka {
        //TODO: zavrsiti implementaciju
        // BITNO: COROUTINE SE POKREĆE PRIJE NEGO SE POZOVE OVA METODA, TAMO GDJE SE ONA POZIVA
        // CINEASTE LV8/SearchFragment
            return withContext(Dispatchers.IO) {
                val latinskiNaziv = getLatinskiNaziv(biljka)
                var speciesIdByLatinList =
                    ApiAdapter.retrofit.getPlants(latinskiNaziv).body()!!.plants
                var idOfSearchedPlant: Int = speciesIdByLatinList[0].id
                var speciesThatMatches: Species =
                    ApiAdapter.retrofit.getPlant(idOfSearchedPlant).body()!!

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

                if(speciesThatMatches.toxic != null) {
                    if(!biljka.medicinskoUpozorenje.lowercase().contains("toksicno"))
                        fixedMedicinskoUpozorenje += " TOKSIČNO"
                }

                if(speciesThatMatches.lightValue != null && speciesThatMatches.atmosphericHumidity != null) {
                    fixedKlimatskiTipovi.clear()
                    if(speciesThatMatches.lightValue in 6..9 && speciesThatMatches.atmosphericHumidity in 1..5)
                        fixedKlimatskiTipovi.add(KlimatskiTip.SREDOZEMNA)

                    if(speciesThatMatches.lightValue in 6..9 && speciesThatMatches.atmosphericHumidity in 5..8)
                        fixedKlimatskiTipovi.add(KlimatskiTip.SUBTROPSKA)

                    if(speciesThatMatches.lightValue in 8..10 && speciesThatMatches.atmosphericHumidity in 7..10)
                        fixedKlimatskiTipovi.add(KlimatskiTip.TROPSKA)

                    if(speciesThatMatches.lightValue in 4..7 && speciesThatMatches.atmosphericHumidity in 3..7)
                        fixedKlimatskiTipovi.add(KlimatskiTip.UMJERENA)

                    if(speciesThatMatches.lightValue in 7..9 && speciesThatMatches.atmosphericHumidity in 1..2)
                        fixedKlimatskiTipovi.add(KlimatskiTip.SUHA)

                    if(speciesThatMatches.lightValue in 0..5 && speciesThatMatches.atmosphericHumidity in 3..7)
                        fixedKlimatskiTipovi.add(KlimatskiTip.PLANINSKA)
                }

                if(speciesThatMatches.soilTexture != null) {
                    fixedZemljisniTipovi.clear()
                    if(speciesThatMatches.soilTexture == 9)
                        fixedZemljisniTipovi.add(Zemljiste.SLJUNOVITO)

                    else if(speciesThatMatches.soilTexture == 10)
                        fixedZemljisniTipovi.add(Zemljiste.KRECNJACKO)

                    else if(speciesThatMatches.soilTexture in 1..2)
                        fixedZemljisniTipovi.add(Zemljiste.GLINENO)

                    else if(speciesThatMatches.soilTexture in 3..4)
                        fixedZemljisniTipovi.add(Zemljiste.PJESKOVITO)

                    else if(speciesThatMatches.soilTexture in 5..6)
                        fixedZemljisniTipovi.add(Zemljiste.ILOVACA)

                    else if(speciesThatMatches.soilTexture in 7..8)
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