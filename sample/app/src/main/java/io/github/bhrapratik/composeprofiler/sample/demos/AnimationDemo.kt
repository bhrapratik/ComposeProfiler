package io.github.bhrapratik.composeprofiler.sample.demos

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.bhrapratik.composeprofiler.TrackRecompositions
import kotlin.math.roundToInt

private val Travel = 240.dp

@Composable
fun AnimationDemo(fixed: Boolean) {
    TrackRecompositions("AnimationDemo")

    val transition = rememberInfiniteTransition(label = "ball")
    // Keep the State object; don't use `by` so we control WHERE .value is read
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "x",
    )

    Box(Modifier.fillMaxWidth().height(96.dp).padding(top = 24.dp)) {
        if (fixed) {
            // ✅ Value read inside the lambda → only the layout phase reruns, no recomposition
            val travelPx = with(LocalDensity.current) { Travel.toPx() }
            Ball(Modifier.offset { IntOffset((travelPx * progress.value).roundToInt(), 0) })
        } else {
            // ❌ Value read during composition → AnimationDemo and Ball recompose every frame
            Ball(Modifier.offset(x = Travel * progress.value))
        }
    }
}

@Composable
private fun Ball(modifier: Modifier) {
    TrackRecompositions("Ball")
    Box(modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
}