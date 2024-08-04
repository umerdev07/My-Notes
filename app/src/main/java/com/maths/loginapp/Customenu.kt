package com.maths.loginapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class CustomMenu {

    fun customMenu(context: Context, view: View) {
        val pop = PopupMenu(context, view)
        pop.inflate(R.menu.menues)

        pop.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    showLogoutConfirmationDialog(context)
                    true
                }
               R.id.multilanguage -> {
                   showLanguageSpinnerDialog(context)
                   true
               }

                else -> false
            }
        }

        try {
            val fieldMpopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMpopup.isAccessible = true
            val mPopup = fieldMpopup.get(pop)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pop.show()
        }
    }

    private fun showLanguageSpinnerDialog(context: Context) {
        val languages = arrayOf("Urdu","English", "Chinese", "Spanish", "Hindi", "Arabic", "Portuguese", "Bengali", "Russian", "Japanese", "Punjabi")
        val languageCodes = arrayOf("ur","en", "zh", "es", "hi", "ar", "pt", "bn", "ru", "ja", "pa")
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.choose_language))
            .setItems(languages) { dialog, which ->
                changeLanguage(context, languageCodes[which])
                Toast.makeText(context, "Selected: ${languages[which]}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun changeLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Restart the activity to apply the new language
        val intent = Intent(context, context::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }




    private fun showLogoutConfirmationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.do_you_want_to_logout))
            .setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                performLogout(context)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun performLogout(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        googleSignInClient.signOut().addOnCompleteListener {

            // Clear Firebase authentication token
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(context, Log_inActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

}
