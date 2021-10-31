package com.example.fechingthedata.resources

data class LastWaypoint(
    val accuracy: Double,
    val batteryLevel: Int,
    val batteryPower: Boolean,
    val bearing: Double,
    val createTime: Long,
    val fuelLevel: Int,
    val id: Int,
    val ignitionOn: Boolean,
    val lat: Double,
    val lng: Double,
    val odometerReading: Double,
    val speed: Double,
    val truckId: Int,
    val updateTime: Long
)