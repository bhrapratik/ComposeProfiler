package io.github.bhrapratik.composeprofiler.sample.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.bhrapratik.composeprofiler.TrackRecompositions
import io.github.bhrapratik.composeprofiler.trackSize

/** `var` + not a data class → unstable, equals() is identity. */
class Transaction(var title: String)

@Composable
fun StabilityDemo(fixed: Boolean) {
    TrackRecompositions("StabilityDemo")

    var clicks by remember { mutableIntStateOf(0) }
    val rememberedItems = remember {
        listOf(Transaction("Coffee"), Transaction("Train"), Transaction("Groceries"))
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = { clicks++ }) { Text("Click me") }
        Text("Total clicks: $clicks")      // read here → StabilityDemo recomposes on each click

        BalanceCard(balance = 12_450)       // stable Int → always skips

        TransactionSummary(
            items = if (fixed) {
                rememberedItems                                        // ✅ same instances
            } else {
                listOf(Transaction("Coffee"), Transaction("Train"), Transaction("Groceries"))  // ❌ new
            },
        )
    }
}

@Composable
private fun BalanceCard(balance: Int) {
    TrackRecompositions("BalanceCard")
    Card(Modifier.fillMaxWidth().trackSize("BalanceCard")) {
        Text("Balance: $balance kr", Modifier.padding(24.dp), style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun TransactionSummary(items: List<Transaction>) {
    TrackRecompositions("TransactionSummary")
    Card(Modifier.fillMaxWidth().trackSize("TransactionSummary")) {
        Column(Modifier.padding(16.dp)) { items.forEach { Text(it.title) } }
    }
}