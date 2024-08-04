package com.maths.loginapp

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.maths.loginapp.databinding.ActivitySiignupBinding

class SiignupActivity : AppCompatActivity() {
    private val binding : ActivitySiignupBinding by lazy{
        ActivitySiignupBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signin.setOnClickListener {
            startActivity(Intent(this , Log_inActivity::class.java))
            finish()
        }

        // GET TEXT TO FIELD
        binding.register.setOnClickListener {
            val email = binding.email.text.toString()
            val username = binding.username.text.toString()
            val pass = binding.password.text.toString()
            val re_pass = binding.rePassword.text.toString()

            // CHECK IF ANY FIELD IS BLANK

            if(email.isEmpty() || username.isEmpty() || pass.isEmpty() || re_pass.isEmpty()){
                showAlertDialog(getString(R.string.some_fields_is_empty_please_fill_it))
            }else if(pass != re_pass){
                showAlertDialog(getString(R.string.both_password_must_be_same))
            }else{
                auth.createUserWithEmailAndPassword(email , pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,
                                getString(R.string.register_successfully), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this , Log_inActivity::class.java))
                            finish()
                        }else{
                            showAlertDialog(
                                getString(
                                    R.string.registration_failed,
                                    task.exception?.message
                                ))
                        }
                    }
            }
        }
    }
    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)

            .setCancelable(true)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }
}