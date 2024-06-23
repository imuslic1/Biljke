package com.example.biljke

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biljke.MyConverter.BitmapConverter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val NOVA_BILJKA_ACTIVITY_REQUEST_CODE = 0
    private lateinit var biljkeRV: RecyclerView
    private lateinit var selectMode: Spinner
    private lateinit var resetButton : Button
    private lateinit var newPlantButton : Button
    private lateinit var filteredBiljke : List<Biljka>
    private lateinit var pretragaLinearni : LinearLayout
    private lateinit var pretragaNazivET : EditText
    private lateinit var bojaSpinner : Spinner
    private lateinit var brzaPretragaButton : Button
    private lateinit var DB : BiljkaDatabase
    private var selectedBiljka : Biljka? = null
    private var currentMode : String = "medical"
    private var selectedColor : String = "red"
    private var pretrazenoPoBoji : Boolean = false

    private var biljkeList: MutableList<Biljka> = mutableListOf()
    private lateinit var pretrazeneBiljkePoBoji : List<Biljka>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize the DB
        ContextProvider.initialize(this)
        DB = BiljkaDatabase.getInstance(this)
        val biljkaDao = DB.biljkaDao()
        val trefle = TrefleDAO()
        trefle.setContext(this)

        //Add from BiljkeStaticData:
        lifecycleScope.launch {
            val biljkeDB : List<Biljka> = biljkaDao.getAllBiljkas()
            if(biljkeDB.isEmpty())
                biljkaDao.insertBiljkas(fetchBiljke())

            val result = biljkaDao.getAllBiljkas().toMutableList()
            when(result) {
                is List<Biljka> -> onFetchPlantsIntoDBSuccess(result)
                else -> onError()
            }


            //Insert images for plants
            val job2 = launch {
                for(biljka in result) {
                    var bitmap : Bitmap = trefle.getImage(biljka)!!
                    DB.biljkaDao().addImage(biljka.id!!, bitmap)

                }
                updateAdapter(currentMode, result)
            }
            job2.join()

            //FixOffline for plants that were added while WiFi was off
            val job3 = launch {
                try {
                    biljkaDao.fixOfflineBiljka()
                    val result = biljkaDao.getAllBiljkas().toMutableList()
                    when(result) {
                        is List<Biljka> -> onFetchPlantsIntoDBSuccess(result)
                        else -> onError()
                    }
                } catch(e: Exception) {
                    Toast.makeText(this@MainActivity, "Nije moguće provjeriti nevalidirane biljke.\nProvjerite internet konekciju.", Toast.LENGTH_LONG).show()
                }
            }
            job3.join()
        }

        //Initialize the variables
        selectMode = findViewById(R.id.modSpinner)
        biljkeRV = findViewById(R.id.biljkeRV)
        resetButton = findViewById(R.id.resetBtn)
        newPlantButton = findViewById(R.id.novaBiljkaBtn)
        pretragaLinearni = findViewById(R.id.pretragaLinearni)
        pretragaLinearni.visibility = View.GONE

        pretragaNazivET = findViewById(R.id.pretragaET)
        bojaSpinner = findViewById(R.id.bojaSPIN)
        brzaPretragaButton = findViewById(R.id.brzaPretraga)

        filteredBiljke = biljkeList

        val colorModes = arrayOf("red", "blue", "yellow", "orange", "purple", "brown", "green")
        bojaSpinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, colorModes)

        val viewModes = arrayOf("Medicinski", "Kuharski", "Botanički")
        selectMode.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, viewModes)

        biljkeRV.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        biljkeRV.adapter = MedicinskiModAdapter(biljkeList) { selectedBiljka ->
            filteredBiljke = filterBiljke(currentMode, selectedBiljka)
            updateAdapter(currentMode, filteredBiljke)
        }

        selectMode.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
                pretrazenoPoBoji = false
                currentMode = parent.getItemAtPosition(position).toString()

                if(currentMode == "Botanički")
                    pretragaLinearni.visibility = View.VISIBLE
                else pretragaLinearni.visibility = View.GONE

                val biljke = selectedBiljka?.let { filteredBiljke } ?: biljkeList
                updateAdapter(currentMode, biljke)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // default view je medicinski
                pretragaLinearni.visibility = View.GONE
                updateAdapter("Medicinski", biljkeList)
            }
        }

        resetButton.setOnClickListener {
            pretragaNazivET.text.clear()
            pretrazenoPoBoji = false
            selectedBiljka = null
            filteredBiljke = biljkeList
            updateAdapter(currentMode, biljkeList)
        }

        newPlantButton.setOnClickListener {
            val intent = Intent(this, NovaBiljkaActivity::class.java)
            intent.putParcelableArrayListExtra("biljkeList", ArrayList(biljkeList))
            startActivityForResult(intent, REQUEST_CODE)
        }

        bojaSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
                selectedColor = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                bojaSpinner.prompt = "Odaberite boju: "
            }
        }

        brzaPretragaButton.setOnClickListener {
            val substr : String = pretragaNazivET.text.toString()

            val dao = TrefleDAO()
            dao.setContext(this)
            if(substr != "")
                lifecycleScope.launch {
                    try {
                        pretrazeneBiljkePoBoji = dao.getPlantswithFlowerColor(selectedColor, substr)
                        updateAdapter(currentMode, pretrazeneBiljkePoBoji)
                        pretrazenoPoBoji = true

                    } catch(e: Exception) {
                        Toast.makeText(this@MainActivity, "Neuspjela pretraga!", Toast.LENGTH_SHORT).show()
                    }

                }
            else {
                pretragaNazivET.error = "Unesite naziv za pretragu!"
            }
        }
    }

    fun onFetchPlantsIntoDBSuccess(plants : MutableList<Biljka>) {
        biljkeList = plants
        filteredBiljke = biljkeList
        selectedBiljka = null
        updateAdapter(currentMode, biljkeList)
    }

    fun onError() {
        Toast.makeText(this@MainActivity, "Došlo je do greške u pristupu bazi podataka.\nPokušajte ponovno.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_CODE = 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOVA_BILJKA_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                val biljkaDao = DB.biljkaDao()
                val dao = TrefleDAO()
                dao.setContext(this)

                lifecycleScope.launch {
                    val result = biljkaDao.getAllBiljkas().toMutableList()

                    when(result) {
                        is List<Biljka> -> onFetchPlantsIntoDBSuccess(result)
                        else -> onError()
                    }

                    val job2 = launch {
                        for(biljka in biljkeList) {
                            var bitmap : Bitmap = dao.getImage(biljka)!!
                            val result = DB.biljkaDao().addImage(biljka.id!!, bitmap)
                        }
                        updateAdapter(currentMode, biljkeList)
                    }
                    job2.join()
                }

                biljkeList = data?.getParcelableArrayListExtra("biljkeList")?: mutableListOf()
                filteredBiljke = biljkeList
                currentMode = "Medicinski"
                selectMode.setSelection(0)
                // Refresh the UI
                updateAdapter(currentMode, biljkeList)
            }
        }
    }

    private fun filterBiljke(mode:String, selectedBiljka: Biljka): List<Biljka> {
            this.selectedBiljka = selectedBiljka
            return when (mode) {
                "Medicinski" -> {
                    filteredBiljke.filter { biljka ->
                        biljka.medicinskeKoristi.intersect(selectedBiljka.medicinskeKoristi.toSet())
                            .isNotEmpty()
                    }
                }

                "Kuharski" -> {
                    filteredBiljke.filter { biljka ->
                        biljka.profilOkusa == selectedBiljka.profilOkusa || biljka.jela.intersect(
                            selectedBiljka.jela.toSet()
                        ).isNotEmpty()
                    }
                }

                "Botanički" -> {
                    filteredBiljke.filter { biljka ->
                        biljka.porodica == selectedBiljka.porodica && biljka.klimatskiTipovi.intersect(
                            selectedBiljka.klimatskiTipovi.toSet()
                        )
                            .isNotEmpty() && biljka.zemljisniTipovi.intersect(selectedBiljka.zemljisniTipovi.toSet())
                            .isNotEmpty()
                    }
                }
                else -> biljkeList
            }
    }

    private fun updateAdapter(mode: String, biljke: List<Biljka>) {
        when(mode) {
            "Medicinski" -> {
                biljkeRV.adapter = MedicinskiModAdapter(biljke) {selectedBiljka ->
                    filteredBiljke = filterBiljke(mode, selectedBiljka)
                    updateAdapter(mode, filteredBiljke)
                }
            }
            "Kuharski" -> {
                biljkeRV.adapter = KuharskiModAdapter(biljke) {selectedBiljka ->
                    filteredBiljke = filterBiljke(mode, selectedBiljka)
                    updateAdapter(mode, filteredBiljke)
                }
            }
            "Botanički" -> {
                biljkeRV.adapter = BotanickiModAdapter(biljke) {selectedBiljka ->
                    if(!pretrazenoPoBoji) {
                        filteredBiljke = filterBiljke(mode, selectedBiljka)
                        updateAdapter(mode, filteredBiljke)
                    }
                }
            }
        }
    }
}