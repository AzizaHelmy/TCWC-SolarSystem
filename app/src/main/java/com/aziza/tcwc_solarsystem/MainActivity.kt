package com.aziza.tcwc_solarsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//region theme
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
//endregion
private data class Planet(
    val name: String,
    val subtitle: String,
    @DrawableRes val image: Int,
    val weight: String,
    val day: String,
    val temperature: String,
    val info: String
)
private val planets = listOf(
    Planet("Saturn", "The Ring Master", R.drawable.saturn, "70kg → 74kg", "10.7 Hours", "-178°C, Bring a\njacket", "Lighter than\nwater"),
    Planet("Mars", "The next colony", R.drawable.mars, "70kg → 27kg", "24.6 Hours", "-65°C, Bring a\njacket", "Red Dust Storms"),
    Planet("Mercury", "The Fastest Planet", R.drawable.mercury, "70kg → 26kg", "1,408 Hours", "167°C", "Birthday every\n88 days")
)
private data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            SolarSystemScreen()
        }
    }
}
@Composable
private fun SolarSystemScreen() {
    var selectedPlanet by rememberSaveable { mutableStateOf(planets.first().name) }
    val selected = remember(selectedPlanet) {
        planets.first { it.name == selectedPlanet }
    }

    val listState = rememberLazyListState()

    val progress by remember {
        derivedStateOf {
            val offset = if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset
            } else {
                600
            }
            (offset / 600f).coerceIn(0f, 1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0xFF05020A),
                        0.44f to Color(0xFF061427),
                        1.00f to Color(0xFF02050E)
                    )
                )
            )
    ) {
        EarthHero(progress = progress)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            item {
                Spacer(Modifier.height(1000.dp))
            }

            items(planets, key = { it.name }) { planet ->
                PlanetCard(
                    planet = planet,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
        }

        StartHeader(progress = progress)

        SolarHeader(progress = progress)

        SwipeHint(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            visible = progress < 0.08f
        )
    }
}
@Composable
private fun SolarHeader(progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 178.dp)
            .graphicsLayer {
                alpha = progress
                translationY = 40f * (1f - progress)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Our Solar System",
            fontFamily = Rubik,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = White88,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Earth is only one small part of a much larger\nstory.",
            fontFamily = Lily,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = White80,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
private fun StartHeader(progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 96.dp)
            .graphicsLayer {
                alpha = 1f - progress
                translationY = -110f * progress
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Earth",
            fontFamily = Rubik,
            fontWeight = FontWeight.Bold,
            fontSize = 64.sp,
            lineHeight = 72.sp,
            color = White88
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "A tiny blue world drifting\nthrough the endless dark.",
            fontFamily = Lily,
            fontSize = 19.sp,
            lineHeight = 24.sp,
            color = White80,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
private fun EarthHero(progress: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(R.drawable.earth),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .requiredSize(980.dp)
                .offset(y = 195.dp)
                .graphicsLayer {
                    val scale = 1f + (0.22f - 1f) * progress
                    scaleX = scale
                    scaleY = scale
                    translationY = -520f * progress
                }
        )
    }
}
@Composable
private fun StarField() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val stars = listOf(
            Star(0.74f, 0.05f, 2.4f, 0.28f),
            Star(0.26f, 0.16f, 1.8f, 0.30f),
            Star(0.60f, 0.23f, 1.4f, 0.44f),
            Star(0.39f, 0.30f, 2.1f, 0.42f),
            Star(0.53f, 0.35f, 2.8f, 0.52f),
            Star(0.81f, 0.36f, 1.9f, 0.34f),
            Star(0.18f, 0.43f, 2.2f, 0.36f),
            Star(0.66f, 0.49f, 1.6f, 0.34f),
            Star(0.30f, 0.56f, 2.7f, 0.32f),
            Star(0.73f, 0.68f, 2.1f, 0.28f)
        )

        stars.forEach { star ->
            drawCircle(
                color = Color.White.copy(alpha = star.alpha),
                radius = star.radius,
                center = Offset(
                    x = size.width * star.x,
                    y = size.height * star.y
                )
            )
        }
    }
}
@Composable
private fun SwipeHint(
    modifier: Modifier = Modifier,
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier.padding(bottom = 20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SwipeChevrons()

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Swipe up to explore",
                fontFamily = Rubik,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = White100
            )
        }
    }
}
@Composable
private fun SwipeChevrons() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        repeat(3) { index ->
            val alpha = when (index) {
                0 -> 0.42f
                1 -> 0.62f
                else -> 0.86f
            }

            Canvas(
                modifier = Modifier
                    .width(18.dp)
                    .height(8.dp)
            ) {
                val stroke = 2.dp.toPx()
                drawLine(
                    color = White100.copy(alpha = alpha),
                    start = Offset(1.dp.toPx(), size.height - 1.dp.toPx()),
                    end = Offset(size.width / 2f, 1.dp.toPx()),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = White100.copy(alpha = alpha),
                    start = Offset(size.width - 1.dp.toPx(), size.height - 1.dp.toPx()),
                    end = Offset(size.width / 2f, 1.dp.toPx()),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
            }

            if (index != 2) {
                Spacer(Modifier.height(3.dp))
            }
        }
    }
}
@Composable
private fun PlanetCard(
    planet: Planet,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .width(328.dp)
            .height(242.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
                .background(SpaceCard)
                .border(
                    0.5.dp,
                    CardStroke,
                    cardShape
                )
        )

        Image(
            painter = painterResource(planet.image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .requiredSize(250.dp)
                .offset(
                    x = (-54).dp,
                    y = (-28).dp
                )
                .graphicsLayer {
                    scaleX = 1.08f
                    scaleY = 1.08f
                }
        )

        Column(
            modifier = Modifier
                .offset(x = 132.dp, y = 28.dp)
                .width(170.dp)
        ) {
            Text(
                text = planet.name,
                fontFamily = Rubik,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                color = White88
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = planet.subtitle,
                fontFamily = Rubik,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                color = White66
            )
        }

        Row(
            modifier = Modifier
                .offset(x = 28.dp, y = 116.dp)
                .width(292.dp)
                .height(40.dp),
            verticalAlignment = Alignment.Top
        ) {
            PlanetInfoItem(
                icon = R.drawable.ic_weight_scale,
                label = "You Would Weigh",
                value = planet.weight,
                modifier = Modifier.width(126.dp)
            )
            Spacer(Modifier.width(14.dp))
            VerticalDivider()
            Spacer(Modifier.width(21.dp))
            PlanetInfoItem(
                icon = R.drawable.ic_sun,
                label = "One Day",
                value = planet.day,
                modifier = Modifier.width(130.dp)
            )
        }

        HorizontalDividerLine(
            modifier = Modifier
                .offset(x = 26.dp, y = 150.dp)
                .width(276.dp)
        )

        Row(
            modifier = Modifier
                .offset(x = 28.dp, y = 170.dp)
                .width(292.dp)
                .height(44.dp),
            verticalAlignment = Alignment.Top
        ) {
            PlanetInfoItem(
                icon = R.drawable.ic_temperature,
                label = "Temperature",
                value = planet.temperature,
                modifier = Modifier.width(126.dp)
            )
            Spacer(Modifier.width(14.dp))
            VerticalDivider()
            Spacer(Modifier.width(21.dp))
            PlanetInfoItem(
                icon = R.drawable.ic_alert_circle,
                label = "Additional info",
                value = planet.info,
                modifier = Modifier.width(130.dp)
            )
        }
    }
}

@Composable
private fun PlanetInfoItem(
    @DrawableRes icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = White66,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                fontFamily = Rubik,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = White66,
                maxLines = 1
            )
            Text(
                text = value,
                fontFamily = Rubik,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                color = White88
            )
        }
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Divider)
    )
}

@Composable
private fun HorizontalDividerLine(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .height(1.dp)
            .background(Divider)
    )
}

@Preview(widthDp = 360, heightDp = 300)
@Composable
private fun PlanetCardPreview() {
    val saturnPlanet = Planet(
        name = "Saturn",
        subtitle = "The Ring Master",
        image = R.drawable.saturn,
        weight = "70kg → 74kg",
        day = "10.7 Hours",
        temperature = "-178°C, Bring a\njacket",
        info = "Lighter than\nwater"
    )

    PlanetCard(
        planet = saturnPlanet
    )
}



@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun SolarSystemPreview() {
    SolarSystemScreen()
}
