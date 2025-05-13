package com.example.myapplication.ui.main

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.CompletedQuiz

class ProfileHistoryQuizAdapter(
    private var quizList: List<CompletedQuiz>
) : RecyclerView.Adapter<ProfileHistoryQuizAdapter.QuizHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_item_quiz_history, parent, false)
        return QuizHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizHistoryViewHolder, position: Int) {
        val quizHistory = quizList[position]
        holder.bind(quizHistory)
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    class QuizHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quizScore: TextView = itemView.findViewById(R.id.textQuizScore)
        private val quizTime: TextView = itemView.findViewById(R.id.textQuizTime)
        private val textQuizTitle: TextView = itemView.findViewById(R.id.textQuizTitle)

        fun bind(quiz: CompletedQuiz) {
            val formattedDate = quiz.createdAt.let {
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
            textQuizTitle.text = "Quiz ${formattedDate}"
            quizScore.text = "Score: ${quiz.countCorrect}/${quiz.totalQuestion}"
            quizTime.text =  "Time taken: ${quiz.timeTaken} s"
        }
    }
    fun updateData(newQuizList: List<CompletedQuiz>) {
        quizList = newQuizList
        notifyDataSetChanged()
    }
}
