package com.example.fechingthedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CursorAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fechingthedata.recycle.CustomAdapter
import com.example.fechingthedata.resources.ApiInterface
import com.example.fechingthedata.resources.DataX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //now we construct a retrofit builder object and try make the call

        var list : List<DataX>? = null

        runBlocking {
            async {
                list = getData() }.await()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = CustomAdapter(list)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private suspend fun getData() : List<DataX>? {
        //now we will use retrofit to get the data from the internet
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mystral.in/tt/mobile/logistics/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val api =  retrofit.create(ApiInterface::class.java)
        var list:List<DataX>? = null

        try {
            withContext(Dispatchers.IO){
                //now using that api we will call all the other objects
                val res = api.getData()
                list = res?.body()?.data
            }
        }
        catch (e: Exception){
            Toast.makeText(this, "not rendered", Toast.LENGTH_SHORT).show()
        }


    return list

    }


}