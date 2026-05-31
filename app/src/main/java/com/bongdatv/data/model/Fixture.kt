package com.bongdatv.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FixtureResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<Fixture>
)

@Serializable
data class Fixture(
    val id: Int,
    val referenceId: String? = null,
    val slug: String,
    val title: String,
    val startTime: String,
    val elapsedTime: String? = null,
    val isLive: Boolean = false,
    val isPinned: Boolean = false,
    val isHot: Boolean = false,
    val sport: Sport? = null,
    val league: League? = null,
    val homeTeam: Team? = null,
    val awayTeam: Team? = null,
    val score: Score? = null,
    val status: FixtureStatus? = null,
    val fixtureCommentators: List<FixtureCommentator> = emptyList()
)

@Serializable
data class Team(
    val id: Int,
    val slug: String? = null,
    val name: String,
    val logoUrl: String? = null
)

@Serializable
data class Score(
    val home: Int = 0,
    val away: Int = 0
)

@Serializable
data class FixtureStatus(
    val id: Int? = null,
    val code: String? = null,
    val description: String? = null
)

@Serializable
data class FixtureCommentator(
    val id: Int,
    val priority: Int = 0,
    val commentator: Commentator
)

@Serializable
data class Commentator(
    val id: Int,
    val name: String? = null,
    val nickname: String? = null,
    val avatarUrl: String? = null,
    val streams: List<Stream> = emptyList()
)

@Serializable
data class Stream(
    val id: Int,
    val priority: Int = 0,
    val name: String,
    val sourceUrl: String
)
