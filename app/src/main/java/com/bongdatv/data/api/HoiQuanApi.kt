package com.bongdatv.data.api

import com.bongdatv.data.model.FixtureResponse
import com.bongdatv.data.model.LeagueResponse
import com.bongdatv.data.model.SportResponse
import retrofit2.http.GET

interface HoiQuanApi {

    @GET("sports")
    suspend fun getSports(): SportResponse

    @GET("leagues")
    suspend fun getLeagues(): LeagueResponse

    @GET("fixtures/unfinished")
    suspend fun getUnfinishedFixtures(): FixtureResponse

    @GET("fixtures/finished")
    suspend fun getFinishedFixtures(): FixtureResponse

    @GET("replays")
    suspend fun getReplays(): FixtureResponse
}
