package com.example.fechingthedata

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.CursorAdapter
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet.GONE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fechingthedata.recycle.CustomAdapter
import com.example.fechingthedata.resources.ApiInterface
import com.example.fechingthedata.resources.DataX
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var recyclerView: RecyclerView
    private lateinit var displayList: MutableList<DataX>
    private lateinit var mapView: MapView
    var list: List<DataX>? = null

    //to know in what state the app is currently is
    private var isMap = true
    var zoomVlaue = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initilize the api


        //get a reference of map view
        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        //get the data from the api to get the required list of data
        runBlocking {
            async {
                list = getData()
            }.await()
        }

        //the list section to be used to store and display truck based on search
        displayList = mutableListOf()
        displayList.addAll(list!!)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = CustomAdapter(displayList)
        recyclerView.layoutManager = LinearLayoutManager(this)


        //initial state of the activity we have the map visible and the list is GONE
        recyclerView.visibility = View.GONE
        mapView.visibility = View.VISIBLE

    }

    private suspend fun getData(): List<DataX>? {
        //now we will use retrofit to get the data from the internet
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mystral.in/tt/mobile/logistics/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiInterface::class.java)
        var list: List<DataX>? = null

        try {
            withContext(Dispatchers.IO) {
                //now using that api we will call all the other objects
                val res = api.getData()
                list = res?.body()?.data
            }
        }
        catch (e: Exception) {
            Toast.makeText(this, "not rendered", Toast.LENGTH_SHORT).show()
        }
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_menu, menu)
        //create item that we have
        val searchItem: MenuItem = menu!!.findItem(R.id.action_search)
        //using that item as search view that what it is constructed for
        val searchView = searchItem.actionView as SearchView


        //making the function call to get the required result
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(newString: String?): Boolean {
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null && newText.length != 0) {
                        displayList.clear()
                        var search = newText.toLowerCase()

                        for (i in list!!) {
                            if (i.truckNumber.toLowerCase().contains(search)) {
                                displayList.add(i)
                            }

                            recyclerView.adapter?.notifyDataSetChanged()

                        }
                    } else {
                        displayList.clear()
                        displayList.addAll(list!!)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }

                    return true
                }

            }
        )

        return super.onCreateOptionsMenu(menu)
    }


// when the map get ready this function gets called so that we can use the map object to work on the stuff
    override fun onMapReady(googleMap: GoogleMap) {

       val map = googleMap
      //using the floating action button to zoom in
       val zoomBtn = findViewById<FloatingActionButton>(R.id.floatingActionButton)
       zoomBtn.setOnClickListener {
           zoomVlaue = map.cameraPosition.zoom + 1f
           map.animateCamera(CameraUpdateFactory.zoomTo(zoomVlaue), 1000, null)
       }

        //placing the markers according to the needs
        for (i in list!!) {
            val location = LatLng(i.lastWaypoint.lat, i.lastWaypoint.lng)
            //if the truck is not updated for more than 4 hours than the display red
            val time = (i.lastWaypoint.updateTime - i.lastWaypoint.createTime) / (60 * 60)
            if (time >= 4) {
                googleMap.addMarker(
                    MarkerOptions().position(location).title("${i.truckNumber}")
                        .icon(getBitmapDescriptorFromVector(this, R.drawable.ic_truck_red))
                )
            } else if (i.lastRunningState.truckRunningState == 0) {
                //this show that the truck is stoped
                if (i.lastWaypoint.ignitionOn) {
                    googleMap.addMarker(
                        MarkerOptions().position(location).title("${i.truckNumber}")
                            .icon(getBitmapDescriptorFromVector(this, R.drawable.ic_truck_yellow))
                    )


                } else {
                    googleMap.addMarker(
                        MarkerOptions().position(location).title("${i.truckNumber}")
                            .icon(getBitmapDescriptorFromVector(this, R.drawable.ic_truck_blue))
                    )
                }
            } else {
                googleMap.addMarker(
                    MarkerOptions().position(location).title("${i.truckNumber}")
                        .icon(getBitmapDescriptorFromVector(this, R.drawable.ic_truck_green))
                )
            }
            //various lavel for the various stuff
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))

        }


    }

    //if we want to use vector we have to use BitmapDescripterFactor
    private fun getBitmapDescriptorFromVector(context: Context, resourceId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, resourceId)
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


  //life cycle methods are override for the map to function correctly
    override fun onStart() {
        mapView.onStart()
        super.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    // implementing on click feature for the toggle btn
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.toggle->{
                if(isMap){
                    mapView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    item.icon = ContextCompat.getDrawable(this,R.drawable.ic_map_shift)
                    isMap = false
                }
                else{
                    mapView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    item.icon = ContextCompat.getDrawable(this,R.drawable.ic_list)
                    isMap = true
                }
                return true
            }

           else ->{
               return super.onOptionsItemSelected(item)
           }
        }
    }
}