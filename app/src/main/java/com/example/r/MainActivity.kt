    package com.example.r

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var biljkeRV: RecyclerView
    private lateinit var biljkeList: List<Biljka>
    private lateinit var selectMode: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        //enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectMode = findViewById(R.id.modSpinner)
        biljkeRV = findViewById(R.id.biljkeRV)
        biljkeList = fetchBiljke()

        val modes = arrayOf("medical", "cooking", "botanical")
        selectMode.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, modes)


        biljkeRV.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        selectMode.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedMode = parent.getItemAtPosition(position).toString()

                when (selectedMode) {
                    "medical" -> {
                        biljkeRV.adapter = MedicinskiModAdapter(biljkeList)
                    }
                    "cooking" -> {
                        biljkeRV.adapter = KuharskiModAdapter(biljkeList)
                    }
                    "botanical" -> {
                        biljkeRV.adapter = BotanickiModAdapter(biljkeList)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // default view je medicinski
                biljkeRV.adapter = MedicinskiModAdapter(biljkeList)
            }
        }
    }
}