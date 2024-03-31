package com.example.spirala1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicinskiModAdapter(
    private var biljke: List<Biljka>,

    private val onItemClicked: (biljka: Biljka) -> Unit

) : RecyclerView.Adapter<MedicinskiModAdapter.BiljkaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.medicinski_mod, parent, false)

        return BiljkaViewHolder(view)
    }
    override fun getItemCount(): Int = biljke.size
    override fun onBindViewHolder(holder: BiljkaViewHolder, position: Int) {
        holder.nazivBiljke.text = biljke[position].naziv;
        val idMatch: String = biljke[position].naziv

//Pronalazimo id drawable elementa na osnovu id-a postera, modificirano u odnosu na vježbu
        val context: Context = holder.slikaBiljke.context
        var id: Int = context.resources
            .getIdentifier(idMatch, "drawable", context.packageName)
        if (id==0) id=context.resources
            .getIdentifier("default_img", "drawable", context.packageName)


        holder.slikaBiljke.setImageResource(id)
        holder.upozorenjeBiljka.text = biljke[position].medicinskoUpozorenje

        val medicinskeKoristi = biljke[position].medicinskeKoristi
        if (medicinskeKoristi.isNotEmpty()) {
            holder.korist1Biljka.text = medicinskeKoristi[0].opis
        }
        if (medicinskeKoristi.size > 1) {
            holder.korist2Biljka.text = medicinskeKoristi[1].opis
        }
        if (medicinskeKoristi.size > 2) {
            holder.korist3Biljka.text = medicinskeKoristi[2].opis
        }

        holder.itemView.setOnClickListener{ onItemClicked(biljke[position]) }

    }
    fun updateBiljke(biljke: List<Biljka>) {
        this.biljke = biljke
        notifyDataSetChanged()
    }
    inner class BiljkaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slikaBiljke: ImageView = itemView.findViewById(R.id.slikaItem)
        val nazivBiljke: TextView = itemView.findViewById(R.id.nazivItem)

        val upozorenjeBiljka: TextView = itemView.findViewById(R.id.upozorenjeItem)
        val korist1Biljka: TextView = itemView.findViewById(R.id.korist1Item)
        val korist2Biljka: TextView = itemView.findViewById(R.id.korist2Item)
        val korist3Biljka: TextView = itemView.findViewById(R.id.korist3Item)

    }
}