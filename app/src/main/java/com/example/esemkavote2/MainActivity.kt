package com.example.esemkavote2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var etUsername : TextInputEditText
    private lateinit var etPassword : TextInputEditText
    private lateinit var btnLogin : Button

    private val loginURL = "https://labapi.smkn2kra.sch.id/api/v1/auth/signin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsername = findViewById(R.id.editText1)
        etPassword = findViewById(R.id.editText2)
        btnLogin = findViewById(R.id.loginButton)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()){
                loginUser(username,password)
            } else {
                Toast.makeText(this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(loginURL)
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
                val response = if (responseCode == HttpURLConnection.HTTP_OK){
                    val reader =  BufferedReader(InputStreamReader(conn.inputStream))
                    val responseStr = reader.readText()
                    reader.close()
                    responseStr
                } else {
                    null
                }

                withContext(Dispatchers.Main){
                    if (response != null){
                        val jsonResponse = JSONObject(response)
                        if (jsonResponse.has("token")) {
                            val token = jsonResponse.getString("token")
                            saveToken(token)
                            Toast.makeText(this@MainActivity,"Login berhasil!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity,"Login Gagal!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Login Gagal! Periksa Koneksi.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Error :${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("APP_PREF", MODE_PRIVATE)
        sharedPreferences.edit().putString("TOKEN", token).apply()
    }
}