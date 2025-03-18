package com.example.myapplication.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.main.MessageAdapter

class SwipeToDeleteCallback(
    private val adapter: MessageAdapter,
    private val context: Context,
    private val onDeleteConfirmed: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) { // Vuốt sang phải

    private val paint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        AlertDialog.Builder(context)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa cuộc trò chuyện này?")
            .setPositiveButton("Xóa") { _, _ ->
                onDeleteConfirmed(position) //
                adapter.removeItem(position)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                adapter.notifyItemChanged(position) // Khôi phục item nếu hủy
                dialog.dismiss()
            }
            .show()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        if (dX > 0) { // Vuốt sang phải
            c.drawRect(
                itemView.left.toFloat(), itemView.top.toFloat(),
                itemView.left + dX, itemView.bottom.toFloat(), paint
            )
            c.drawText("Xóa", itemView.left + 50f, itemView.top + 100f, paint)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
