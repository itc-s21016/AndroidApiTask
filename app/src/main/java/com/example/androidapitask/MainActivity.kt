package com.example.androidapitask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import com.example.androidapitask.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // ホットペッパーAPIのURL
        val apiUrl = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=318a1003a75ce344&large_area=Z011&budget+codeB008&results_available&format=json"

        // APIキー
        val apiKey = "318a1003a75ce344"

        // 現在地の緯度経度
        val latitude = 35.681236
        val longitude = 139.767125

        // キーワード検索する場合
        val keyword = "ラーメン"

        // リクエストURLの作成
        val url = "$apiUrl&key=$apiKey&lat=$latitude&lng=$longitude&range=3&keyword=$keyword"

        // リクエストの送信
        val request = Request.Builder().url(url).build()
//        val client = OkHttpClient()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()

                // JSONをパースして必要な情報を取得する
                val restaurants = parseJson(json)

                // UIスレッドで表示するために、runOnUiThreadを使用する
                runOnUiThread {
                    // 検索結果をリストビューに表示する処理を実装する
                }
            }
        })
    }
    // JSONをパースして必要な情報を取得する
    fun parseJson(json: String?): List<Restaurant> {
        val restaurants = mutableListOf<Restaurant>()
        val jsonObject = JSONObject(json)
        val results = jsonObject.getJSONObject("results")
        val restaurantArray = results.getJSONArray("shop")

        for (i in 0 until restaurantArray.length()) {
            val restaurantObject = restaurantArray.getJSONObject(i)
            val name = restaurantObject.getString("name")
            val address = restaurantObject.getString("address")
            val photoUrl = restaurantObject.getString("photo")
            val latitude = restaurantObject.getDouble("lat")
            val longitude = restaurantObject.getDouble("lng")
            val restaurant = Restaurant(name, address, photoUrl, latitude, longitude)
            restaurants.add(restaurant)
        }

        return restaurants
        
    }



}