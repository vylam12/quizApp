package com.example.myapplication.ui.forgotPassword

import android.text.*
import android.view.*
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CheckEmailAdapter(private val otpLength: Int):
    RecyclerView.Adapter<CheckEmailAdapter.ForgotPasswordViewHolder>(){
    private val otpValues = Array(otpLength){""}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForgotPasswordViewHolder {
       val view = LayoutInflater.from(parent.context)
           .inflate(R.layout.viewholder_input,parent,false)
        return  ForgotPasswordViewHolder(view)
    }
    override fun onBindViewHolder(holder: ForgotPasswordViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int= otpLength

    fun getOtpCode():String{
        return otpValues.joinToString("")
    }

    inner class ForgotPasswordViewHolder(view:View): RecyclerView.ViewHolder(view){
        private val otpEditText : EditText = view.findViewById(R.id.textOtp)
//        private val otpTextInputLayout: TextInputLayout = view.findViewById(R.id.OtpTextInputLayout)
        fun bind(position: Int){
            otpEditText.setText(otpValues[position])

            otpEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length ==1 &&position< otpLength -1){
                        itemView.rootView.findViewById<RecyclerView>(R.id.recyclerOtp)
                            .findViewHolderForAdapterPosition(position+1)
                            ?.itemView?.findViewById<EditText>(R.id.textOtp)
                            ?.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    otpValues[position] = s.toString()
                }

            })

        }
    }
}