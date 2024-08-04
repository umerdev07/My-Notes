package com.maths.loginapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.maths.loginapp.databinding.ActivityLogInBinding


class Log_inActivity : AppCompatActivity() {
private val binding : ActivityLogInBinding by lazy {
    ActivityLogInBinding.inflate(layoutInflater)
}
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googlebtn.setOnClickListener{

            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
        binding.signUp.setOnClickListener {
            startActivity(Intent(this  , SiignupActivity::class.java))
        }

        binding.login.setOnClickListener {
            val useremail = binding.username.text.toString()
            val pass = binding.password.text.toString()

            if(useremail.isEmpty() || pass.isEmpty()){
                showAlertDialog(getString(R.string.please_fill_the_details))
            }else{
                auth.signInWithEmailAndPassword(useremail , pass)
                    .addOnCompleteListener(this) {task ->
                        if(task.isSuccessful){
                            Toast.makeText(this,
                                getString(R.string.login_successfully), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this , MainActivity::class.java))
                            finish()
                        }else{
                            showAlertDialog(
                                getString(
                                    R.string.failed_to_login_because_you_are_not_registered,
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
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
            result ->

        if(result.resultCode== RESULT_OK)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount?=task.result
                val credentials = GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credentials).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, getString(R.string.login_successfully), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this , MainActivity::class.java))
                    }else{
                        Toast.makeText(this,
                            getString(R.string.login_failed, task.exception?.message), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show()
        }
    }
}