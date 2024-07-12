package com.maths.loginapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.maths.loginapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
private val binding:ActivityMainBinding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
}
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        setContentView(binding.root)



       binding.logout.setOnClickListener {
           val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
               .requestEmail()
               .build()
                GoogleSignIn.getClient(this , gso).signOut()
              startActivity(Intent(this,Log_inActivity::class.java))
           finish()
       }

    }
}