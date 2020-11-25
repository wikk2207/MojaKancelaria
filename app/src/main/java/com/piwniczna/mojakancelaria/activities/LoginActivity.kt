package com.piwniczna.mojakancelaria.activities


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.common.hash.Hashing
import com.piwniczna.mojakancelaria.DB.DataService

import com.piwniczna.mojakancelaria.Models.PasswordEntity
import com.piwniczna.mojakancelaria.R
import java.lang.NullPointerException
import java.nio.charset.StandardCharsets


class LoginActivity : AppCompatActivity() {
    lateinit var passwordEditText : EditText
    lateinit var loginButton: Button
    lateinit var service : DataService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        passwordEditText = findViewById(R.id.passwordCodeEditText)
        loginButton = findViewById(R.id.loginButton)

        service = DataService(this)

    }

    fun handleLogin(view: View) {

        val pin = passwordEditText.text
        val pinHash = Hashing.sha256()
                .hashString(pin, StandardCharsets.UTF_8)
                .toString()

        AsyncTask.execute {

            try {
                val dbHash = service.getPasswordHash()
                if(dbHash.equals(null)){
                    throw NullPointerException()
                }
                runOnUiThread {
                    if (!pinHash.equals(dbHash.hash)) {
                        val text = R.string.login_failed_toast
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(applicationContext, text, duration)
                        toast.show()

                        passwordEditText.setText("")

                    }
                    else {
                        val text = R.string.login_successful_toast
                        val duration = Toast.LENGTH_LONG
                        val toast = Toast.makeText(applicationContext, text, duration)
                        toast.show()

                        openMainActivity()

                    }
                }
            }
            catch (e: NullPointerException){
                runOnUiThread {
                    showPasswordDialog()
                }
            }

        }
    }

    fun openMainActivity() {
        val myIntent = Intent(this, MainActivity::class.java)
        startActivityForResult(myIntent, 123)
    }

    fun showPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Podaj nowy PIN")
        val dialogLayout = inflater.inflate(R.layout.password_dialog, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Zatwiedź") { _, _ ->  setPin(editText.text.toString())}
        builder.show()

    }

    fun setPin(newPin:String) {
        AsyncTask.execute {
            val pinHash = Hashing.sha256()
                    .hashString(newPin, StandardCharsets.UTF_8)
                    .toString()
            service.addNewPassword(PasswordEntity(pinHash))
        }
    }


}