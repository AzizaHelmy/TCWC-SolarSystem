package com.aziza.tcwc_solarsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

//region theme
private val SpaceCard = Color(0xFF0B1223)
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

private val HeroScrollDistance = 620.dp
private val EarthHeroSize = 980.dp
private const val EarthSettledScale = 0.32f

private val EarthTopOffset = 200.dp
private val EarthTravelDistance = 440.dp
private val StartHeaderExitDistance = 110.dp
private val SolarHeaderTop = 145.dp
private val SolarHeaderEnterDistance = 40.dp
private val PlanetCardWidth = 328.dp
private val PlanetCardHeight = 256.dp
private val PlanetCardHorizontalPadding = 20.dp
private val PlanetInfoColumnWidth = 126.dp

private const val EarthSettledAlpha = 0.92f
private const val SolarHeaderRevealStart = 0.28f
private const val SolarHeaderStartScale = 0.88f
private val PlanetViewportTop = 360.dp
private val StackReveal = 14.dp
private val CardSpacing = 32.dp
private val EarthGlowSize = 600.dp
private val EarthGlowTopOffset = 40.dp

@Immutable
private data class Planet(
    val name: String,
    val subtitle: String,
    @param:DrawableRes val image: Int,
    val weight: String,
    val day: String,
    val temperature: String,
    val info: String
)

private val planets = listOf(
    Planet(
        "Saturn",
        "The Ring Master",
        R.drawable.saturn,
        "70kg → 74kg",
        "10.7 Hours",
        "-178°C, Bring a\njacket",
        "Lighter than\nwater"
    ),
    Planet(
        "Mars",
        "The next colony",
        R.drawable.mars,
        "70kg → 27kg",
        "24.6 Hours",
        "-65°C, Bring a\njacket",
        "Red Dust Storms"
    ),
    Planet(
        "Mercury",
        "The Fastest Planet",
        R.drawable.mercury,
        "70kg → 26kg",
        "1,408 Hours",
        "167°C",
        "Birthday every\n88 days"
    ),
    Planet(
        "Venus",
        "The Toxic Beauty",
        R.drawable.venus,
        "70kg → 63kg",
        "243 Days",
        "465°C",
        "Sun rises from\nWest"
    ),
    Planet(
        "Jupiter",
        "The Heavy Giant",
        R.drawable.jupiter,
        "70kg → 177kg",
        "9.9 Hours",
        "-110°C, Bring a\njacket",
        "Has 95 Moons"
    ),
    Planet(
        "Uranus",
        "The Lazy Iceberg",
        R.drawable.uranus,
        "70kg → 62kg",
        "17 Hours",
        "-224°C, Bring 3\njackets",
        "diamond Shower"
    ),
    Planet(
        "Neptune",
        "The Windy World",
        R.drawable.neptune,
        "70kg → 79kg",
        "16 Hours",
        "-214°C, Bring 3\njackets",
        "Wind faster than\nSound"
    )
)

@Immutable
private data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float
)

