package com.example.fechingthedata.recycle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fechingthedata.R
import com.example.fechingthedata.resources.DataX

class CustomAdapter(val list : List<DataX>?
): RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_layout,
            parent,
            false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
      val text = holder.itemView.findViewById<TextView>(R.id.truckName)

        //set the truck name
        text.text = list?.get(position)?.truckNumber
        val state = holder.itemView.findViewById<TextView>(R.id.state)
        val speed = holder.itemView.findViewById<TextView>(R.id.speed)
        val updated = holder.itemView.findViewById<TextView>(R.id.lastUpdated)




        //here lot of computation is to be done to show the items on the list
        var stopStartTime : Long? = null
        var createTime : Long? = null
        var updateTime : Long? = null
        var s :Int? = null

        if (list != null) {
            stopStartTime =    list[position].lastRunningState.stopStartTime
            createTime    =    list[position].lastWaypoint.createTime
            updateTime    =    list[position].lastWaypoint.updateTime
            s = list[position].lastRunningState.truckRunningState
        }
        val  totalTimeRuning  =  claculateTime(stopStartTime,updateTime)
        val  lastUpdate = claculateTime(createTime,updateTime)

        //calculate the day/hrs/min
        //calculate the days
        // 1 day = 24*60*60
        val days = totalTimeRuning/(24*60*60)
        val hrs = (totalTimeRuning%(24*60*60))/(60*60)
        val min = (totalTimeRuning%(60*60))/60

        val days1 = lastUpdate/(24*60*60)
        val hrs1 = (lastUpdate%(24*60*60))/(60*60)
        val min1 = (lastUpdate%(60*60))/60



        //depending on the state only
        var res1 : String? = null
        var res : String? = null
        res = timeValue(days,hrs,min)
        res1 = timeValue(days1,hrs1,min1)




        var k = 0

        if(s == 1){
            state.text = "Running since last ${res}"
            speed.text = list?.get(position)?.lastWaypoint?.speed.toString() + " k/h"

        }
        if(s == 0){
            state.text = "Stop since last ${res}"
            speed.text = ""
        }




        updated.text = "${res1} ago"




    }

    private fun timeValue(days: Long, hrs: Long, min: Long): String? {
        if(days>0)
           return "${days} days"
        else if(hrs>0)
            return "${hrs} hrs"
        else
            return  "${min} min"

    }

    private fun claculateTime(start:Long?,end:Long?) : Long {
      return end!! - start!!
    }

    override fun getItemCount(): Int {
        if(list==null)
            return 0
        else
            return list.size


    }
}