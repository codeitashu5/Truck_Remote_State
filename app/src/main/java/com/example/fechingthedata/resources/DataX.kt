package com.example.fechingthedata.resources

import com.example.fechingthedata.resources.LastRunningState
import com.example.fechingthedata.resources.LastWaypoint

data class DataX(
    val breakdown: Boolean,
    val companyId: Int,
    val createTime: Long,
    val deactivated: Boolean,
    val durationInsideSite: Int,
    val externalTruck: Boolean,
    val fuelSensorInstalled: Boolean,
    val id: Int,
    val imeiNumber: String,
    val lastRunningState: LastRunningState,
    val lastWaypoint: LastWaypoint,
    val name: String,
    val password: String,
    val simNumber: String,
    val trackerType: Int,
    val transporterId: Int,
    val truckNumber: String,
    val truckSizeId: Int,
    val truckTypeId: Int
)