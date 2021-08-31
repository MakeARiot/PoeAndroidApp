package com.example.poetest

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("SetTextI18n")
    @ExperimentalUnsignedTypes
    override fun onResume() {
        super.onResume()

        val helloTv: TextView = findViewById(R.id.hello)
        val c_tv: TextView = findViewById(R.id.textView)
        val imV: ImageView = findViewById(R.id.imageView)
        val btnDw: Button = findViewById(R.id.button)
        val btnUp: Button = findViewById(R.id.button3)

        val url = "https://www.pathofexile.com"

        var indexCounter = 0u
        Thread {
            // запрос на получение валюты
            val query = URL("$url/api/trade/data/static").readText()

            // парсинг валюты
            val js = JSONObject(query)
            val jsArr = js.getJSONArray("result")
            val currency = jsArr.getJSONObject(0)
            val allCur = currency.getJSONArray("entries") // JSONArray

            val arr = Array(allCur.length()) { it } // массив с цифрами от 0 до длины массива со всей валютой
            val picArr = Array<RequestCreator?>(allCur.length()) { null } // массив в котором будут храниться запросы на скачивание картинок
            arr.forEach {
                Picasso.get().setIndicatorsEnabled(false) // *дебаг* проверка откуда идёт изображения (красный - с сервера, синий - кэш)
                val cCur = allCur.getJSONObject( it ) // перебор каждой валюты чтобы достать ссылку на скачивание картинки
                picArr[it] = Picasso.get().load("$url${cCur.get("image")}").resize(70, 70) // запрос на скачивание картинки, заполнение массива
            }

            runOnUiThread {
                var currentCurrency = allCur.getJSONObject(0) // текущая валюта
                helloTv.text = currentCurrency.get("text").toString() // вывод названия
                picArr[0]?.into(imV) // вывод картинки
                c_tv.text = "(${0})"
//                Picasso.get().load("$url${allCur.getJSONObject(0).get("image")}")

                btnUp.setOnClickListener {
                    indexCounter++
                    if (indexCounter > (allCur.length() - 1).toUInt()) {
                        indexCounter = 0u
                    }

                    currentCurrency = allCur.getJSONObject(indexCounter.toInt()) // текущая валюта

                    helloTv.text = currentCurrency.get("text").toString() // вывод названия
                    picArr[indexCounter.toInt()]?.into(imV) // вывод картинки
                    c_tv.text = "(${indexCounter})"
                    // Picasso.get().load("$url${currentCurrency.get("image")}")

                }
                btnDw.setOnClickListener {
                    indexCounter--
                    if (indexCounter < 0u || indexCounter > allCur.length().toUInt()) {
                        indexCounter = (allCur.length() - 1).toUInt()
                    }

                    currentCurrency = allCur.getJSONObject(indexCounter.toInt()) // текущая валюта

                    helloTv.text = currentCurrency.get("text").toString() // вывод названия
                    picArr[indexCounter.toInt()]?.into(imV) // вывод картинки
                    c_tv.text = "(${indexCounter})"
                    // Picasso.get().load("$url${currentCurrency.get("image")}")


//                    Toast.makeText(this, "xeeeee", Toast.LENGTH_SHORT).show()

                }
            }
        }.start()
    }
}
