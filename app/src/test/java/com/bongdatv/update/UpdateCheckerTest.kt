package com.bongdatv.update

import io.mockk.mockk
import okhttp3.OkHttpClient
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateCheckerTest {

    private lateinit var checker: UpdateChecker

    @Before
    fun setUp() {
        checker = UpdateChecker(mockk<OkHttpClient>())
    }

    @Test
    fun `isNewerVersion returns true when major is higher`() {
        assertTrue(checker.isNewerVersion("2.0.0", "1.0.0"))
    }

    @Test
    fun `isNewerVersion returns true when minor is higher`() {
        assertTrue(checker.isNewerVersion("1.2.0", "1.1.0"))
    }

    @Test
    fun `isNewerVersion returns true when patch is higher`() {
        assertTrue(checker.isNewerVersion("1.0.2", "1.0.1"))
    }

    @Test
    fun `isNewerVersion returns false when versions are equal`() {
        assertFalse(checker.isNewerVersion("1.0.0", "1.0.0"))
    }

    @Test
    fun `isNewerVersion returns false when latest is lower`() {
        assertFalse(checker.isNewerVersion("1.0.0", "2.0.0"))
    }

    @Test
    fun `isNewerVersion returns false when minor is lower`() {
        assertFalse(checker.isNewerVersion("1.1.0", "1.2.0"))
    }

    @Test
    fun `isNewerVersion handles missing patch segment`() {
        assertTrue(checker.isNewerVersion("1.1", "1.0.0"))
    }
}