@Immutable
private data class HeroMotion(
    val scrollDistancePx: Float,
    val earthTravelPx: Float,
    val startHeaderExitPx: Float,
    val solarHeaderEnterPx: Float
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
    val scrollState = rememberScrollState()
    val motion = rememberHeroMotion()
    val density = LocalDensity.current

    val stackRevealPx = with(density) { StackReveal.toPx() }
    val cardHeightPx = with(density) { PlanetCardHeight.toPx() }
    val cardSpacingPx = with(density) { CardSpacing.toPx() }
    val introPx = with(density) { HeroScrollDistance.toPx() }

    val heroProgress = remember(scrollState, motion) {
        {
            val distance = motion.scrollDistancePx.coerceAtLeast(1f)
            (scrollState.value / distance).coerceIn(0f, 1f)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedSpaceBackground(
            progressProvider = heroProgress,
            modifier = Modifier.zIndex(0f)
        )

        StarsOverlay()

        EarthGlow(
            progressProvider = heroProgress,
            modifier = Modifier.zIndex(0.5f)
        )

        EarthHero(
            progressProvider = heroProgress,
            travelDistancePx = motion.earthTravelPx,
            modifier = Modifier.zIndex(1f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .background(Color.Black.copy(alpha = 0.06f))
        )
        SolarHeader(
            progressProvider = heroProgress,
            enterDistancePx = motion.solarHeaderEnterPx,
            modifier = Modifier.zIndex(4f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = PlanetViewportTop)
                .clipToBounds()
                .zIndex(3f)
                .verticalScroll(scrollState)
        ) {
            Spacer(
                modifier = Modifier.height(
                    HeroScrollDistance +
                            ((PlanetCardHeight + CardSpacing) * planets.size) +
                            300.dp
                )
            )

            planets.forEachIndexed { index, planet ->
                PlanetCard(
                    planet = planet,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(index.toFloat())
                        .graphicsLayer {
                            val normalY =
                                introPx + index * (cardHeightPx + cardSpacingPx)

                            val stackedY =
                                index * stackRevealPx

                            translationY =
                                maxOf(normalY, stackedY + scrollState.value)
                        }
                )
            }
        }

        StartHeader(
            progressProvider = heroProgress,
            exitDistancePx = motion.startHeaderExitPx,
            modifier = Modifier.zIndex(5f)
        )

        SwipeHint(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .zIndex(6f),
            progressProvider = heroProgress
        )
    }
}

@Composable
private fun rememberHeroMotion(): HeroMotion {
    val density = LocalDensity.current
    return remember(density) {
        with(density) {
            HeroMotion(
                scrollDistancePx = HeroScrollDistance.toPx(),
                earthTravelPx = EarthTravelDistance.toPx(),
                startHeaderExitPx = StartHeaderExitDistance.toPx(),
                solarHeaderEnterPx = SolarHeaderEnterDistance.toPx()
            )
        }
    }
}

@Composable
private fun AnimatedSpaceBackground(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val progress = progressProvider()

        val top = androidx.compose.ui.graphics.lerp(
            Color(0xFF05020A),
            Color(0xFF1E1B4B),
            progress
        )

        val middle = androidx.compose.ui.graphics.lerp(
            Color(0xFF061427),
            Color(0xFF0F172A),
            progress
        )

        val bottom = androidx.compose.ui.graphics.lerp(
            Color(0xFF02050E),
            Color(0xFF030712),
            progress
        )

        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to top,
                    0.50f to middle,
                    1.00f to bottom
                )
            )
        )
    }
}

