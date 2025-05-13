package com.example.myapplication.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Quiz
import java.text.SimpleDateFormat
import java.util.Locale

class QuizAdapter(
    private val items: List<Quiz>
): RecyclerView.Adapter<QuizAdapter.QuizViewHolder>()  {
    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtQuizScore: TextView = itemView.findViewById(R.id.textQuizScore)
        val txtQuizTime: TextView = itemView.findViewById(R.id.textQuizTime)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_item_quiz_history, parent, false)
        return QuizViewHolder(view)
    }
    val inputFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())
    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val item = items[position]
        val date = inputFormat.parse(item.createdAt)
        val displayDate = outputFormat.format(date)
        holder.txtQuizScore.text ="Score ${item.countCorrect}/${item.totalQuestion}"
        holder.txtQuizTime.text ="Time taken: ${item.timeTaken}"

    }

    override fun getItemCount(): Int = items.size

}