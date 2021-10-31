package com.example.fechingthedata.resources

import com.example.fechingthedata.resources.Data
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("searchTrucks?auth-company=PCH&companyId=33&deactivated=false&key=g2qb5jvucg7j8skpu5q7ria0mu&q-expand=true&q-include=lastRunningState,lastWaypoint")
    suspend fun getData() : Response<Data>?
}