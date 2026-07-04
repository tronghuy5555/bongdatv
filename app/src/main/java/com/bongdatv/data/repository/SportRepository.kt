package com.bongdatv.data.repository

import com.bongdatv.data.api.HoiQuanApi
import com.bongdatv.data.api.LiveStatsApi
import com.bongdatv.data.model.Fixture
import com.bongdatv.data.model.LiveStatsResponse
import com.bongdatv.data.model.Sport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportRepository @Inject constructor(
    private val hoiQuanApi: HoiQuanApi,
    private val liveStatsApi: LiveStatsApi
) {

    suspend fun getSports(): Result<List<Sport>> = runCatching {
        hoiQuanApi.getSports().data
    }

    suspend fun getLiveFixtures(): Result<List<Fixture>> = runCatching {
        hoiQuanApi.getUnfinishedFixtures().data.filter { it.isLive }
    }

    suspend fun getUpcomingFixtures(): Result<List<Fixture>> = runCatching {
        hoiQuanApi.getUnfinishedFixtures().data.filter { !it.isLive }
    }

    suspend fun getFinishedFixtures(): Result<List<Fixture>> = runCatching {
        hoiQuanApi.getFinishedFixtures().data
    }

    suspend fun getFixtureById(fixtureId: String): Result<Fixture> = runCatching {
        val id = fixtureId.toIntOrNull()

        findFixture(hoiQuanApi.getUnfinishedFixtures().data, fixtureId, id)
            ?: findFixture(hoiQuanApi.getFinishedFixtures().data, fixtureId, id)
            ?: findFixture(hoiQuanApi.getReplays().data, fixtureId, id)
            ?: error("Fixture not found: $fixtureId")
    }

    suspend fun getLiveStats(referenceIds: List<String>): Result<LiveStatsResponse> = runCatching {
        val ids = referenceIds.joinToString("-")
        liveStatsApi.getFixturesByIds(ids = ids)
    }

    private fun findFixture(fixtures: List<Fixture>, fixtureId: String, id: Int?): Fixture? =
        fixtures.firstOrNull { fixture ->
            fixture.id == id ||
                fixture.referenceId == fixtureId ||
                fixture.slug == fixtureId
        }
}
