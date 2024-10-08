package com.example.biljke

import android.content.Context
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

class KuharskiModAdapter(
    private var biljke: List<Biljka>,
    private val onItemClicked: (biljka: Biljka) -> Unit

) : RecyclerView.Adapter<KuharskiModAdapter.BiljkaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder
    {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.kuharski_mod, parent, false)

        return BiljkaViewHolder(view)
    }
    override fun getItemCount(): Int = biljke.size
    override fun onBindViewHolder(holder: BiljkaViewHolder, position: Int) {
        holder.nazivBiljke.text = biljke[position].naziv;

        val context: Context = holder.slikaBiljke.context
        //korutina
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val dao = TrefleDAO()
            dao.setContext(context)
            val db = BiljkaDatabase.getInstance(ContextProvider.getContext())
            try{
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


        holder.okusBiljka.text = biljke[position].profilOkusa.opis


        val jelaTemp = biljke[position].jela
        holder.jelo1Biljka.text = ""
        holder.jelo2Biljka.text = ""
        holder.jelo3Biljka.text = ""

        if (jelaTemp.size > 0) {
            holder.jelo1Biljka.text = jelaTemp[0]
        }
        if (jelaTemp.size > 1) {
            holder.jelo2Biljka.text = jelaTemp[1]
        }
        if (jelaTemp.size > 2) {
            holder.jelo3Biljka.text = jelaTemp[2]
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
        val okusBiljka: TextView = itemView.findViewById(R.id.profilOkusaItem)
        val jelo1Biljka: TextView = itemView.findViewById(R.id.jelo1Item)
        val jelo2Biljka: TextView = itemView.findViewById(R.id.jelo2Item)
        val jelo3Biljka: TextView = itemView.findViewById(R.id.jelo3Item)
    }
}