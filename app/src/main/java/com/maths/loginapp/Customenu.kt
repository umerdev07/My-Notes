package com.maths.loginapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.PopupMenu
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

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

    private fun showLogoutConfirmationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                performLogout(context)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
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
