package com.example.fechingthedata

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(),OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var j = 0
        for(i in list!!){
            val location = LatLng(i.lastWaypoint.lat,i.lastWaypoint.lng)
            //if the truck is not updated for more than 4 hours than the display red
            val time = (i.lastWaypoint.updateTime - i.lastWaypoint.createTime)/(60*60)
            if(time>4){
                googleMap.addMarker(
                    MarkerOptions().position(location).title("${i.truckNumber}")
                        .icon(getBitmapDescriptorFromVector(this,R.drawable.ic_truck_red))
                )
            }
            else if(i.lastRunningState.truckRunningState == 0){
                //this show that the truck is stoped
                if(i.lastWaypoint.ignitionOn){
                    googleMap.addMarker(
                        MarkerOptions().position(location).title("${i.truckNumber}")
                            .icon(getBitmapDescriptorFromVector(this,R.drawable.ic_truck_yellow))
                    )
                }
                else{
                    googleMap.addMarker(
                        MarkerOptions().position(location).title("${i.truckNumber}")
                            .icon(getBitmapDescriptorFromVector(this,R.drawable.ic_truck_blue))
                    )
                }
            }
            else{
                googleMap.addMarker(
                    MarkerOptions().position(location).title("${i.truckNumber}")
                        .icon(getBitmapDescriptorFromVector(this,R.drawable.ic_truck_green))
                )
            }


            //various lavel for the various stuff
            val zoomLavel = 4f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,zoomLavel))

        }

    }

    //if we want to use vector we have to use BitmapDescripterFactor
    private fun  getBitmapDescriptorFromVector(context: Context, resourceId:Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, resourceId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas =  Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}