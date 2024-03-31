package com.example.spirala1

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var biljkeRV: RecyclerView
    private lateinit var biljkeList: List<Biljka>
    private lateinit var selectMode: Spinner
    private lateinit var resetButton : Button

    //TODO: NE ZNAM MOZE LI OVAKO, RAZMISLI!
    private lateinit var filteredBiljke: List<Biljka>
    private var selectedBiljka : Biljka? = null
    private var currentMode: String = "medical"

    override fun onCreate(savedInstanceState: Bundle?) {
        //enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectMode = findViewById(R.id.modSpinner)
        biljkeRV = findViewById(R.id.biljkeRV)
        biljkeList = fetchBiljke()
        resetButton = findViewById(R.id.resetBtn)
        filteredBiljke = biljkeList

        val modes = arrayOf("medical", "cooking", "botanical")
        selectMode.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, modes)


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
                currentMode = parent.getItemAtPosition(position).toString()

                //TODO: MODIFY TO KEEP FILTERED PLANTS, DONT FILTER AFTER SELECTED PLANT HAS A VALUE
                val biljke = selectedBiljka?.let { filteredBiljke } ?: biljkeList
                updateAdapter(currentMode, biljke)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // default view je medicinski
                updateAdapter("medical", biljkeList)
            }
        }

        resetButton.setOnClickListener {
            selectedBiljka = null
            filteredBiljke = biljkeList
            updateAdapter(currentMode, biljkeList)
        }
    }

    private fun filterBiljke(mode:String, selectedBiljka: Biljka): List<Biljka> {
        this.selectedBiljka = selectedBiljka
        return when (mode) {
            "medical" -> {
                filteredBiljke.filter { biljka ->
                    biljka.medicinskeKoristi.intersect(selectedBiljka.medicinskeKoristi.toSet()).isNotEmpty()
                }
            }
            "cooking" -> {
                filteredBiljke.filter { biljka ->
                    biljka.profilOkusa == selectedBiljka.profilOkusa || biljka.jela.intersect(
                        selectedBiljka.jela.toSet()
                    ).isNotEmpty()
                }
            }
            "botanical" -> {
                filteredBiljke.filter { biljka ->
                    biljka.porodica == selectedBiljka.porodica && biljka.klimatskiTipovi.intersect(
                        selectedBiljka.klimatskiTipovi.toSet()
                    ).isNotEmpty() && biljka.zemljisniTipovi.intersect(selectedBiljka.zemljisniTipovi.toSet()).isNotEmpty()
                }
            }
            else -> biljkeList
        }
    }


    //TODO: SET filteredBiljke as a global, update as necessary
    private fun updateAdapter(mode: String, biljke: List<Biljka>) {
        when(mode) {
            "medical" -> {
                biljkeRV.adapter = MedicinskiModAdapter(biljke) {selectedBiljka ->
                    filteredBiljke = filterBiljke(mode, selectedBiljka)
                    updateAdapter(mode, filteredBiljke)
                }
            }
            "cooking" -> {
                biljkeRV.adapter = KuharskiModAdapter(biljke) {selectedBiljka ->
                    filteredBiljke = filterBiljke(mode, selectedBiljka)
                    updateAdapter(mode, filteredBiljke)
                }
            }
            "botanical" -> {
                biljkeRV.adapter = BotanickiModAdapter(biljke) {selectedBiljka ->
                    filteredBiljke = filterBiljke(mode, selectedBiljka)
                    updateAdapter(mode, filteredBiljke)
                }
            }
        }
    }




}