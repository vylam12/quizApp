package com.example.myapplication.ui.translate

import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Phonetic
import java.io.IOException

class PhoneticAdapter (private val phonetics: List<Phonetic>) :
    RecyclerView.Adapter<PhoneticAdapter.PhoneticViewHolder>(){
    class PhoneticViewHolder(view: View):RecyclerView.ViewHolder(view){
        val phoneticText: TextView = view.findViewById(R.id.phoneticText)
        val playAudio: ImageView = view.findViewById(R.id.playAudio)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):PhoneticViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item_phonetic, parent, false)
        return  PhoneticViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneticViewHolder, position: Int) {
        val phonetic = phonetics[position]
        if (phonetic.text.isNotEmpty() &&  phonetic.text != "Không có" && phonetic.audio.isNotEmpty() && phonetic.audio != "Không có") {
            holder.itemView.visibility = View.VISIBLE
            holder.phoneticText.text = "${phonetic.text} (${phonetic.type})"
            holder.playAudio.visibility = View.VISIBLE
            holder.playAudio.setOnClickListener {
                playAudio(phonetic.audio)
            }
        } else {
            holder.itemView.visibility = View.GONE
            holder.phoneticText.visibility = View.GONE
            holder.playAudio.visibility = View.GONE
        }

    }

    override fun getItemCount() = phonetics.size
    private fun playAudio(audioUrl: String) {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { it.start() }
            mediaPlayer.setOnCompletionListener { it.release() }
        } catch (e: IOException) {
            Log.e("PhoneticAdapter", "Lỗi phát audio: ${e.message}")
        }
    }
}