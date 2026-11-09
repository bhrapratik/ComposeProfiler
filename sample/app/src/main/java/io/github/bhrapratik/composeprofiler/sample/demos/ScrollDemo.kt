package io.github.bhrapratik.composeprofiler.sample.demos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.bhrapratik.composeprofiler.TrackRecompositions
import kotlinx.coroutines.launch

@Composable
fun ScrollDemo(fixed: Boolean) {
    TrackRecompositions("ScrollDemo")
    val listState = rememberLazyListState()

    val showButton = if (fixed) {
        // ✅ Recomposes only when the Boolean flips, not on every scrolled pixel
        val derived by remember(listState) {
            derivedStateOf { listState.isScrolledPastTop() }
        }
        derived
    } else {
        // ❌ Reads scroll offset directly → ScrollDemo recomposes on every scroll frame
        listState.isScrolledPastTop()
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(count = 500, key = { it }) { i ->
                Text("Item $i", Modifier.fillMaxWidth().padding(16.dp))
                HorizontalDivider()
            }
        }
        ScrollToTopButton(
            visible = showButton,
            listState = listState,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }
}

private fun LazyListState.isScrolledPastTop(): Boolean =
    firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 200

@Composable
private fun ScrollToTopButton(visible: Boolean, listState: LazyListState, modifier: Modifier) {
    TrackRecompositions("ScrollToTopButton")
    val scope = rememberCoroutineScope()
    AnimatedVisibility(visible = visible, modifier = modifier) {
        ExtendedFloatingActionButton(onClick = { scope.launch { listState.animateScrollToItem(0) } }) {
            Text("↑ Top")
        }
    }
}