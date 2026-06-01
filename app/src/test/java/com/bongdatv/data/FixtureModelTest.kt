package com.bongdatv.data

import com.bongdatv.data.model.Fixture
import com.bongdatv.data.model.Score
import com.bongdatv.data.model.Team
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FixtureModelTest {

    @Test
    fun `Fixture isLive defaults to false`() {
        val fixture = Fixture(id = 1, slug = "test", title = "Test", startTime = "2026-06-01T10:00:00Z")
        assertFalse(fixture.isLive)
    }

    @Test
    fun `Fixture isPinned defaults to false`() {
        val fixture = Fixture(id = 1, slug = "test", title = "Test", startTime = "2026-06-01T10:00:00Z")
        assertFalse(fixture.isPinned)
    }

    @Test
    fun `Fixture with isLive true is correctly set`() {
        val fixture = Fixture(id = 1, slug = "live-match", title = "Live Match", startTime = "2026-06-01T10:00:00Z", isLive = true)
        assertTrue(fixture.isLive)
    }

    @Test
    fun `Score defaults to 0-0`() {
        val score = Score()
        assertEquals(0, score.home)
        assertEquals(0, score.away)
    }

    @Test
    fun `Team has correct name and id`() {
        val team = Team(id = 42, name = "Barcelona", slug = "barcelona")
        assertEquals(42, team.id)
        assertEquals("Barcelona", team.name)
    }

    @Test
    fun `Fixture fixtureCommentators defaults to empty list`() {
        val fixture = Fixture(id = 1, slug = "test", title = "Test", startTime = "2026-06-01T10:00:00Z")
        assertTrue(fixture.fixtureCommentators.isEmpty())
    }
}
