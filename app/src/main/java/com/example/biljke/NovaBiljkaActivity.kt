package com.example.biljke

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView.INVALID_POSITION
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class NovaBiljkaActivity : AppCompatActivity() {
    private val br: BroadcastReceiver = ConnectivityBroadcastReceiver()
    private val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

    private lateinit var nazivET : EditText
    private lateinit var porodicaET : EditText
    private lateinit var medicinskoUpozorenjeET : EditText
    private lateinit var jeloET : EditText

    private lateinit var medicinskaKoristLV : ListView
    private lateinit var klimatskiTipLV: ListView
    private lateinit var zemljisniTipLV : ListView
    private lateinit var profilOkusaLV : ListView
    private lateinit var jelaLV : ListView

    private lateinit var dodajJeloBtn : Button
    private lateinit var dodajBiljkuBtn : Button
    private lateinit var uslikajBiljkuBtn : Button

    private lateinit var slikaIV : ImageView
    private val requestImageCapture = 1

    /**
     *  Koriste se za prikazivanje svih enum tipova za choice u odgovarajućim ListView poljima
     */
    private val _listaKlimatskihTipova : List<String> = KlimatskiTip.entries.map{it.opis}
    private val _listaZemljisnihTipova: List<String> = Zemljiste.entries.map{it.naziv}
    private val _listaMedicinskihKoristi: List<String> = MedicinskaKorist.entries.map{it.opis}
    private val _listaProfilOkusa: List<String> = ProfilOkusaBiljke.entries.map{it.opis}

    private val listaJela = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plant)
        val biljkeList = intent.getParcelableArrayListExtra<Biljka>("biljkeList")

        registerReceiver(br, filter)


        nazivET = findViewById(R.id.nazivET)
        porodicaET = findViewById(R.id.porodicaET)
        medicinskoUpozorenjeET = findViewById(R.id.medicinskoUpozorenjeET)
        jeloET = findViewById(R.id.jeloET)

        medicinskaKoristLV = findViewById(R.id.medicinskaKoristLV)
        medicinskaKoristLV.choiceMode =ListView.CHOICE_MODE_MULTIPLE

        klimatskiTipLV = findViewById(R.id.klimatskiTipLV)
        klimatskiTipLV.choiceMode =ListView.CHOICE_MODE_MULTIPLE

        zemljisniTipLV = findViewById(R.id.zemljisniTipLV)
        zemljisniTipLV.choiceMode =ListView.CHOICE_MODE_MULTIPLE

        profilOkusaLV = findViewById(R.id.profilOkusaLV)
        profilOkusaLV.choiceMode = ListView.CHOICE_MODE_SINGLE
        jelaLV = findViewById(R.id.jelaLV)

        dodajJeloBtn = findViewById(R.id.dodajJeloBtn)
        dodajBiljkuBtn = findViewById(R.id.dodajBiljkuBtn)
        uslikajBiljkuBtn = findViewById(R.id.uslikajBiljkuBtn)

        slikaIV = findViewById(R.id.slikaIV)
        slikaIV.setImageResource(R.drawable.default_img)

        /**
         * Podešavanje adaptera za prikaz elemenata listi u respektivnim ListViews
         */
        val jelaAdapter = ArrayAdapter(this, R.layout.lv_item_medium, listaJela)
        jelaLV.adapter = jelaAdapter

        val klimatskiAdapter = ArrayAdapter(this, R.layout.lv_item_small_multiplechoice, _listaKlimatskihTipova)
        klimatskiTipLV.adapter = klimatskiAdapter

        val zemljisteAdapter=ArrayAdapter(this, R.layout.lv_item_small_multiplechoice, _listaZemljisnihTipova)
        zemljisniTipLV.adapter=zemljisteAdapter

        val medKoristAdapter=ArrayAdapter(this, R.layout.lv_item_small_multiplechoice, _listaMedicinskihKoristi)
        medicinskaKoristLV.adapter=medKoristAdapter

        val profilOkusaAdapter=ArrayAdapter(this, R.layout.lv_item_small_singlechoice, _listaProfilOkusa)
        profilOkusaLV.adapter=profilOkusaAdapter

        dodajJeloBtn.setOnClickListener {
            val novoJelo = jeloET.text.toString()
            val dodajJeloString = getString(R.string.dodajJelo)

            dodajJeloBtn.text = dodajJeloString

            var dodati = true
            if (novoJelo.length < 2 || novoJelo.length > 20) {
                jeloET.error = "Naziv je neprihvatljive dužine!"
                dodati = false
            }

            var postoji = false
            listaJela.forEach {
                if (it.lowercase() == novoJelo.lowercase()) postoji = true
            }

            if (!postoji && dodati) {
                listaJela.add(novoJelo)
                jelaLV.adapter = jelaAdapter
                jeloET.text.clear()
            }

            if (postoji) {
                jeloET.error = "Jelo već postoji!"
            }
        }

        jelaLV.setOnItemClickListener{ parent, view, position, id ->
            val izmijeniJeloString = getString(R.string.izmijeniJelo)
            dodajJeloBtn.text = izmijeniJeloString
            val odabranoJelo = listaJela[position]
            jeloET.setText(odabranoJelo)
            listaJela.remove(odabranoJelo)
            jelaLV.adapter = jelaAdapter
        }

        dodajBiljkuBtn.setOnClickListener {
            val konstrNaziv = nazivET.text.toString()
            val konstrPorodica = porodicaET.text.toString()
            val konstrUpozorenje = medicinskoUpozorenjeET.text.toString()

            /**
             * Izvlačenje med. koristi koji idu u objekat nove biljke
             */
            val checkedMedKoristi = medicinskaKoristLV.checkedItemPositions
            val konstrListaMedKoristEnum = mutableListOf<MedicinskaKorist>()
            for (i in 0 until medicinskaKoristLV.adapter.count) {
                if (checkedMedKoristi[i]) {
                    val opis = medicinskaKoristLV.adapter.getItem(i) as String
                    val enumValue = MedicinskaKorist.getFromDescription(opis)
                    if(enumValue != null) konstrListaMedKoristEnum.add(enumValue)
                }
            }

            /**
             * Izvlačenje klim. tipova koji idu u objekat nove biljke
             */
            val checkedKlimTipovi = klimatskiTipLV.checkedItemPositions
            val konstrListaKlimTipEnum = mutableListOf<KlimatskiTip>()
            for (i in 0 until klimatskiTipLV.adapter.count) {
                if (checkedKlimTipovi[i]) {
                    val opis = klimatskiTipLV.adapter.getItem(i) as String
                    val enumValue = KlimatskiTip.getFromDescription(opis)
                    if(enumValue != null) konstrListaKlimTipEnum.add(enumValue)
                }
            }

            /**
             * Izvlačenje zemlj. tipova koji idu u objekat nove biljke
             */
            val checkedZemljista = zemljisniTipLV.checkedItemPositions
            val konstrListaZemljistaEnum = mutableListOf<Zemljiste>()
            for (i in 0 until zemljisniTipLV.adapter.count) {
                if (checkedZemljista[i]) {
                    val opis = zemljisniTipLV.adapter.getItem(i) as String
                    val enumValue = Zemljiste.getFromName(opis)
                    if(enumValue != null) konstrListaZemljistaEnum.add(enumValue)
                }
            }

            /**
             * Izvlačenje profila okusa koji idu u objdkat nove biljke
             */
            val checkedProfilOkusa = profilOkusaLV.checkedItemPosition
            var konstrProfilOkusaEnum : ProfilOkusaBiljke? = null
            if(checkedProfilOkusa != INVALID_POSITION) {
                val opis = profilOkusaLV.adapter.getItem(checkedProfilOkusa) as String
                val enumValue = ProfilOkusaBiljke.getFromDescription(opis)
                if (enumValue != null) konstrProfilOkusaEnum = enumValue
            }

            /**
             * Potrebno za validaciju
             */
            val jelo=jeloET.text.toString()
            var mozeSeDodati = true

            /**
             * Validacija podataka
             */

            //TODO: Check if is okay
            var duzinaNazivaOk = true
            if(konstrNaziv.length !in 2..40) { duzinaNazivaOk = false; mozeSeDodati = false }
            if(!duzinaNazivaOk) nazivET.error = "Naziv je neprihvatljive dužine!"


            var latNameGiven = true
            val pattern = "\\(([^)]+)\\)".toRegex()
            val matchResult = pattern.find(konstrNaziv)
            if(matchResult == null) { latNameGiven = false; mozeSeDodati = false }
            if(!latNameGiven) nazivET.error = "Naziv biljke mora sadržavati latinski naziv!"


            var duzinaPorodiceOk = true
            if(konstrPorodica.length !in 2..20) { duzinaPorodiceOk = false; mozeSeDodati = false }
            if(!duzinaPorodiceOk) porodicaET.error = "Naziv je neprihvatljive dužine!"

            var duzinaUpozorenjaOk = true
            if(konstrUpozorenje.length !in 2..20) { duzinaUpozorenjaOk = false; mozeSeDodati = false }
            if(!duzinaUpozorenjaOk) medicinskoUpozorenjeET.error = "Naziv je neprihvatljive dužine!"

            var duzinaJelaOk = true
            if(jelo.isNotEmpty() && (jelo.length !in 2..20)) { duzinaJelaOk = false; mozeSeDodati=false }
            if(!duzinaJelaOk) jeloET.error = "Naziv je neprihvatljive dužine!"

            var odabranaBarJednaMedKorist = true
            if (konstrListaMedKoristEnum.isEmpty()) odabranaBarJednaMedKorist=false
            if(!odabranaBarJednaMedKorist){
                mozeSeDodati = false
                Toast.makeText(this@NovaBiljkaActivity, "Odaberite barem jednu medicinsku korist", Toast.LENGTH_SHORT).show()
                dodajBiljkuBtn.error = "Odaberite barem jednu medicinsku korist"
            }

            var odabranBarJedanKlimTip = true
            if (konstrListaKlimTipEnum.isEmpty()) odabranBarJedanKlimTip=false
            if(!odabranBarJedanKlimTip){
                mozeSeDodati = false
                Toast.makeText(this@NovaBiljkaActivity, "Odaberite barem jedan klimatski tip", Toast.LENGTH_SHORT).show()
                dodajBiljkuBtn.error = "Odaberite barem jedan klimatski tip"
            }

            var odabranBarJedanZemljTip = true
            if (konstrListaZemljistaEnum.isEmpty()) odabranBarJedanZemljTip=false
            if(!odabranBarJedanZemljTip){
                mozeSeDodati = false
                Toast.makeText(this@NovaBiljkaActivity, "Odaberite barem jedan zemljisni tip", Toast.LENGTH_SHORT).show()
                dodajBiljkuBtn.error = "Odaberite barem jedan zemljisni tip"
            }

            var postojiBarJednoJelo = true
            if(listaJela.isEmpty()) postojiBarJednoJelo=false
            if(!postojiBarJednoJelo){
                mozeSeDodati = false
                Toast.makeText(this@NovaBiljkaActivity, "Dodajte barem jedno jelo", Toast.LENGTH_SHORT).show()
                dodajBiljkuBtn.error = "Dodajte barem jedno jelo"
            }

            var odabranProfilOkusa = true
            if(konstrProfilOkusaEnum == null) odabranProfilOkusa=false
            if(!odabranProfilOkusa){
                mozeSeDodati = false
                Toast.makeText(this@NovaBiljkaActivity, "Odaberite profil okusa", Toast.LENGTH_SHORT).show()
                dodajBiljkuBtn.error = "Odaberite profil okusa"
            }

            /**
             * Nakon uspjesne validacije, ovo je nova biljka
             */
            if(mozeSeDodati) {
                val novaBiljka = konstrProfilOkusaEnum?.let { it1 ->
                    Biljka(
                        naziv = konstrNaziv,
                        porodica = konstrPorodica,
                        medicinskoUpozorenje = konstrUpozorenje,
                        medicinskeKoristi = konstrListaMedKoristEnum,
                        profilOkusa = it1,
                        jela = listaJela,
                        klimatskiTipovi = konstrListaKlimTipEnum,
                        zemljisniTipovi = konstrListaZemljistaEnum
                    )
                }

                val context: Context = this
                var fixedBiljka: Biljka? = null
                val DB = BiljkaDatabase.getInstance(this)
                val biljkaDao = DB.biljkaDao()
                val scope = CoroutineScope(Job() + Dispatchers.Main)
                scope.launch {
                    try {
                        val dao = TrefleDAO()
                        dao.setContext(context)
                        fixedBiljka = novaBiljka?.let { it1 -> dao.fixData(it1) }
                        biljkeList?.add(fixedBiljka)
                        biljkaDao.insertBiljka(fixedBiljka!!)
                        Toast.makeText(this@NovaBiljkaActivity, "Biljka uspješno dodana!", Toast.LENGTH_SHORT).show()

                        /*val returnIntent = Intent()
                        returnIntent.putParcelableArrayListExtra("biljkeList",
                            biljkeList?.let { it1 -> ArrayList(it1) })

                         */
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    catch(e: IllegalArgumentException){
                        biljkaDao.saveBiljka(novaBiljka!!)
                        Toast.makeText(this@NovaBiljkaActivity, e.message + "Biljka dodana u listu bez validacije.\nProvjeriti latinski naziv!", Toast.LENGTH_LONG).show()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    catch(e: Exception) {
                        biljkaDao.saveBiljka(novaBiljka!!)
                        Toast.makeText(this@NovaBiljkaActivity, "Biljka dodana u listu bez validacije.\nProvjeriti internet konekciju!", Toast.LENGTH_LONG).show()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }

        uslikajBiljkuBtn.setOnClickListener {
            val kameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try{
                startActivityForResult(kameraIntent, requestImageCapture)
            }
            catch(e: ActivityNotFoundException){
                Toast.makeText(this@NovaBiljkaActivity, "Import slike neuspjesan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestImageCapture && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            slikaIV.setImageBitmap(imageBitmap)
        }
    }
}