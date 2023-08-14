package fr.isen.debray.arthurdroidburger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser


data class PastOrder(
    val id_sender: String,
    val id_receiver: String,
    val sender: String,
    val receiver: String,
    val code: String,
    val type_msg: String,
    val message: String,
    val create_date: String
) {

    val burger: String
        get() = extractValueFromMessage("burger")

    val delivery_time: String
        get() = extractValueFromMessage("delivery_time")

    private fun extractValueFromMessage(key: String): String {
        val jsonParser = JsonParser()
        val jsonMessage = jsonParser.parse(message) as JsonObject
        return jsonMessage.get(key)?.asString ?: ""
    }
}

data class PastOrdersResponse(
    val data: List<PastOrder>?,
    val code: Int
)


class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchPastOrders()
    }

    private fun fetchPastOrders() {

        Log.d("DebugTag", "Fetching past orders")
        val client = OkHttpClient()

        val requestBody = RequestBody.create("application/json".toMediaType(), """
            {
                "id_shop": "1",
                "id_user": 660
            }
        """.trimIndent())

        val request = Request.Builder()
            .url("http://test.api.catering.bluecodegames.com/listorders")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("DebugTag", "Response body: $responseBody") // Debug log

                    val gson = Gson()
                    val pastOrdersResponse = gson.fromJson(responseBody, PastOrdersResponse::class.java)

                    runOnUiThread {
                        val adapter = PastOrdersAdapter(pastOrdersResponse.data ?: emptyList())
                        recyclerView.adapter = adapter
                    }
                } else {
                    // Handle error
                    Log.d("DebugTag", "Response failed: ${response.code}")

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                Log.e("DebugTag", "Network failure: ${e.message}")

            }
        })
    }


}
