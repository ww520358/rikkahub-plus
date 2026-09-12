package me.rerere.rikkahub.ui.pages.server

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.hugeicons.stroke.Folder01
import me.rerere.hugeicons.stroke.Terminal
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.theme.CustomColors

private data class FileItem(
    val name: String,
    val isDirectory: Boolean,
    val size: String
)

private data class TerminalLine(
    val content: String,
    val isError: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerDetailPage(serverId: Int) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.server_detail_tab_files),
        stringResource(R.string.server_detail_tab_terminal)
    )

    var currentPath by remember { mutableStateOf("/home/user") }
    var files by remember {
        mutableStateOf(
            listOf(
                FileItem("documents", true, "-"),
                FileItem("downloads", true, "-"),
                FileItem("file.txt", false, "12 KB"),
                FileItem("script.sh", false, "4 KB")
            )
        )
    }
    var terminalLines by remember {
        mutableStateOf(
            listOf(
                TerminalLine("Welcome to remote terminal"),
                TerminalLine("$ ls -la"),
                TerminalLine("total 128"),
                TerminalLine("drwxr-xr-x  5 user user 4096 Sep 10 10:00 ."),
            )
        )
    }
    var commandInput by remember { mutableStateOf("") }

    LaunchedEffect(serverId) {
        // Simulate loading server details
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.server_detail_page_title))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> FileTab(
                    currentPath = currentPath,
                    files = files,
                    onPathChange = { currentPath = it },
                    modifier = Modifier.weight(1f)
                )

                1 -> TerminalTab(
                    lines = terminalLines,
                    input = commandInput,
                    onInputChange = { commandInput = it },
                    onSend = {
                        if (commandInput.isNotBlank()) {
                            terminalLines = terminalLines + TerminalLine("$ $commandInput")
                            terminalLines = terminalLines + TerminalLine("Executed: $commandInput")
                            commandInput = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FileTab(
    currentPath: String,
    files: List<FileItem>,
    onPathChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { /* navigate up */ }) {
                    Icon(
                        imageVector = HugeIcons.ArrowLeft01,
                        contentDescription = stringResource(R.string.server_detail_navigate_up)
                    )
                }
                Text(
                    text = currentPath,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(files, key = { it.name }) { file ->
                FileRow(
                    file = file,
                    onClick = {
                        if (file.isDirectory) {
                            onPathChange("$currentPath/${file.name}")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FileRow(
    file: FileItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (file.isDirectory) HugeIcons.Folder01 else HugeIcons.Terminal,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!file.isDirectory) {
                Text(
                    text = file.size,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TerminalTab(
    lines: List<TerminalLine>,
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(lines) { line ->
                Text(
                    text = line.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (line.isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(stringResource(R.string.server_detail_terminal_hint)) },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onSend) {
                    Icon(
                        imageVector = HugeIcons.Terminal,
                        contentDescription = stringResource(R.string.server_detail_send_command)
                    )
                }
            }
        )
    }
}