@Composable
private fun EarthGlow(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .requiredSize(EarthGlowSize)
                .offset(y = EarthGlowTopOffset)
                .graphicsLayer {
                    val progress = progressProvider()

                    alpha = progress

                    val scale = 0.8f + (1f - 0.8f) * progress
                    scaleX = scale
                    scaleY = scale
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E1B4B).copy(alpha = 0.45f),
                            Color(0xFF0F172A).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

private fun progressBetween(
    progress: Float,
    start: Float,
    end: Float
): Float {
    return ((progress - start) / (end - start)).coerceIn(0f, 1f)
}

@Composable
private fun SolarHeader(
    progressProvider: () -> Float,
    enterDistancePx: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = SolarHeaderTop)
            .graphicsLayer {
                val progress = progressProvider()
                val revealProgress = progressBetween(
                    progress = progress,
                    start = SolarHeaderRevealStart,
                    end = 1f
                )
                val scale = SolarHeaderStartScale + (1f - SolarHeaderStartScale) * revealProgress

                alpha = revealProgress
                translationY = enterDistancePx * (1f - revealProgress)
                scaleX = scale
                scaleY = scale
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

        Spacer(Modifier.height(6.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Earth is only one small part of a much larger story.",
            fontFamily = Lily,
            fontSize = 16.sp,
            //lineHeight = 22.sp,
            color = White80,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StartHeader(
    progressProvider: () -> Float,
    exitDistancePx: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 96.dp)
            .graphicsLayer {
                val progress = progressProvider()
                val introAlpha = (1f - (progress / 0.12f)).coerceIn(0f, 1f)

                alpha = introAlpha
                translationY = -exitDistancePx * progress
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
private fun EarthHero(
    progressProvider: () -> Float,
    travelDistancePx: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(R.drawable.earth),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .requiredSize(EarthHeroSize)
                .offset(y = EarthTopOffset)
                .graphicsLayer {
                    val progress = progressProvider()
                    val scale = 1f + (EarthSettledScale - 1f) * progress
                    scaleX = scale
                    scaleY = scale
                    translationY = -travelDistancePx * progress
                    alpha = 1f + (EarthSettledAlpha - 1f) * progress
                }
        )
    }
}

@Composable
private fun StarsOverlay(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(R.drawable.stars_overlay),
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .zIndex(0.1f),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun SwipeHint(
    modifier: Modifier = Modifier,
    progressProvider: () -> Float
) {

    Column(
        modifier = modifier
            .padding(bottom = 20.dp)
            .graphicsLayer {
                val progress = progressProvider()
                val introAlpha = (1f - (progress / 0.08f)).coerceIn(0f, 1f)

                alpha = introAlpha
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            .width(PlanetCardWidth)
            .height(PlanetCardHeight)
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
                    x = (-46).dp,
                    y = (-28).dp
                )
                .graphicsLayer {
                    scaleX = 1.02f
                    scaleY = 1.02f
                }
        )

        Column(
            modifier = Modifier
                .offset(x = 142.dp, y = 28.dp)
                .width(160.dp)
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
                .offset(x = PlanetCardHorizontalPadding, y = 118.dp)
                .width(PlanetCardWidth - PlanetCardHorizontalPadding * 2)
                .height(44.dp),
            verticalAlignment = Alignment.Top
        ) {
            PlanetInfoItem(
                icon = R.drawable.ic_weight_scale,
                label = "You Would Weigh",
                value = planet.weight,
                modifier = Modifier.width(PlanetInfoColumnWidth)
            )
            Spacer(Modifier.width(12.dp))
            VerticalDivider()
            Spacer(Modifier.width(14.dp))
            PlanetInfoItem(
                icon = R.drawable.ic_sun,
                label = "One Day",
                value = planet.day,
                modifier = Modifier.width(PlanetInfoColumnWidth)
            )
        }

        HorizontalDividerLine(
            modifier = Modifier
                .offset(x = PlanetCardHorizontalPadding, y = 168.dp)
                .width(PlanetCardWidth - PlanetCardHorizontalPadding * 2)
        )

        Row(
            modifier = Modifier
                .offset(x = PlanetCardHorizontalPadding, y = 188.dp)
                .width(PlanetCardWidth - PlanetCardHorizontalPadding * 2)
                .height(52.dp),
            verticalAlignment = Alignment.Top
        ) {
            PlanetInfoItem(
                icon = R.drawable.ic_temperature,
                label = "Temperature",
                value = planet.temperature,
                modifier = Modifier.width(PlanetInfoColumnWidth)
            )
            Spacer(Modifier.width(12.dp))
            VerticalDivider()
            Spacer(Modifier.width(14.dp))
            PlanetInfoItem(
                icon = R.drawable.ic_alert_circle,
                label = "Additional info",
                value = planet.info,
                modifier = Modifier.width(PlanetInfoColumnWidth)
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
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = White66,
                maxLines = 1
            )
            Text(
                text = value,
                fontFamily = Rubik,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 16.sp,
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
            .height(42.dp)
            .background(Divider)
    )
}

@Composable
private
fun HorizontalDividerLine(
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
