package com.example.esemkavote2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext


class SignUpActivity : AppCompatActivity() {

    private lateinit var etUsername : TextInputEditText
    private lateinit var etPassword : TextInputEditText
    private lateinit var etCPassword : TextInputEditText
    private lateinit var registerButton : Button
    private lateinit var txtLogin : TextView


    private val regisURL = "https://labapi.smkn2kra.sch.id/api/v1/auth/signup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etUsername = findViewById(R.id.editText1)
        etPassword = findViewById(R.id.editText2)
        etCPassword = findViewById(R.id.editText3)
        registerButton = findViewById(R.id.registerButton)
        txtLogin = findViewById(R.id.txtLogin)
        txtLogin.movementMethod = LinkMovementMethod.getInstance()

        registerButton.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val cPassword = etCPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty() && cPassword.isNotEmpty()){
                if (password == cPassword){
                    registerUser(username, password)
                } else {
                    Toast.makeText(this, "Password is doesn't match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
            }
        }

        txtLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    private fun registerUser(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(regisURL);
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-type", "application/json")
                conn.doOutput = true
                conn.doInput = true

                val jsonParams = JSONObject()
                jsonParams.put("username", username)
                jsonParams.put("password", password)

                val outputStream: OutputStream = conn.outputStream
                outputStream.write(jsonParams.toString().toByteArray(Charsets.UTF_8))
                outputStream.close()

                val responseCode = conn.responseCode
                val responseMessage = conn.responseMessage

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        Toast.makeText(this@SignUpActivity, "Registrasi Berhasil! Silahkan login", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignUpActivity, "Registrasi Gagal: $responseMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

