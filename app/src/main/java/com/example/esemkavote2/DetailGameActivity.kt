package com.example.esemkavote2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DetailGameActivity : AppCompatActivity() {
    private lateinit var gameImage: ImageView
    private lateinit var titleText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var authorText: TextView
    private lateinit var btnBack: Button
    private lateinit var scoreText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_game)

        gameImage = findViewById(R.id.detailImage)
        titleText = findViewById(R.id.detailTitle)
        descriptionText = findViewById(R.id.detailDescription)
        authorText = findViewById(R.id.detailAuthor)
        btnBack = findViewById(R.id.btnBack)
        scoreText = findViewById(R.id.detailScore)


        val gameSlug = intent.getStringExtra("GAME_SLUG")
        if (gameSlug != null) {
            fetchGameDetails(gameSlug)
        } else {
            Toast.makeText(this, "Error: No game selected", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnBack.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun fetchGameDetails(slug: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPref = getSharedPreferences("APP_PREF", MODE_PRIVATE)
                val token = sharedPref.getString("TOKEN", null) ?: return@launch

                val url = URL("https://labapi.smkn2kra.sch.id/api/v1/game/$slug")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json")
                val responseCode = conn.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)

                    // Extract details
                    val title = json.getString("title")
                    val description = json.getString("description")
                    val author = json.getString("author")
                    val score = json.getString("scoreCount")
                    val imageUrl = "https://labapi.smkn2kra.sch.id/" + json.getString("thumbnail").replace("gs/", "games/")

                    withContext(Dispatchers.Main) {
                        titleText.text = title
                        descriptionText.text = description
                        authorText.text = "Author: $author"
                        scoreText.text = "Score: $score"


                        Glide.with(this@DetailGameActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.error)
                            .into(gameImage)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetailGameActivity, "Failed to load game details", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailGameActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}