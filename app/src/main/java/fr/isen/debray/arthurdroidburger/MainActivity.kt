package fr.isen.debray.arthurdroidburger

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import java.util.Calendar
import android.widget.Button
import java.util.*
import java.text.SimpleDateFormat
import android.widget.TextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import okhttp3.*
import java.io.IOException
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType

class MainActivity : ComponentActivity() {


    private lateinit var tvSelectedTime: TextView
    private val selectedTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    lateinit var button: Button
    lateinit var NomEdit: EditText
    lateinit var PrenomEdit: EditText
    lateinit var AddresseEdit: EditText
    lateinit var NumeroEdit: EditText
    lateinit var spinner1: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        NomEdit = findViewById(R.id.NomEdit)
        PrenomEdit = findViewById(R.id.PrenomEdit)
        AddresseEdit = findViewById(R.id.AddresseEdit)
        NumeroEdit = findViewById(R.id.NumeroEdit)
        spinner1 = findViewById(R.id.spinner1)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)

        button.setOnClickListener {
            val firstname = NomEdit.text.toString()
            val lastname = PrenomEdit.text.toString()
            val address = AddresseEdit.text.toString()
            val phone = NumeroEdit.text.toString()
            val burger = spinner1.selectedItem.toString()
            val delivery_time = tvSelectedTime.text.toString().removePrefix("Heure sélectionnée :").trim()

            val champsManquants = mutableListOf<String>()

            if (firstname.isEmpty()) {
                champsManquants.add("Nom")
            }
            if (address.isEmpty()) {
                champsManquants.add("Adresse")
            }
            if (phone.isEmpty()) {
                champsManquants.add("Téléphone")
            }
            if (burger == "Selectionnez le Burger") {
                champsManquants.add("Burger")
            }
            if (delivery_time == "Heure selectionnée :") {
                champsManquants.add("Heure")
            }

            if (champsManquants.isNotEmpty()) {
                val messageErreur = "Les champs suivants sont manquants : ${champsManquants.joinToString(", ")}"
                Toast.makeText(this, messageErreur, Toast.LENGTH_SHORT).show()
            } else {

                val jsonCommand = """
            {
                "id_shop": "1",
                "id_user": 660,
                "msg": "{\"firstname\":\"$firstname\",\"lastname\":\"$lastname\",\"address\":\"$address\",\"phone\":\"$phone\",\"burger\":\"$burger\",\"delivery_time\":\"$delivery_time\"}"
            }
        """.trimIndent()

                val client = OkHttpClient()

                val requestBody = RequestBody.create("application/json".toMediaType(), jsonCommand)

                val request = Request.Builder()
                    .url("http://test.api.catering.bluecodegames.com/user/order")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            Log.d("Réponse du serveur", "Réponse : $responseBody")

                            // Si la réponse est OK, démarrer l'activité de confirmation
                            if (responseBody?.contains("200") == true) {
                                val intent = Intent(this@MainActivity, OrderConfirmationActivity::class.java)
                                startActivity(intent)
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, "Erreur lors de la commande", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e("Réponse du serveur", "Erreur : ${response.code} - ${response.message}")
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Erreur lors de la commande", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("Réponse du serveur", "Échec de la requête : ${e.message}")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Échec de la commande", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        val btnShowTimePicker: Button = findViewById(R.id.btnShowTimePicker)
        btnShowTimePicker.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)
                val formattedTime = selectedTimeFormat.format(selectedTime.time)
                tvSelectedTime.text = "Heure sélectionnée : $formattedTime"
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
}
