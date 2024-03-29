package com.example.r

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BotanickiModAdapter(
    private var biljke: List<Biljka>,

    //TODO: implementirati funkcionalnost za selektovanje slicnih biljaka
    //private val onItemClicked: (biljka: Biljka) -> Unit

) : RecyclerView.Adapter<BotanickiModAdapter.BiljkaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder
    {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.botanicki_mod, parent, false)

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
        holder.porodicaBiljka.text = biljke[position].porodica
        holder.klimatskiTipBiljka.text = biljke[position].klimatskiTipovi[0].opis
        holder.zemljisniTipBiljka.text = biljke[position].zemljisniTipovi[0].naziv

        //TODO: implementirati funkcionalnost za selektovanje slicnih biljaka
        //holder.itemView.setOnClickListener{ onItemClicked(biljke[position]) }
    }
    fun updateBiljke(biljke: List<Biljka>) {
        this.biljke = biljke
        notifyDataSetChanged()
    }
    inner class BiljkaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slikaBiljke: ImageView = itemView.findViewById(R.id.slikaItem)
        val nazivBiljke: TextView = itemView.findViewById(R.id.nazivItem)

        val porodicaBiljka: TextView = itemView.findViewById(R.id.porodicaItem)
        val klimatskiTipBiljka: TextView = itemView.findViewById(R.id.klimatskiTipItem)
        val zemljisniTipBiljka: TextView = itemView.findViewById(R.id.zemljisniTipItem)

    }
}