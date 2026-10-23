package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DefaultData
import com.example.model.AppThemePreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string and verify 1Baghdad v3_9 real server configuration without fake data`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("دجلة بلس TV", appName)

        val db = DefaultData.createInitialDatabase()
        assertEquals(AppThemePreset.ROYAL_BRONZE, db.config.themePreset)
        assertEquals("24:43:dd:c9:07:47", db.config.macAddress)
        assertEquals("136115", db.config.deviceKey)
        assertTrue(db.config.validActivationCodes.contains("1"))
        assertTrue(db.config.validActivationCodes.contains("B1"))
        assertEquals("http://foxbleu.org:8789", db.config.xtreamConfig.hostUrl)
        // Verify zero fake/mock channels or VOD items prior to live server sync
        assertTrue(db.channels.isEmpty())
        assertTrue(db.vodItems.isEmpty())
        assertTrue(db.matches.isEmpty())
    }
}
