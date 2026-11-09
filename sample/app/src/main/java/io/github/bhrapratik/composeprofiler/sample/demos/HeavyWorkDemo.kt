package io.github.bhrapratik.composeprofiler.sample.demos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.bhrapratik.composeprofiler.TrackRecompositions
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun HeavyWorkDemo(fixed: Boolean) {
    TrackRecompositions("HeavyWorkDemo")

    // 100k random names, created once
    val allNames = remember {
        val random = Random(42)
        List(100_000) { "User-" + random.nextInt(1_000_000).toString().padStart(6, '0') }
    }

    // A clock that ticks 10×/s → this composable recomposes 10×/s
    var ticks by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            ticks++
        }
    }

    val sorted = if (fixed) {
        remember(allNames) { allNames.sorted() }   // ✅ sorted once, reused while the input is the same
    } else {
        allNames.sorted()                          // ❌ sorted again on every recomposition, on the main thread
    }

    Text("Clock: $ticks", style = MaterialTheme.typography.titleMedium)
    Text("Showing the first 50 of ${sorted.size} sorted names")
    LazyColumn {
        items(sorted.take(50)) { name ->
            Text(name, Modifier.fillMaxWidth().padding(vertical = 6.dp))
        }
    }
}