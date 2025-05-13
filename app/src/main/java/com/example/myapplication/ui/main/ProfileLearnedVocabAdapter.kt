package com.example.myapplication.ui.main

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.LearnedWord

class ProfileLearnedVocabAdapter (
    private var vocabList: List<LearnedWord>
    ) : RecyclerView.Adapter<ProfileLearnedVocabAdapter.VocabViewHolder>() {

        inner class VocabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txtVocab: TextView = itemView.findViewById(R.id.textWord)
            val txtCorrectAnswers: TextView = itemView.findViewById(R.id.textCorrectAnswers)
            val txtLastReviewed: TextView = itemView.findViewById(R.id.textLastReviewed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_save_item, parent, false)
            return VocabViewHolder(view)
        }

        override fun onBindViewHolder(holder: VocabViewHolder, position: Int) {
            val item = vocabList[position]

            val formattedDate = item.lastReviewedAt.let {
                try {
                    val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
                    inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                    val date = inputFormat.parse(it)
                    val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    outputFormat.format(date!!)
                } catch (e: Exception) {
                    "Không xác định"
                }
            } ?: "Haven't studied yet"

            holder.txtVocab.text = item.word
            holder.txtCorrectAnswers.text ="Answered correctly: ${item.correctAnswers} time"
            holder.txtLastReviewed.text = "Last reviewed: ${formattedDate}"
        }

        override fun getItemCount(): Int = vocabList.size

        fun updateData(newList: List<LearnedWord>) {
            vocabList  = newList
            notifyDataSetChanged()
        }
    }
