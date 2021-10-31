package com.example.fechingthedata.resources

data class LastRunningState(
    val lat: Double,
    val lng: Double,
    val stopNotficationSent: Int,
    val stopStartTime: Long,
    val truckId: Int,
    val truckRunningState: Int
)