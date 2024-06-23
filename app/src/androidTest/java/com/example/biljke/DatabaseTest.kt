package com.example.biljke

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.biljke.BiljkaDatabase.BiljkaDAO


@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    companion object {
        lateinit var db: SupportSQLiteDatabase
        lateinit var context: Context
        lateinit var roomDb: BiljkaDatabase
        lateinit var biljkaDAO: BiljkaDAO

        @BeforeClass
        @JvmStatic
        fun createDB() = runBlocking {
            val scenarioRule = ActivityScenario.launch(MainActivity::class.java)
            context = ApplicationProvider.getApplicationContext()
            roomDb = Room.inMemoryDatabaseBuilder(context, BiljkaDatabase::class.java).build()
            biljkaDAO = roomDb.biljkaDao()
            biljkaDAO.getAllBiljkas()
            db = roomDb.openHelper.readableDatabase
        }
    }

    @get:Rule
    val intentsTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun insertBiljka() = runBlocking {
        val vel : Int = biljkaDAO.getAllBiljkas().size
        biljkaDAO.saveBiljka(
            Biljka(
                naziv = "Ružmarin (Rosmarinus officinalis)",
                porodica = "Lamiaceae (metvice)",
                medicinskoUpozorenje = "Treba ga koristiti umjereno i konsultovati se sa ljekarom pri dugotrajnoj upotrebi ili upotrebi u većim količinama.",
                medicinskeKoristi = listOf(MedicinskaKorist.PROTUUPALNO, MedicinskaKorist.REGULACIJAPRITISKA),
                profilOkusa = ProfilOkusaBiljke.AROMATICNO,
                jela = listOf("Pečeno pile", "Grah", "Gulaš"),
                klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUHA),
                zemljisniTipovi = listOf(Zemljiste.SLJUNOVITO, Zemljiste.KRECNJACKO)
            )
        )

        ViewMatchers.assertThat(biljkaDAO.getAllBiljkas().size, CoreMatchers.`is`(vel+1))
    }

    @Test
    fun deleteAll() = runBlocking {
        biljkaDAO.clearData()
        ViewMatchers.assertThat(biljkaDAO.getAllBiljkas().size, CoreMatchers.`is`(0))
    }

    @Test
    fun insertBiljkaUListu() = runBlocking {
        val vel : Int = biljkaDAO.getAllBiljkas().size
        val novaBiljka = Biljka(
            naziv = "Nanaa (Mentha spicata)",
            porodica = "Lamiaceae (metvice)",
            medicinskoUpozorenje = "Nije preporučljivo za trudnice, dojilje i djecu mlađu od 3 godine.",
            medicinskeKoristi = listOf(MedicinskaKorist.PROTUUPALNO, MedicinskaKorist.PROTIVBOLOVA),
            profilOkusa = ProfilOkusaBiljke.MENTA,
            jela = listOf("Jogurt sa voćem", "Gulaš"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.UMJERENA),
            zemljisniTipovi = listOf(Zemljiste.GLINENO, Zemljiste.CRNICA)
        )
        biljkaDAO.saveBiljka(novaBiljka)
        val listaNakonInserta = biljkaDAO.getAllBiljkas()
        assertEquals(true, listaNakonInserta.contains(novaBiljka))
    }



}