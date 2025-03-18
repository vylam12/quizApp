package com.example.myapplication.ui.translate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Definition

class DefinitionAdapter (private val definitions: List<Definition>) : RecyclerView.Adapter<DefinitionAdapter.DefinitionViewHolder>(){
    class DefinitionViewHolder(view: View):RecyclerView.ViewHolder(view){
        val definitionText: TextView = view.findViewById(R.id.definitionText)
        val exampleText : TextView = view.findViewById(R.id.exampleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item_definition, parent, false)
        return DefinitionViewHolder(view)
    }

    override fun onBindViewHolder(holder:DefinitionViewHolder, position: Int) {
        val definition = definitions[position]

        holder.definitionText.text = "- " + definition.definition

        if (!definition.example.isNullOrBlank() && definition.example != "Không có") {
            holder.exampleText.visibility = View.VISIBLE
            holder.exampleText.text = "Example: ${definition.example}"
        } else {
            holder.exampleText.visibility = View.GONE
        }
    }
    override fun getItemCount() = definitions.size
}