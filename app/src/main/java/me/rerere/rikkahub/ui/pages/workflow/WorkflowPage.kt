package me.rerere.rikkahub.ui.pages.workflow

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import me.rerere.hugeicons.stroke.Add01
import me.rerere.hugeicons.stroke.Delete01
import me.rerere.hugeicons.stroke.Edit01
import me.rerere.hugeicons.stroke.Play
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.theme.CustomColors

private data class WorkflowNode(
    val id: String,
    val type: String,
    val summary: String,
    val params: Map<String, String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowPage() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var nodes by remember {
        mutableStateOf(
            listOf(
                WorkflowNode(
                    "1",
                    "Prompt",
                    "System prompt setup",
                    mapOf("text" to "You are a helpful assistant")
                ),
                WorkflowNode(
                    "2",
                    "LLM Call",
                    "Generate response",
                    mapOf("model" to "gpt-4o")
                ),
                WorkflowNode(
                    "3",
                    "Output",
                    "Format and return",
                    mapOf("format" to "markdown")
                )
            )
        )
    }
    var workflowInput by remember { mutableStateOf("") }
    var editingNode by remember { mutableStateOf<String?>(null) }
    var editParamKey by remember { mutableStateOf("") }
    var editParamValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Initialize workflow state
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.workflow_page_title))
                },
                navigationIcon = {
                    BackButton()
                },
                scrollBehavior = scrollBehavior,
                colors = CustomColors.topBarColors
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newId = (nodes.size + 1).toString()
                    nodes = nodes + WorkflowNode(newId, "New Node", "Untitled", emptyMap())
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = HugeIcons.Add01,
                    contentDescription = stringResource(R.string.workflow_add_node)
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CustomColors.topBarColors.containerColor
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(nodes, key = { it.id }) { node ->
                    WorkflowNodeCard(
                        node = node,
                        onEdit = {
                            editingNode = node.id
                            editParamKey = node.params.keys.firstOrNull() ?: ""
                            editParamValue = node.params[editParamKey] ?: ""
                        },
                        onDelete = {
                            nodes = nodes.filter { it.id != node.id }
                        }
                    )
                }
            }

            if (editingNode != null) {
                val node = nodes.find { it.id == editingNode }
                if (node != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.workflow_edit_node, node.type),
                            style = MaterialTheme.typography.titleSmall
                        )
                        OutlinedTextField(
                            value = editParamKey,
                            onValueChange = { editParamKey = it },
                            label = { Text(stringResource(R.string.workflow_param_key)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editParamValue,
                            onValueChange = { editParamValue = it },
                            label = { Text(stringResource(R.string.workflow_param_value)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { editingNode = null }) {
                                Text(stringResource(R.string.workflow_cancel))
                            }
                            TextButton(onClick = {
                                nodes = nodes.map {
                                    if (it.id == editingNode) {
                                        val newParams = it.params.toMutableMap()
                                        newParams[editParamKey] = editParamValue
                                        it.copy(params = newParams)
                                    } else {
                                        it
                                    }
                                }
                                editingNode = null
                            }) {
                                Text(stringResource(R.string.workflow_save))
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = workflowInput,
                    onValueChange = { workflowInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.workflow_input_hint)) },
                    singleLine = true
                )
                FloatingActionButton(
                    onClick = { /* run workflow */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = HugeIcons.Play,
                        contentDescription = stringResource(R.string.workflow_run)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkflowNodeCard(
    node: WorkflowNode,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CustomColors.cardColors,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.type,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = node.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (node.params.isNotEmpty()) {
                    Text(
                        text = node.params.entries.joinToString { "${it.key}=${it.value}" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = HugeIcons.Edit01,
                    contentDescription = stringResource(R.string.workflow_edit)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = HugeIcons.Delete01,
                    contentDescription = stringResource(R.string.workflow_delete)
                )
            }
        }
    }
}
