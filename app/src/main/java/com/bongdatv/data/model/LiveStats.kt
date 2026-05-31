package com.bongdatv.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LiveStatsResponse(
    val totalRecords: Int,
    val results: List<LiveFixture>
)

@Serializable
data class LiveFixture(
    val fixture: LiveFixtureInfo,
    val league: LiveLeague? = null,
    val teams: LiveTeams? = null,
    val goals: LiveGoals? = null
)

@Serializable
data class LiveFixtureInfo(
    val id: Int,
    val status: LiveStatus? = null
)

@Serializable
data class LiveStatus(
    val long: String? = null,
    val short: String? = null,
    val elapsed: Int? = null,
    val extra: Int? = null
)

@Serializable
data class LiveLeague(
    val id: Int,
    val name: String? = null,
    val logo: String? = null
)

@Serializable
data class LiveTeams(
    val home: LiveTeam? = null,
    val away: LiveTeam? = null
)

@Serializable
data class LiveTeam(
    val id: Int,
    val name: String? = null,
    val logo: String? = null
)

@Serializable
data class LiveGoals(
    val home: Int? = null,
    val away: Int? = null
)
