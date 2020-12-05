package com.ari.adzanapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ari.adzanapp.R
import com.ari.adzanapp.model.DataKota
import com.ari.adzanapp.network.AsyncTask
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var listDataKota: MutableList<DataKota>? = null
    private var mDataKotaAdapter: ArrayAdapter<DataKota>? = null
    private var greetImg: ImageView? = null
    private var greetText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greetImg = findViewById(R.id.greeting_img)
        greetText = findViewById(R.id.greeting_text)

        listDataKota = ArrayList()
        mDataKotaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listDataKota as ArrayList<DataKota>
        )
        mDataKotaAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kota.adapter = mDataKotaAdapter
        kota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val kota = mDataKotaAdapter!!.getItem(position)
                loadJadwal(kota?.id)
            }

        }

        loadKota()
        greeting()
    }

    @SuppressLint("SetTextI18n")
    private fun greeting() {
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greetText?.setText("Good morning, have you prayed at dawn?")
            greetImg?.setImageResource(R.drawable.good_morning)
        } else if (timeOfDay >= 12 && timeOfDay < 15) {
            greetText?.setText("Good afternoon, have you prayed at dzuhur?")
            greetImg?.setImageResource(R.drawable.afternoon)
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            greetText?.setText("Good afternoon, have you prayed at ashar?")
            greetImg?.setImageResource(R.drawable.afternoon)
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            greetText?.setText("Good evening, have you prayed at maghrib? next one is isya")
            greetText?.setTextColor(Color.BLACK)
            greetImg?.setImageResource(R.drawable.evening)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadJadwal(id: Int?) {
        try {
            val idKota = id.toString()

            val current = SimpleDateFormat("yyyy-MM-dd")
            val tanggal = current.format(Date())

            val url = "https://api.banghasan.com/sholat/format/json/jadwal/kota/$idKota/tanggal/$tanggal"
            val task = AsyncTask(this, object : AsyncTask.OnPostExecuteListener {
                override fun onPostExecute(result: String) {

                    Log.d("JadwalData", result)
                    try {
                        val jsonObj = JSONObject(result)
                        val objJadwal = jsonObj.getJSONObject("jadwal")
                        val obData = objJadwal.getJSONObject("data")

                        tv_tanggal.text = obData.getString("tanggal")
                        tv_subuh.text = obData.getString("subuh")
                        tv_dzuhur.text = obData.getString("dzuhur")
                        tv_ashar.text = obData.getString("ashar")
                        tv_maghrib.text = obData.getString("maghrib")
                        tv_isya.text = obData.getString("isya")

                        Log.d("dataJadwal", obData.toString())

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            })
            task.execute(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun loadKota() {
        try {
            val url = "https://api.banghasan.com/sholat/format/json/kota"
            val task = AsyncTask(this, object : AsyncTask.OnPostExecuteListener {
                override fun onPostExecute(result: String) {

                    Log.d("KotaData", result)
                    try {
                        val jsonObj = JSONObject(result)
                        val jsonArray = jsonObj.getJSONArray("kota")
                        var daftarKota: DataKota?
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            daftarKota = DataKota()
                            daftarKota.id = obj.getInt("id")
                            daftarKota.nama = obj.getString("nama")
                            listDataKota!!.add(daftarKota)
                        }
                        mDataKotaAdapter!!.notifyDataSetChanged()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            })
            task.execute(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}