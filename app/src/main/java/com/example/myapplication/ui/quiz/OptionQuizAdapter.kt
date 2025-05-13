package com.example.myapplication.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class OptionQuizAdapter(
    private val options: List<String>,
    private val onClick:(Int,String, Boolean) -> Unit
    ) :
    RecyclerView.Adapter<OptionQuizAdapter.OptionViewHolder>() {
        private var selectedAnswerIndex : Int? = null
        private var isCorrectAnswer: Boolean? = null
        private var correctAnswerIndex: Int? = null

    fun setCorrectAnswerIndex(index:Int){
        correctAnswerIndex = index
        notifyDataSetChanged()
    }

    inner class OptionViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val textOption : TextView = itemView.findViewById(R.id.textOption)
        val LinearLayout: LinearLayout = itemView.findViewById(R.id.linear1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
       val view = LayoutInflater.from(parent.context)
           .inflate(R.layout.viewholder_answer, parent, false)
        return  OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
      val option = options[position]
        holder.textOption.text = option

        when{
            selectedAnswerIndex == position && isCorrectAnswer == true->{
                holder.LinearLayout.setBackgroundResource(R.drawable.true_option_bg)
                holder.textOption.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.md_theme_onSecondaryContainer))

            }
            selectedAnswerIndex == position && isCorrectAnswer == false->{
                holder.LinearLayout.setBackgroundResource(R.drawable.false_option_bg)
                holder.textOption.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.md_theme_onErrorContainer))
            }
            correctAnswerIndex == position -> {
                holder.LinearLayout.setBackgroundResource(R.drawable.true_option_bg)
                holder.textOption.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary1_2))
            }
            else-> {
                holder.LinearLayout.setBackgroundResource(R.drawable.option_background)
            }
        }
        holder.itemView.setOnClickListener  {
            if (selectedAnswerIndex == null) {
                selectedAnswerIndex = holder.adapterPosition
                onClick(holder.adapterPosition, option, true)
            }
        }
    }
    override fun getItemCount(): Int= options.size

    fun setAnswerResult ( answerIndex: Int, isCorrect: Boolean){
        selectedAnswerIndex = answerIndex
        isCorrectAnswer = isCorrect
        notifyDataSetChanged()
    }
}