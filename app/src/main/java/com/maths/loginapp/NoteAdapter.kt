package com.maths.loginapp

import android.icu.text.CaseMap.Title
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.maths.loginapp.databinding.NoteitemsBinding

class NoteAdapter(private val notes: List<Noteitem>,private val itemClickListener : OnItemClickListener)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

        interface OnItemClickListener{
            fun onDeleteClick(noteId:String)
            fun onUpdateClick(noteId:String ,title: String , description:String)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteitemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.binding.delete.setOnClickListener{
            itemClickListener.onDeleteClick(note.noteId)
        }
        holder.binding.update.setOnClickListener{
            itemClickListener.onUpdateClick(note.noteId ,note.title,note.description)
        }
    }

    override fun getItemCount(): Int {
      return notes.size
    }

    class NoteViewHolder(val binding: NoteitemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Noteitem) {
            binding.rtitle.text = note.title
            binding.rdescription.text = note.description
        }
    }
}
