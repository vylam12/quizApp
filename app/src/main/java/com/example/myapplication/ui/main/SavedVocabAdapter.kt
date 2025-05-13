package com.example.myapplication.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.SavedVocab
import com.example.myapplication.repositori.TranslateResponse.Vocab
import com.example.myapplication.viewmodel.TranslateViewModel

class SavedVocabAdapter(
    private var items: List<Vocab>,
    private val onUnsaveClick: (Vocab) -> Unit,
    private val onItemClick: ((Vocab) -> Unit)? = null,
    private val onSaveClick: ((Vocab) -> Unit)
) : RecyclerView.Adapter<SavedVocabAdapter.VocabViewHolder>() {
    inner class VocabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtWord: TextView = itemView.findViewById(R.id.txtWord)
        val txtMeaning: TextView = itemView.findViewById(R.id.txtMeaning)
        val saveButton: ImageButton = itemView.findViewById(R.id.saveVocab)
        val unsaveButton: ImageButton = itemView.findViewById(R.id.unsaveVocab)
    }
    fun updateList(newList: List<Vocab>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_item_save_vocab, parent, false)
        return VocabViewHolder(view)
    }

    override fun onBindViewHolder(holder: VocabViewHolder, position: Int) {
        val item = items[position]
        holder.txtWord.text = item.word
        holder.txtMeaning.text = item.meaning
        if (item.isSaved) {
            holder.unsaveButton.visibility = View.VISIBLE
            holder.saveButton.visibility = View.GONE
        } else {
            holder.unsaveButton.visibility = View.GONE
            holder.saveButton.visibility = View.VISIBLE
        }

        holder.unsaveButton.setOnClickListener {
            onUnsaveClick(item)
        }

        holder.saveButton.setOnClickListener {
            onSaveClick(item)
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }


    }

    override fun getItemCount(): Int = items.size


}
