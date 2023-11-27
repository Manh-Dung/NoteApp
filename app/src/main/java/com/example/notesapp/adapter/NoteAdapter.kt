package com.example.notesapp.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.data.Notes
import java.io.FileNotFoundException
import java.io.InputStream

class NoteAdapter(
    private val onItemClick: (Notes) -> Unit,
    private val context: Context
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private var noteList = emptyList<Notes>()

    fun updateData(newItemList: List<Notes>) {
        noteList = newItemList
        notifyDataSetChanged()
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItem: TextView = itemView.findViewById(R.id.titleItem)
        val subTitleItem: TextView = itemView.findViewById(R.id.subTitleItem)
        val timeItem: TextView = itemView.findViewById(R.id.timeItem)
        val noteItem: LinearLayout = itemView.findViewById(R.id.noteItem)
        val imageItem: ImageView = itemView.findViewById(R.id.imageItem)
        val backgroundItem: LinearLayout = itemView.findViewById(R.id.backGroundItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        holder.titleItem.text = note.title
        holder.subTitleItem.text = note.subTitle
        holder.timeItem.text = note.dateTime
        if (note.image == "") {
            holder.imageItem.visibility = View.GONE
        } else {
            val uri = Uri.parse(note.image)
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    holder.imageItem.setImageURI(uri)
                } else {
                    holder.imageItem.visibility = View.GONE
                }
            } catch (e: FileNotFoundException) {
                holder.imageItem.visibility = View.GONE
            } finally {
                inputStream?.close()
            }
        }


        val colorDrawable = ColorDrawable(Color.parseColor(note.color))
        holder.backgroundItem.background = colorDrawable

        holder.noteItem.setOnClickListener {
            onItemClick(note)
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }
}