package com.example.esemkavote2

import GameAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HomeActivity : AppCompatActivity() {

    private lateinit var listView : ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val dataList = mutableListOf<String>()

    private val API_URL = "https://labapi.smkn2kra.sch.id/api/v1/games"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        fetchData()
    }

    data class Game (
        val title: String,
        val description: String,
        val author: String,
        val score: String,
        val slug: String,
        val thumbnail: String
    )

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPref = getSharedPreferences("APP_PREF", MODE_PRIVATE)
                val token = sharedPref.getString("TOKEN", null) ?: return@launch

                val url = URL(API_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doInput = true

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val responseStr = reader.readText()
                    reader.close()

                    val jsonArray = JSONObject(responseStr).getJSONArray("content") // Adjust as needed

                    val games = mutableListOf<Game>()
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val game = Game(
                            title = item.getString("title"),
                            description = item.getString("description"),
                            author = item.getString("author"),
                            score = item.getString("scoreCount"),
                            slug = item.getString("slug"),
                            thumbnail = item.getString("thumbnail")
                        )
                        games.add(game)
                    }

                    withContext(Dispatchers.Main) {
                        updateListView(games)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Failed to fetch data!")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    private fun updateListView(gameList: List<Game>) {
        val adapter = GameAdapter(this, gameList)
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter
    }

    private fun showToast(string: String){
        Toast.makeText(this@HomeActivity, string, Toast.LENGTH_SHORT).show()
    }
}