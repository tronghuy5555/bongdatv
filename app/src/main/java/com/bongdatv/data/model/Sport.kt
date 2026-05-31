package com.bongdatv.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SportResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<Sport>
)

@Serializable
data class Sport(
    val id: Int,
    val slug: String,
    val priority: Int,
    val name: String,
    val iconUrl: String? = null,
    val backgroundCardUrl: String? = null,
    val backgroundMainUrl: String? = null,
    val posterUrl: String? = null,
    val fixtureCount: Int = 0,
    val liveFixtureCount: Int = 0,
    val hasLive: Boolean = false
)
