package com.example.biljke

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Adapter
import android.widget.Button
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.hasToString
import org.hamcrest.number.OrderingComparison

import com.example.biljke.BitmapMatcher.withBitmap
import com.example.biljke.ErrorMatcher.withErrorText


import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestS2 {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun dodajNovuBiljku(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())

        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("Peceno povrce"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())

        val recyclerView = activityRule.scenario.onActivity {
            val recyclerView = it.findViewById<RecyclerView>(R.id.biljkeRV)
            val itemCount = recyclerView.adapter!!.itemCount
            //10 biljaka je vec dodano u BiljkeStaticData
            MatcherAssert.assertThat(itemCount, OrderingComparison.greaterThan(10))
        }


    }
    @Test
    fun izmijeniJelo(){
        val scenario = ActivityScenario.launch(NovaBiljkaActivity::class.java)
        var adapter: Adapter? = null
        var buttonText : String? = null

        scenario.onActivity { activity ->
            val listView = activity.findViewById<ListView>(R.id.jelaLV)
            adapter = listView.adapter
            buttonText = activity.findViewById<Button>(R.id.dodajJeloBtn).text.toString()
        }
        assert(buttonText == "Dodaj jelo")

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        var brojJela = adapter?.count ?: 0
        assert(brojJela == 1)

        onData(hasToString("Gulas")).inAdapterView(withId(R.id.jelaLV)).perform(click())

        scenario.onActivity { activity ->
            buttonText = activity.findViewById<Button>(R.id.dodajJeloBtn).text.toString()
        }
        assert(buttonText == "Izmijeni jelo")

        onView(withId(R.id.jeloET)).perform(typeText("s"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        scenario.onActivity { activity ->
            buttonText = activity.findViewById<Button>(R.id.dodajJeloBtn).text.toString()
        }
        assert(buttonText == "Dodaj jelo")

        brojJela = adapter?.count ?: 0
        assert(brojJela == 1)

        onData(hasToString("Gulass")).inAdapterView(withId(R.id.jelaLV)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText(""))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        brojJela = adapter?.count ?: 0
        assert(brojJela == 1)

    }

    @Test
    fun neprhivatljivNazivBiljke(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan (Thymus vulgaris)"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())

        onView(withId(R.id.nazivET)).check(matches(hasErrorText("Naziv je neprihvatljive dužine!")))
    }

    @Test
    fun neprihvatljivNazivPorodice(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae  (usnatice)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("Peceno povrce"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.porodicaET)).check(matches(hasErrorText("Naziv je neprihvatljive dužine!")))
    }

    @Test
    fun neprihvatljivoMedUpozorenje(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("Vecina ljudi ne osjeca utjecaj"))

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("Peceno povrce"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.medicinskoUpozorenjeET)).check(matches(hasErrorText("Naziv je neprihvatljive dužine!")))
    }

    @Test
    fun neprihvatljivNazivJela(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("G"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Naziv je neprihvatljive dužine!")))

        onView(withId(R.id.jeloET)).perform(typeText("ulas s pire krompirom"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Naziv je neprihvatljive dužine!")))

        onView(withId(R.id.jeloET)).perform(clearText())
        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
    }

    @Test
    fun dodajJelo(){
        val scenario = ActivityScenario.launch(NovaBiljkaActivity::class.java)
        var adapter: Adapter? = null

        scenario.onActivity { activity ->
            val listView = activity.findViewById<ListView>(R.id.jelaLV)
            adapter = listView.adapter
        }

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        val brojJela = adapter?.count ?: 0
        assert(brojJela == 1)
    }

    @Test
    fun jeloPostojiUListi(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(withId(R.id.jeloET)).check(matches(hasErrorText("Jelo već postoji!")))
    }

    @Test
    fun praznaListaJela(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))
        closeSoftKeyboard()

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(withErrorText("Dodajte barem jedno jelo")))
    }

    @Test
    fun neoznacenaMedKorist(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("Gulas"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(withErrorText("Odaberite barem jednu medicinsku korist")))
    }

    @Test
    fun neoznacenaKlima(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("Peceno povrce"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(withErrorText("Odaberite barem jedan klimatski tip")))
    }

    @Test
    fun neoznacenoZemljiste(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("Peceno povrce"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Mentol - osvježavajući, hladan ukus")).inAdapterView(withId(R.id.profilOkusaLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(withErrorText("Odaberite barem jedan zemljisni tip")))
    }

    @Test
    fun neoznacenProfilOkusa(){
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        onView(withId(R.id.nazivET)).perform(typeText("Timijan"))
        onView(withId(R.id.porodicaET)).perform(typeText("Lamiaceae (usnate)"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("...."))

        onView(withId(R.id.jeloET)).perform(typeText("Peceno povrce"))
        closeSoftKeyboard()
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onData(hasToString("Protuupalno - za smanjenje upale")).inAdapterView(withId(R.id.medicinskaKoristLV)).perform(click())
        onData(hasToString("Umjerena klima - topla ljeta i hladne zime")).inAdapterView(withId(R.id.klimatskiTipLV)).perform(click())
        onData(hasToString("Ilovača")).inAdapterView(withId(R.id.zemljisniTipLV)).perform(click())

        onView(withId(R.id.dodajBiljkuBtn)).perform(click())
        onView(withId(R.id.dodajBiljkuBtn)).check(matches(withErrorText("Odaberite profil okusa")))
    }

    @Test
    fun uslikajIPrikaziSliku() {
        onView(withId(R.id.novaBiljkaBtn)).perform(click())
        Intents.init()

        val slikaZaTest: Bitmap = BitmapFactory.decodeResource(
            InstrumentationRegistry.getInstrumentation().targetContext.resources,
            R.drawable.test_img
        )

        val slikaRezultat = Intent()
        val bundle = Bundle()
        bundle.putParcelable("data", slikaZaTest)
        slikaRezultat.putExtras(bundle)

        val rezultat = Instrumentation.ActivityResult(Activity.RESULT_OK, slikaRezultat)

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(rezultat)

        onView(withId(R.id.uslikajBiljkuBtn)).perform(click())
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
        onView(withId(R.id.slikaIV)).check(matches(withBitmap(slikaZaTest)))

        Intents.release()
    }
}