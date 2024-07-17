package com.maths.loginapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.maths.loginapp.databinding.ActivityMainBinding
import com.maths.loginapp.databinding.UpdateDialogBinding


class MainActivity : AppCompatActivity(), NoteAdapter.OnItemClickListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        recyclerView = binding.recyclerView // Ensure you have a RecyclerView with this ID in your layout
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.menue.setOnClickListener {
            CustomMenu().customMenu(this, it)
        }

        binding.createNotes.setOnClickListener {
            createnote()
        }

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReferences = databaseReference.child("users").child(user.uid).child("notes")
            noteReferences.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val noteList = mutableListOf<Noteitem>()
                    for (noteSnapshot in snapshot.children) {
                        val note = noteSnapshot.getValue(Noteitem::class.java)
                        note?.let {
                            noteList.add(it)
                        }
                    }
                    val adapter = NoteAdapter(noteList, this@MainActivity)
                    recyclerView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Failed to load notes.", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }




    override fun onDeleteClick(noteId: String) {
        val currentuser = auth.currentUser
        currentuser?.let { user ->
            val noteReference: DatabaseReference = databaseReference
                .child("users")
                .child(user.uid)
                .child("notes")

            // Check if noteId exists before attempting deletion
            noteReference.child(noteId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.apply {
                            setTitle("Confirm Deletion")
                            setMessage("Do you want to delete this note?")
                            setPositiveButton("Delete") { dialog, which ->
                                // Perform deletion
                                noteReference.child(noteId).removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Note Deleted Successfully", Toast.LENGTH_SHORT).show()

                                        // Check if there are any remaining notes
                                        noteReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (!snapshot.exists()) {
                                                    binding.nonotes.visibility = View.VISIBLE
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                // Handle possible errors.
                                            }
                                        })
                                    } else {
                                        Toast.makeText(context, "Failed to delete note.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            setNegativeButton("No") { dialog, which ->
                                dialog.dismiss()
                            }
                        }
                        builder.create().show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Note does not exist.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch note details.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


    override fun onUpdateClick(noteId: String, currentTitle: String, currentDescription: String) {

        val dialogBinding = UpdateDialogBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .setTitle("Update Notes")
            .setPositiveButton("Update") { dialog, which ->
                val newtitles = dialogBinding.newtitle.text.toString()
                val newdescriptions = dialogBinding.newdescription.text.toString()
                updatenotedatabase(noteId, newtitles, newdescriptions)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
        dialogBinding.newtitle.setText(currentTitle)
        dialogBinding.newdescription.setText(currentDescription)


        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_radius)

        dialog.show()
    }

    private fun updatenotedatabase(noteId: String, newtitles: String, newdescriptions: String) {
        val currentuser = auth.currentUser
        currentuser?.let { user ->
            val noteReference = databaseReference.child("users").child(user.uid).child("notes")
            val updatenote = Noteitem(newtitles, newdescriptions, noteId)
            noteReference.child(noteId).setValue(updatenote)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Note Update Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Note Update Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun createnote() {
        val dialogBinding = UpdateDialogBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .setTitle("Create Notes")
            .setPositiveButton("Create") { dialog, which ->
                val newtitles = dialogBinding.newtitle.text.toString()
                val newdescriptions = dialogBinding.newdescription.text.toString()
                addnotedatabase(newtitles, newdescriptions)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()


        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_radius)

        dialog.show()
    }

    private fun addnotedatabase(addtitles: String, adddescriptions: String) {
               if(addtitles.isEmpty() || adddescriptions.isEmpty()){
                   Toast.makeText(this, "Fill all Fields", Toast.LENGTH_SHORT).show()
               }else{
                   val currentuser = auth.currentUser
                   currentuser?.let { user ->
                       val noteReference = databaseReference.child("users").child(user.uid).child("notes")
                       val noteId = noteReference.push().key ?: return
                       val newNote = Noteitem(addtitles, adddescriptions, noteId)
                       noteReference.child(noteId).setValue(newNote)
                           .addOnCompleteListener { task ->
                               if (task.isSuccessful) {
                                   Toast.makeText(this, "Note Added Successfully", Toast.LENGTH_SHORT).show()
                                   binding.nonotes.visibility = View.INVISIBLE // Use nonotes directly
                               } else {
                                   Toast.makeText(this, "Note Addition Failed", Toast.LENGTH_SHORT).show()
                                   binding.nonotes.visibility = View.VISIBLE
                               }
                           }
                   }

               }
    }
}
