package com.example.fechingthedata.resources

import com.example.fechingthedata.resources.DataX
import com.example.fechingthedata.resources.ResponseCode

data class Data(
    val `data`: List<DataX>,
    val responseCode: ResponseCode
)