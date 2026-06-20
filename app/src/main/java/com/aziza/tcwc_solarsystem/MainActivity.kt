package com.aziza.tcwc_solarsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.annotation.Keep
import androidx.compose.foundation.layout.fillMaxHeight

//region theme
private val SpaceCard = Color(0xFF0B0E17)
private val CardStroke = Color(0x1AFFFFFF)

private val White100 = Color(0xFFFFFFFF)
private val White88 = Color(0xE0FFFFFF)
private val White80 = Color(0xCCFFFFFF)
private val LabelColor = Color(0xFF94A3B8)
private val Divider = Color(0x14FFFFFF)

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
private val PlanetCardHeight = 260.dp
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
        "-178°C, Bring a jacket",
        "Lighter than water"
    ),
    Planet(
        "Mars",
        "The next colony",
        R.drawable.mars,
        "70kg → 27kg",
        "24.6 Hours",
        "-65°C, Bring a jacket",
        "Red Dust Storms"
    ),
    Planet(
        "Mercury",
        "The Fastest Planet",
        R.drawable.mercury,
        "70kg → 26kg",
        "1,408 Hours",
        "167°C",
        "Birthday every 88 days"
    ),
    Planet(
        "Venus",
        "The Toxic Beauty",
        R.drawable.venus,
        "70kg → 63kg",
        "243 Days",
        "465°C",
        "Sun rises from West"
    ),
    Planet(
        "Jupiter",
        "The Heavy Giant",
        R.drawable.jupiter,
        "70kg → 177kg",
        "9.9 Hours",
        "-110°C, Bring a jacket",
        "Has 95 Moons"
    ),
    Planet(
        "Uranus",
        "The Lazy Iceberg",
        R.drawable.uranus,
        "70kg → 62kg",
        "17 Hours",
        "-224°C, Bring 3 jackets",
        "Diamond Shower"
    ),
    Planet(
        "Neptune",
        "The Windy World",
        R.drawable.neptune,
        "70kg → 79kg",
        "16 Hours",
        "-214°C, Bring 3 jackets",
        "Wind faster than Sound"
    )
)

@Immutable
private data class HeroMotion(
    val scrollDistancePx: Float,
    val earthTravelPx: Float,
    val startHeaderExitPx: Float,
    val solarHeaderEnterPx: Float
)

@Keep
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

        EarthImage(
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
            modifier = Modifier.padding(4.dp),
            text = "Earth",
            textAlign = TextAlign.Center,
            fontFamily = Rubik,
            fontWeight = FontWeight.Bold,
            fontSize = 64.sp,
            lineHeight = 76.sp,
            color = White88
        )

        Text(
            text = "A tiny blue world drifting\nthrough the endless dark.",
            fontFamily = Lily,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = White80,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EarthImage(
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
            .padding(bottom = 18.dp)
            .graphicsLayer {
                val progress = progressProvider()
                val introAlpha = (1f - (progress / 0.08f)).coerceIn(0f, 1f)
                alpha = introAlpha
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SwipeArrows()

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = "Swipe up to explore",
            fontFamily = Rubik,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = White100
        )
    }
}

@Composable
private fun SwipeArrows() {
    val transition = rememberInfiniteTransition(label = "swipe_chevrons")

    val phase = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1400,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "chevron_phase"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        repeat(3) { index ->
            Canvas(
                modifier = Modifier
                    .width(18.dp)
                    .height(7.dp)
                    .graphicsLayer {
                        val localPhase = ((phase.value + index * 0.22f) % 1f)

                        val arrowAlpha = when {
                            localPhase < 0.35f -> localPhase / 0.35f
                            localPhase < 0.75f -> 1f
                            else -> 1f - ((localPhase - 0.75f) / 0.25f)
                        }.coerceIn(0f, 1f)

                        alpha = 0.25f + arrowAlpha * 0.75f
                        translationY = -3f * arrowAlpha
                    }
            ) {
                val stroke = 2.dp.toPx()

                drawLine(
                    color = White100,
                    start = Offset(1.dp.toPx(), size.height - 1.dp.toPx()),
                    end = Offset(size.width / 2f, 1.dp.toPx()),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = White100,
                    start = Offset(size.width - 1.dp.toPx(), size.height - 1.dp.toPx()),
                    end = Offset(size.width / 2f, 1.dp.toPx()),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
            }

            if (index != 2) {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PlanetCard(
    planet: Planet,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(PlanetCardHeight)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
                .background(SpaceCard)
                .border(1.dp, CardStroke, cardShape)
        )


        Box(
            modifier = Modifier
                .zIndex(0.5f)
                .size(180.dp)
                .offset(x = (-30).dp, y = (-30).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE29272).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )


        Image(
            painter = painterResource(planet.image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .zIndex(2f)
                .requiredSize(if (planet.name == "Saturn") 260.dp else 200.dp)
                .offset(
                    x = if (planet.name == "Saturn") (-60).dp else (-40).dp,
                    y = if (planet.name == "Saturn") (-70).dp else (-40).dp
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(end = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.width(160.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = planet.name,
                        fontFamily = Rubik,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        lineHeight = 32.sp,
                        color = White100
                    )
                    Text(
                        text = planet.subtitle,
                        fontFamily = Rubik,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        color = LabelColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .fillMaxHeight()
                        .padding(vertical = 12.dp)
                        .align(Alignment.Center)
                        .background(Divider)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .padding(horizontal = 24.dp)
                        .align(Alignment.Center)
                        .background(Divider)
                )

                // Grid Items
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        PlanetInfoItem(
                            icon = R.drawable.ic_weight_scale,
                            label = "You Would Weigh",
                            value = planet.weight,
                            modifier = Modifier.weight(1f)
                        )
                        PlanetInfoItem(
                            icon = R.drawable.ic_sun,
                            label = "One Day",
                            value = planet.day,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        PlanetInfoItem(
                            icon = R.drawable.ic_temperature,
                            label = "Temperature",
                            value = planet.temperature,
                            modifier = Modifier.weight(1f)
                        )
                        PlanetInfoItem(
                            icon = R.drawable.ic_alert_circle,
                            label = "Additional info",
                            value = planet.info,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
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
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = White100,
            modifier = Modifier.size(18.dp)
        )

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = label,
                fontFamily = Rubik,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = LabelColor
            )

            if (value.contains(",")) {
                val parts = value.split(",")
                Column {
                    Text(
                        text = parts[0],
                        fontFamily = Rubik,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = White100
                    )
                    Text(
                        text = parts[1].trim(),
                        fontFamily = Rubik,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        color = LabelColor
                    )
                }
            } else {
                Text(
                    text = value,
                    fontFamily = Rubik,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = White100,
                    lineHeight = 18.sp
                )
            }
        }
    }
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
