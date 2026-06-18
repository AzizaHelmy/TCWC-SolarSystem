package com.aziza.tcwc_solarsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

private val SpaceCard = Color(0xCC0B1223)
private val CardStroke = Color(0xFF2F2E2E)

private val White100 = Color(0xFFFFFFFF)
private val White88 = Color(0xE0FFFFFF)
private val White80 = Color(0xCCFFFFFF)
private val White66 = Color(0xA8FFFFFF)
private val White50 = Color(0x80FFFFFF)
private val Divider = Color(0x29FFFFFF)

private val Rubik = FontFamily(
    Font(R.font.rubik, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_bold, FontWeight.Bold)
)
private val Lily = FontFamily(
    Font(R.font.lily_script_one, FontWeight.Normal)
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolarSystemScreen()
        }
    }
}
@Composable
private fun SolarSystemScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun SolarSystemPreview() {
    SolarSystemScreen()
}