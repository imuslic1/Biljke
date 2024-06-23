package com.example.biljke

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
        holder.nazivBiljke.text = biljke[position].naziv
        val idMatch: String = biljke[position].naziv

        val context: Context = holder.slikaBiljke.context
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        val dao = TrefleDAO()
        dao.setContext(context)
        val db = BiljkaDatabase.getInstance(ContextProvider.getContext())

        scope.launch {
            try{
                dao.setContext(context)

                Glide.with(context)
                    .load(db.biljkaDao().getImageFromDB(biljke[position].id!!))
                    .into(holder.slikaBiljke)
            }
            catch(e : Exception){
                Glide.with(context)
                    .load(dao.defaultBitmap)
                    .into(holder.slikaBiljke)
            }
        }





        holder.upozorenjeBiljka.text = biljke[position].medicinskoUpozorenje

        val medicinskeKoristi = biljke[position].medicinskeKoristi

        holder.korist1Biljka.text = ""
        holder.korist2Biljka.text = ""
        holder.korist3Biljka.text = ""

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