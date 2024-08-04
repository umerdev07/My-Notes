package com.maths.loginapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.maths.loginapp.databinding.ActivityMainBinding
import com.maths.loginapp.databinding.UpdateDialogBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), NoteAdapter.OnItemClickListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter

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

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.menue.setOnClickListener {
            CustomMenu().customMenu(this, it)
        }

        binding.createNotes.setOnClickListener {
            createNote()
        }

        setupSearchView()

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReferences = databaseReference.child(getString(R.string.users)).child(user.uid).child(getString(R.string.notes))
            noteReferences.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val noteList = mutableListOf<Noteitem>()
                    for (noteSnapshot in snapshot.children) {
                        val note = noteSnapshot.getValue(Noteitem::class.java)
                        note?.let {
                            noteList.add(it)
                        }
                    }
                    noteAdapter = NoteAdapter(noteList, this@MainActivity)
                    recyclerView.adapter = noteAdapter
                    binding.nonotes.visibility = if (noteList.isEmpty()) View.VISIBLE else View.INVISIBLE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, getString(R.string.failed_to_load_notes), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDeleteClick(noteId: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference: DatabaseReference = databaseReference
                .child(getString(R.string.users))
                .child(user.uid)
                .child(getString(R.string.notes))

            noteReference.child(noteId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.apply {
                            setTitle(getString(R.string.confirm_deletion))
                            setMessage(getString(R.string.do_you_want_to_delete_this_note))
                            setPositiveButton(getString(R.string.delete)) { dialog, which ->
                                noteReference.child(noteId).removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, getString(R.string.note_deleted_successfully), Toast.LENGTH_SHORT).show()
                                        noteReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (!snapshot.exists()) {
                                                    binding.nonotes.visibility = View.VISIBLE
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {}
                                        })
                                    } else {
                                        Toast.makeText(context, getString(R.string.failed_to_delete_note), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            setNegativeButton(getString(R.string.no)) { dialog, which ->
                                dialog.dismiss()
                            }
                        }
                        builder.create().show()
                    } else {
                        Toast.makeText(this@MainActivity, getString(R.string.note_does_not_exist), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, getString(R.string.failed_to_fetch_note_details), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onUpdateClick(noteId: String, currentTitle: String, currentDescription: String, currentDateTime: String) {
        val dialogBinding = UpdateDialogBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .setTitle(getString(R.string.update_notes))
            .setPositiveButton(getString(R.string.update)) { dialog, which ->
                val newTitles = dialogBinding.newtitle.text.toString()
                val newDescriptions = dialogBinding.newdescription.text.toString()
                val newCalender = dialogBinding.calenderText.text.toString()
                updateNoteDatabase(noteId, newTitles, newDescriptions, newCalender)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .create()

        dialogBinding.newtitle.setText(currentTitle)
        dialogBinding.newdescription.setText(currentDescription)
        dialogBinding.calenderText.text = currentDateTime

        dialogBinding.calender.setOnClickListener {
            onCalenderClick { selectedDateTime ->
                dialogBinding.calenderText.text = selectedDateTime
            }
        }

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_radius)
        dialog.show()
    }

    @SuppressLint("StringFormatMatches")
    override fun onShareClick(noteId: String, title: String, description: String, calender: String) {
        val shareContent = getString(R.string.share_content, noteId, title, description ,calender)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareContent)
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
    }

    private fun updateNoteDatabase(noteId: String, newTitles: String, newDescriptions: String, newCalender: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference = databaseReference.child(getString(R.string.users)).child(user.uid).child(getString(R.string.notes))
            val updateNote = Noteitem(newTitles, newDescriptions, noteId, newCalender)
            noteReference.child(noteId).setValue(updateNote).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.note_update_successfully), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.note_update_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createNote() {
        val dialogBinding = UpdateDialogBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .setTitle(getString(R.string.create_notes))
            .setPositiveButton(getString(R.string.create)) { dialog, which ->
                val newTitles = dialogBinding.newtitle.text.toString()
                val newDescriptions = dialogBinding.newdescription.text.toString()
                val dateTime = dialogBinding.calenderText.text.toString()
                addNoteToDatabase(newTitles, newDescriptions, dateTime)


                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_radius)

        dialogBinding.calender.setOnClickListener {
            onCalenderClick { selectedDateTime ->
                dialogBinding.calenderText.text = selectedDateTime
            }
        }

        dialog.show()
    }

    private fun addNoteToDatabase(title: String, description: String, dateTime: String) {
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference = databaseReference.child(getString(R.string.users)).child(user.uid).child(getString(R.string.notes))
            val newNoteId = noteReference.push().key
            val noteItem = Noteitem(title, description, newNoteId ?: "", dateTime)

            noteReference.child(newNoteId ?: "").setValue(noteItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.note_added_successfully), Toast.LENGTH_SHORT).show()
                    binding.nonotes.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(this, getString(R.string.failed_to_load_notes), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onCalenderClick(onDateTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedDateTime = String.format("%02d/%02d/%d %02d:%02d", dayOfMonth, month + 1, year, hourOfDay, minute)
                        onDateTimeSelected(selectedDateTime)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                noteAdapter.filterNotes(newText ?: "")
                return true
            }
        })
    }
}
