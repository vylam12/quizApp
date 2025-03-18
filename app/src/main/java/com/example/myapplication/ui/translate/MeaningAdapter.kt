package com.example.myapplication.ui.translate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Meaning

class MeaningAdapter(private val meanings: List<Meaning>) : RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder>(){
    class MeaningViewHolder(view: View):RecyclerView.ViewHolder(view){
        val textType: TextView = view.findViewById(R.id.typeText)
        val rvDefinitions : RecyclerView = view.findViewById(R.id.rvDefinitions)
        val tvSynonyms: TextView = view.findViewById(R.id.tvSynonyms)
        val tvAntonyms: TextView = view.findViewById(R.id.tvAntonyms)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MeaningViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item_meaning, parent, false)
        return  MeaningViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeaningViewHolder, position: Int) {
        val meaning = meanings[position]
        holder.textType.text = meaning.type

        holder.rvDefinitions.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvDefinitions.adapter = DefinitionAdapter(meaning.definitions)
        if (!meaning.synonyms.isNullOrEmpty() && meaning.synonyms.none { it == "Không có" }) {
            holder.tvSynonyms.visibility = View.VISIBLE
            holder.tvSynonyms.text = "Synonyms: " + meaning.synonyms.joinToString(", ")
        } else {
            holder.tvSynonyms.visibility = View.GONE
        }

        if (!meaning.antonyms.isNullOrEmpty() && meaning.antonyms.none { it == "Không có" }) {
            holder.tvAntonyms.visibility = View.VISIBLE
            holder.tvAntonyms.text = "Antonyms: " + meaning.antonyms.joinToString(", ")
        } else {
            holder.tvAntonyms.visibility = View.GONE
        }

    }

    override fun  getItemCount() = meanings.size

}