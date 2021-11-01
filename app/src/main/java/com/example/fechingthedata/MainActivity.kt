package com.example.fechingthedata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CursorAdapter
import androidx.appcompat.widget.SearchView
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
import java.util.*

var list : List<DataX>? = null
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var displayList: MutableList<DataX>
    private lateinit var permanent : MutableList<DataX>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this,MapActivity::class.java)
        startActivity(intent)


        runBlocking {
            async {
                list = getData() }.await()
        }

        displayList = mutableListOf()
        permanent = mutableListOf()
        displayList.addAll(list!!)
        permanent.addAll(list!!)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = CustomAdapter(displayList)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_menu,menu)
        //create item that we have
        var searchItem : MenuItem = menu!!.findItem(R.id.action_search)

        //using that item as search view that what it is constructed for
        var searchView = searchItem.actionView as SearchView

        //making the function call to get the required result
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText != null && newText.length != 0){
                        displayList.clear()
                        var search = newText.toLowerCase()

                        for(i in permanent){
                            if(i.truckNumber.toLowerCase().contains(search)){
                                displayList.add(i)
                            }

                            recyclerView.adapter?.notifyDataSetChanged()

                        }
                    }
                    else{
                        displayList.clear()
                        displayList.addAll(permanent)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }

                    return true
                }

            }


        )




        return super.onCreateOptionsMenu(menu)
    }

}