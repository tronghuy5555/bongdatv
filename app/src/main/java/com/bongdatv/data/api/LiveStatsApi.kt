package com.bongdatv.data.api

import com.bongdatv.data.model.LiveStatsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LiveStatsApi {

    @GET("fixtures")
    suspend fun getFixturesByIds(
        @Query("by") by: String = "ids",
        @Query("value") ids: String
    ): LiveStatsResponse
}
