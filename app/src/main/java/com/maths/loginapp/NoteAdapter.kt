package com.maths.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maths.loginapp.databinding.NoteitemsBinding

class NoteAdapter(private val notes: List<Noteitem>, private val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface OnItemClickListener {
        fun onDeleteClick(noteId: String)
        fun onUpdateClick(noteId: String, title: String, description: String , calender:String)
        fun onShareClick(noteId: String, title: String, description: String ,calender: String)

    }

    private var filteredNotes: List<Noteitem> = notes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteitemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = filteredNotes[position] // Use filteredNotes instead of notes
        holder.bind(note)
        holder.binding.delete.setOnClickListener {
            itemClickListener.onDeleteClick(note.noteId)
        }
        holder.binding.update.setOnClickListener {
            itemClickListener.onUpdateClick(note.noteId, note.title, note.description,note.calender )
        }
        holder.binding.share.setOnClickListener {
            itemClickListener.onShareClick(note.noteId, note.title, note.description, note.calender)
        }

    }
    override fun getItemCount(): Int {
        return filteredNotes.size
    }

    class NoteViewHolder(val binding: NoteitemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Noteitem) {
            binding.rtitle.text = note.title
            binding.rdescription.text = note.description
            binding.calenderDateTimeText.text = note.calender
        }
    }

    fun filterNotes(query: String) {
        filteredNotes = if (query.isEmpty()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) || it.description.contains(
                    query,
                    ignoreCase = true
                )
            }
        }
            notifyDataSetChanged()
        }
    }

