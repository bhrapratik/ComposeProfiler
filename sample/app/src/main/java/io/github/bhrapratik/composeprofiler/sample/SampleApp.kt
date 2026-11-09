package io.github.bhrapratik.composeprofiler.sample

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.bhrapratik.composeprofiler.sample.demos.AnimationDemo
import io.github.bhrapratik.composeprofiler.sample.demos.HeavyWorkDemo
import io.github.bhrapratik.composeprofiler.sample.demos.ScrollDemo
import io.github.bhrapratik.composeprofiler.sample.demos.StabilityDemo

enum class Demo(val title: String, val description: String) {
    Stability(
        "Unstable parameters",
        "New unstable instances on every recomposition stop Compose from skipping. Watch the Components tab.",
    ),
    Scroll(
        "Scroll state reads",
        "Reading the scroll offset in composition recomposes on every scroll frame. Scroll, then check Components.",
    ),
    HeavyWork(
        "Heavy work in composition",
        "Sorting 100k items on every recomposition blocks the main thread. Watch the Frames tab turn red.",
    ),
    Animation(
        "Animation state reads",
        "Reading an animated value in composition recomposes every frame. A lambda modifier defers it to layout.",
    ),
}

@Composable
fun SampleApp() {
    var current by rememberSaveable { mutableStateOf<Demo?>(null) }
    var fixed by rememberSaveable { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize()) {
        val demo = current
        if (demo == null) {
            DemoList(onOpen = { current = it; fixed = false })
        } else {
            BackHandler { current = null }
            DemoScaffold(
                demo = demo,
                fixed = fixed,
                onFixedChange = { fixed = it },
                onBack = { current = null },
            ) {
                when (demo) {
                    Demo.Stability -> StabilityDemo(fixed)
                    Demo.Scroll -> ScrollDemo(fixed)
                    Demo.HeavyWork -> HeavyWorkDemo(fixed)
                    Demo.Animation -> AnimationDemo(fixed)
                }
            }
        }
    }
}

@Composable
private fun DemoList(onOpen: (Demo) -> Unit) {
    Column(
        Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Compose Profiler", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Pick a demo, use the switch to compare Slow and Fixed, and tap Reset in the profiler between runs.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(4.dp))
        Demo.entries.forEach { demo ->
            Card(Modifier.fillMaxWidth().clickable { onOpen(demo) }) {
                Column(Modifier.padding(16.dp)) {
                    Text(demo.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(demo.description, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun DemoScaffold(
    demo: Demo,
    fixed: Boolean,
    onFixedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Back") }
            Text(demo.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Text(demo.description, style = MaterialTheme.typography.bodyMedium)
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (fixed) "Fixed ✅" else "Slow ❌",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = fixed, onCheckedChange = onFixedChange)
        }
        content()
    }
}