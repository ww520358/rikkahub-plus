package me.rerere.rikkahub.ui.pages.cluster

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Play
import me.rerere.hugeicons.stroke.Robot
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.theme.CustomColors

private data class ClusterItem(
    val id: String,
    val name: String,
    val mode: String,
    val members: Int,
    val result: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClusterPage() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var clusters by remember {
        mutableStateOf(
            listOf(
                ClusterItem("1", "Code Review Cluster", "Round-robin", 3, "No recent runs"),
                ClusterItem("2", "Translation Squad", "Parallel", 5, "Last: 42 segments processed"),
                ClusterItem("3", "Research Team", "Hierarchical", 2, "Summarizing...")
            )
        )
    }
    var expandedId by remember { mutableStateOf<String?>(null) }
    var runInput by remember { mutableStateOf("") }

    LaunchedEffect(expandedId) {
        // Prepare run area when expanded
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.cluster_page_title))
                },
                navigationIcon = {
                    BackButton()
                },
                scrollBehavior = scrollBehavior,
                colors = CustomColors.topBarColors
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CustomColors.topBarColors.containerColor
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(clusters, key = { it.id }) { cluster ->
                ClusterCard(
                    cluster = cluster,
                    isExpanded = expandedId == cluster.id,
                    onToggleExpand = {
                        expandedId = if (expandedId == cluster.id) null else cluster.id
                    },
                    runInput = if (expandedId == cluster.id) runInput else "",
                    onRunInputChange = { runInput = it },
                    onRun = {
                        clusters = clusters.map {
                            if (it.id == cluster.id) {
                                it.copy(result = "Running: $runInput...")
                            } else {
                                it
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ClusterCard(
    cluster: ClusterItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    runInput: String,
    onRunInputChange: (String) -> Unit,
    onRun: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onToggleExpand,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CustomColors.cardColors,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = HugeIcons.Robot,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cluster.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${cluster.mode} · ${cluster.members} members",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isExpanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = runInput,
                        onValueChange = onRunInputChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.cluster_run_hint)) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = onRun) {
                                Icon(
                                    imageVector = HugeIcons.Play,
                                    contentDescription = stringResource(R.string.cluster_run)
                                )
                            }
                        }
                    )
                    Text(
                        text = cluster.result,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
