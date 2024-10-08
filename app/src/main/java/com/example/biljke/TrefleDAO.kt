package com.example.biljke

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class TrefleDAO {
    private lateinit var context: Context
    lateinit var defaultBitmap: Bitmap
    fun setContext(context: Context) {
        this.context = context
        defaultBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_img)
    }



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
            lateinit var speciesIdByLatinList : List<SpeciesItem>
            val latinskiNaziv = getLatinskiNaziv(biljka)

            try {
                speciesIdByLatinList = ApiAdapter.retrofit.getPlants(latinskiNaziv).body()!!.plants
            } catch(e: Exception) {
                return@withContext defaultBitmap
            }

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

                    /* ČEKIRATI, NE RADI KAKO TREBA
                    val conditions = listOf(
                        Pair(6..9 to 1..5, KlimatskiTip.SREDOZEMNA),
                        Pair(6..9 to 5..8, KlimatskiTip.SUBTROPSKA),
                        Pair(8..10 to 7..10, KlimatskiTip.TROPSKA),
                        Pair(4..7 to 3..7, KlimatskiTip.UMJERENA),
                        Pair(7..9 to 1..2, KlimatskiTip.SUHA),
                        Pair(0..5 to 3..7, KlimatskiTip.PLANINSKA)
                    )

                    for (it in conditions) {
                        if(speciesThatMatches.growth.lightValue in it.first.first && speciesThatMatches.growth.soilTexture in it.first.second)
                            fixedKlimatskiTipovi.add(it.second)
                    }
                    */

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
                if(fixedKlimatskiTipovi.isEmpty())
                    fixedKlimatskiTipovi = biljka.klimatskiTipovi.toMutableList()

                if(speciesThatMatches.growth.soilTexture != null) {
                    fixedZemljisniTipovi.clear()

                    /* ČEKIRATI, NE RADI KAKO TREBA
                    val conditions = listOf(
                        Pair(9..9, Zemljiste.SLJUNOVITO),
                        Pair(10..10, Zemljiste.KRECNJACKO),
                        Pair(1..2, Zemljiste.GLINENO),
                        Pair(3..4, Zemljiste.PJESKOVITO),
                        Pair(5..6, Zemljiste.ILOVACA),
                        Pair(7..8, Zemljiste.CRNICA)
                    )

                    for (it in conditions) {
                        if(speciesThatMatches.growth.soilTexture in it.first)
                            fixedZemljisniTipovi.add(it.second)
                    }
                    */

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
                if(fixedZemljisniTipovi.isEmpty())
                    fixedZemljisniTipovi = biljka.zemljisniTipovi.toMutableList()

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

                var fixedBiljka = Biljka(id = biljka.id,
                                         naziv = biljka.naziv,
                                         porodica = fixedPorodica,
                                         medicinskoUpozorenje = fixedMedicinskoUpozorenje,
                                         medicinskeKoristi = biljka.medicinskeKoristi,
                                         profilOkusa = biljka.profilOkusa,
                                         jela = fixedJela,
                                         klimatskiTipovi = fixedKlimatskiTipovi,
                                         zemljisniTipovi = fixedZemljisniTipovi,
                                         onlineChecked = true)

                return@withContext fixedBiljka
            }
    }

    suspend fun getPlantswithFlowerColor(flower_color : String, substr : String) : List<Biljka> {
        return withContext(Dispatchers.IO) {
            var listaToReturn : MutableList<Biljka> = mutableListOf()
            var listaDobavljenih : MutableList<Species> = mutableListOf()
            var speciesIdByCommonNameList =
                ApiAdapter.retrofit.getPlantsWithColor(flower_color, substr).body()!!.plantIDs

            if(speciesIdByCommonNameList.isEmpty())
                throw IllegalArgumentException("Biljka sa datim nazivom nije pronađena.")

            speciesIdByCommonNameList.forEach{
                listaDobavljenih.add(ApiAdapter.retrofit.getPlant(it.id).body()!!.plantOfSpecies)
            }

            for(it in listaDobavljenih) {
                lateinit var biljkaZaDodati : Biljka
                var nazivZaDodati : String?
                if(it.name != null)
                     nazivZaDodati = it.name + " (" + it.latName + ")"
                else nazivZaDodati = " (" + it.latName + ")"

                var porodicaZaDodati : String = it.family
                val medicinskoUpozorenjeZaDodati : String = ""
                val medicinskeKoristiZaDodati : List<MedicinskaKorist> = listOf()
                val profilOkusaZaDodati : ProfilOkusaBiljke = ProfilOkusaBiljke.AROMATICNO
                var jelaZaDodati : List<String> = listOf()
                var klimatskiTipoviZaDodati : MutableList<KlimatskiTip> = mutableListOf()
                var zemljisniTipoviZaDodati : MutableList<Zemljiste> = mutableListOf()

                if(it.growth.lightValue != null && it.growth.atmosphericHumidity != null) {
                    if(it.growth.lightValue in 6..9 && it.growth.atmosphericHumidity in 1..5)
                        klimatskiTipoviZaDodati.add(KlimatskiTip.SREDOZEMNA)

                    if(it.growth.lightValue in 6..9 && it.growth.atmosphericHumidity in 5..8)
                        klimatskiTipoviZaDodati.add(KlimatskiTip.SUBTROPSKA)

                    if(it.growth.lightValue in 8..10 && it.growth.atmosphericHumidity in 7..10)
                        klimatskiTipoviZaDodati.add(KlimatskiTip.TROPSKA)

                    if(it.growth.lightValue in 4..7 && it.growth.atmosphericHumidity in 3..7)
                        klimatskiTipoviZaDodati.add(KlimatskiTip.UMJERENA)

                    if(it.growth.lightValue in 7..9 && it.growth.atmosphericHumidity in 1..2)
                        klimatskiTipoviZaDodati.add(KlimatskiTip.SUHA)

                    if(it.growth.lightValue in 0..5 && it.growth.atmosphericHumidity in 3..7)
                        klimatskiTipoviZaDodati.add(KlimatskiTip.PLANINSKA)
                }

                if(it.growth.soilTexture != null) {
                    if(it.growth.soilTexture == 9)
                        zemljisniTipoviZaDodati.add(Zemljiste.SLJUNOVITO)

                    else if(it.growth.soilTexture == 10)
                        zemljisniTipoviZaDodati.add(Zemljiste.KRECNJACKO)

                    else if(it.growth.soilTexture in 1..2)
                        zemljisniTipoviZaDodati.add(Zemljiste.GLINENO)

                    else if(it.growth.soilTexture in 3..4)
                        zemljisniTipoviZaDodati.add(Zemljiste.PJESKOVITO)

                    else if(it.growth.soilTexture in 5..6)
                        zemljisniTipoviZaDodati.add(Zemljiste.ILOVACA)

                    else if(it.growth.soilTexture in 7..8)
                        zemljisniTipoviZaDodati.add(Zemljiste.CRNICA)
                }

                biljkaZaDodati = Biljka(
                    naziv = nazivZaDodati,
                    porodica = porodicaZaDodati,
                    medicinskoUpozorenje = medicinskoUpozorenjeZaDodati,
                    medicinskeKoristi = medicinskeKoristiZaDodati,
                    profilOkusa = profilOkusaZaDodati,
                    jela = jelaZaDodati,
                    klimatskiTipovi = klimatskiTipoviZaDodati,
                    zemljisniTipovi = zemljisniTipoviZaDodati
                )
                listaToReturn.add(biljkaZaDodati)
            }

            return@withContext listaToReturn.toList()
        }
    }
}