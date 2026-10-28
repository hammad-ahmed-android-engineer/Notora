package com.example.notora.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraAmber
import com.example.notora.core.designsystem.NotoraCyan
import com.example.notora.core.designsystem.NotoraIndigo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.4f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(20f) }
    val subtitleAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "halo_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
        }
        delay(400)
        launch {
            textAlpha.animateTo(1.0f, animationSpec = tween(500))
        }
        launch {
            textOffset.animateTo(0f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
        }
        delay(300)
        launch {
            subtitleAlpha.animateTo(1.0f, animationSpec = tween(600))
        }

        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070A12),
                        Color(0xFF0B0F19),
                        Color(0xFF13112E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(AppDimensions.components.emptyStateIconContainer * 4)
                .scale(pulseScale)
                .alpha(pulseAlpha * alpha.value)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NotoraIndigo.copy(alpha = 0.45f),
                            NotoraCyan.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(AppDimensions.buttonSmallHeight)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(AppDimensions.components.emptyStateIconContainer * 2.2f)
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraLarge))

            AppText(
                text = stringResource(R.string.app_brand_title),
                style = AppTypography.headlineLarge.copy(
                    fontSize = AppDimensions.textSizes.display,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color.White,
                modifier = Modifier
                    .offset(y = textOffset.value.dp)
                    .alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            AppText(
                text = stringResource(R.string.app_tagline),
                style = AppTypography.bodyMedium.copy(
                    fontSize = AppDimensions.textSizes.small,
                    fontWeight = FontWeight.Medium
                ),
                color = NotoraCyan.copy(alpha = 0.9f),
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(modifier = Modifier.height(AppDimensions.components.avatar))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(subtitleAlpha.value)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.cornerSmall)
                        .clip(CircleShape)
                        .background(NotoraIndigo)
                )
                Box(
                    modifier = Modifier
                        .size(AppDimensions.icons.micro)
                        .clip(CircleShape)
                        .background(NotoraCyan)
                )
                Box(
                    modifier = Modifier
                        .size(AppDimensions.cornerSmall)
                        .clip(CircleShape)
                        .background(NotoraAmber)
                )
            }
        }
    }
}
