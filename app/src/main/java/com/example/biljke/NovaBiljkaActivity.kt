package com.example.biljke

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NovaBiljkaActivity : AppCompatActivity() {
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
    private val REQUEST_IMAGE_CAPTURE = 1

    private val listaJela = mutableListOf<String>()
    //TODO: implementacija listi za ostale LV u layoutu

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plant)

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
        jelaLV = findViewById(R.id.jelaLV)

        dodajJeloBtn = findViewById(R.id.dodajJeloBtn)
        dodajBiljkuBtn = findViewById(R.id.dodajBiljkuBtn)
        uslikajBiljkuBtn = findViewById(R.id.uslikajBiljkuBtn)

        slikaIV = findViewById(R.id.slikaIV)
        slikaIV.setImageResource(R.drawable.default_img)



        val jelaAdapter = ArrayAdapter(this, R.layout.lv_item_medium, listaJela)
        jelaLV.adapter = jelaAdapter

        //TODO: implementacija adaptera za ostale LV iz layouta
        //val klimaAdapter = ArrayAdapter(this, R.layout.lv_item_medium, listaKlimatskihTipova)

        jelaLV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 'position' is the position of the item in the list
            // 'id' is the row id of the item
            val item =
                parent.getItemAtPosition(position) as String // replace String with your data type

            // Now 'item' contains the contents of the tapped item
            Toast.makeText(this@NovaBiljkaActivity, "Clicked item: $item", Toast.LENGTH_SHORT)
                .show()
        }


        dodajJeloBtn.setOnClickListener {
            val novoJelo = jeloET.text.toString()
            val dodajJeloString = getString(R.string.dodajJelo)


            dodajJeloBtn.text = dodajJeloString

            var dodati = true
            if (novoJelo.length < 2 || novoJelo.length > 20) {
                jeloET.setError("Naziv je neprihvatljive dužine!")
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
                //Toast.makeText(this@NovaBiljkaActivity, "Jelo postoji u listi", Toast.LENGTH_SHORT).show()
                jeloET.setError("Jelo već postoji")
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
            val nazivc= nazivET.text.toString()
            val porodica = porodicaET.text.toString()
            val upozorenje = medicinskoUpozorenjeET.text.toString()
            //validacija vrijednosti polja
            //ispis gresaka za sva neispravna polja
            //kako dodati novu bilju - moze se probati nacin da se globalna lista proslijedi po referenci u novaBiljkaActivity i napuni novom biljkom
            //ako je sve ok vrati se korisnik na mainActivity

            //VALIDACIJA:
            //duzina teksta >2 i <20 znakova (da li ima =)
            //ne mogu biti dva ista jela u listi jela (nije casesensitive)
            //u listama sa multiple mora biti bar jedna stvar odabrana
            //u listi jela mora biti dodano bar jedno jelo
            //u profiluOkusa mora bar jedan biti odabran
            //setError() metoda za ispis greske polja u validaciji
        }

        uslikajBiljkuBtn.setOnClickListener {
            val kameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try{
                startActivityForResult(kameraIntent, REQUEST_IMAGE_CAPTURE)
            }
            catch(e: ActivityNotFoundException){
                Toast.makeText(this@NovaBiljkaActivity, "Import slike neuspjesan", Toast.LENGTH_SHORT).show()
            }
        }

        //TODO: kod implementacije medicinskeKoristiLV podesiti
        // layout itema na list_item. Smanjiti veličinu teksta i centrirati adekvatno
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            slikaIV.setImageBitmap(imageBitmap)
        }
    }
}