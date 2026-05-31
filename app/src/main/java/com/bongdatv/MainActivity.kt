package com.bongdatv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bongdatv.ui.navigation.AppNavigation
import com.bongdatv.ui.theme.BongDaTVTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BongDaTVTheme {
                AppNavigation()
            }
        }
    }
}
