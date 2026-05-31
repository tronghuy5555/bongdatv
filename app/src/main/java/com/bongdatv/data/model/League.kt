package com.bongdatv.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LeagueResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<League>
)

@Serializable
data class League(
    val id: Int,
    val slug: String,
    val shortName: String? = null,
    val name: String,
    val logoUrl: String? = null,
    val sport: Sport? = null
)
