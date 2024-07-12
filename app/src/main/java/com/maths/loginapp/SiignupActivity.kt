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
                showAlertDialog("Some fields is Empty Please fill it!")
            }else if(pass != re_pass){
                showAlertDialog("Both Password must be Same")
            }else{
                auth.createUserWithEmailAndPassword(email , pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Register Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this , Log_inActivity::class.java))
                            finish()
                        }else{
                            showAlertDialog("Registration Failed : ${task.exception?.message}")
                        }
                    }
            }
        }
    }
    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)

            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }
}