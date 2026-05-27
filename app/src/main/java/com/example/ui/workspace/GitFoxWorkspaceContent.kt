package com.example.ui.workspace

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GitFoxUiState
import com.example.ui.viewmodel.GitFoxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitFoxWorkspaceScreen(
    viewModel: GitFoxViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var isWorkspaceMenuExpanded by remember { mutableStateOf(false) }
    var isTerminalExpanded by remember { mutableStateOf(false) }
    var cliInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_gitfox_winged_logo),
                            contentDescription = "GitFox Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, CyberTeal.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GitFox",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "cloud",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberTeal.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                },
                actions = {
                    if (state.activeWorkspace != null) {
                        IconButton(
                            onClick = { viewModel.deleteActiveWorkspace() },
                            modifier = Modifier.testTag("delete_workspace_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Active Workspace",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.showIngestionDialog(true) },
                        modifier = Modifier.testTag("add_workspace_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ingest Stack",
                            tint = FoxOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateDark,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            // High comfort bottom segment switcher matching M3 guidelines
            NavigationBar(
                containerColor = Color(0xFFF3EDF7),
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = state.selectedSegment == "editor",
                    onClick = { viewModel.setUiSegment("editor") },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Editor Mode") },
                    label = { Text("Workspace IDE") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D),
                        selectedTextColor = Color(0xFF21005D),
                        indicatorColor = Color(0xFFEADDFF),
                        unselectedIconColor = NeutralSlate,
                        unselectedTextColor = NeutralSlate
                    ),
                    modifier = Modifier.testTag("nav_item_editor")
                )

                NavigationBarItem(
                    selected = state.selectedSegment == "preview",
                    onClick = { viewModel.setUiSegment("preview") },
                    icon = { Icon(Icons.Default.Monitor, contentDescription = "Live Preview") },
                    label = { Text("Live Preview") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D),
                        selectedTextColor = Color(0xFF21005D),
                        indicatorColor = Color(0xFFEADDFF),
                        unselectedIconColor = NeutralSlate,
                        unselectedTextColor = NeutralSlate
                    ),
                    modifier = Modifier.testTag("nav_item_preview")
                )

                NavigationBarItem(
                    selected = state.selectedSegment == "export",
                    onClick = { viewModel.setUiSegment("export") },
                    icon = { Icon(Icons.Default.Cloud, contentDescription = "Deployment") },
                    label = { Text("Deployment") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D),
                        selectedTextColor = Color(0xFF21005D),
                        indicatorColor = Color(0xFFEADDFF),
                        unselectedIconColor = NeutralSlate,
                        unselectedTextColor = NeutralSlate
                    ),
                    modifier = Modifier.testTag("nav_item_export")
                )

                NavigationBarItem(
                    selected = state.selectedSegment == "firebase",
                    onClick = { viewModel.setUiSegment("firebase") },
                    icon = { Icon(Icons.Default.Sync, contentDescription = "Firebase sync") },
                    label = { Text("Firebase Sync") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF21005D),
                        selectedTextColor = Color(0xFF21005D),
                        indicatorColor = Color(0xFFEADDFF),
                        unselectedIconColor = NeutralSlate,
                        unselectedTextColor = NeutralSlate
                    ),
                    modifier = Modifier.testTag("nav_item_firebase")
                )
            }
        },
        containerColor = SlateDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Workspace Info Panel / Selector Header
                WorkspaceSubHeader(
                    state = state,
                    onSelectorClick = { isWorkspaceMenuExpanded = true }
                )

                // Layout depends on the active segment choice
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (state.selectedSegment) {
                        "editor" -> WorkspaceIdePanel(
                            state = state,
                            viewModel = viewModel,
                            isTerminalExpanded = isTerminalExpanded,
                            onToggleTerminal = { isTerminalExpanded = !isTerminalExpanded },
                            cliInput = cliInput,
                            onCliInputChange = { cliInput = it },
                            onCliSubmit = {
                                viewModel.dispatchTerminalCommand(cliInput)
                                cliInput = ""
                                keyboardController?.hide()
                            }
                        )
                        "preview" -> PreviewCanvasPanel(state = state, viewModel = viewModel)
                        "export" -> ExportDeploymentPanel(state = state, viewModel = viewModel)
                        "firebase" -> FirebaseSyncPanel(state = state, viewModel = viewModel)
                    }
                }
            }

            // Embedded Workspace dropdown popup selector
            if (isWorkspaceMenuExpanded) {
                DropdownMenu(
                    expanded = isWorkspaceMenuExpanded,
                    onDismissRequest = { isWorkspaceMenuExpanded = false },
                    modifier = Modifier
                        .background(SlateElevated)
                        .border(1.dp, BorderGrey, RoundedCornerShape(4.dp))
                        .width(280.dp)
                ) {
                    Text(
                        text = "SELECT ACTIVE WORKSPACE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralSlate,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Divider(color = BorderGrey, thickness = 1.dp)
                    state.workspaces.forEach { ws ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = ws.name,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (state.activeWorkspace?.id == ws.id) FoxOrange else Color(0xFF1D1B20)
                                    )
                                    Text(
                                        text = "IP: ${ws.containerIp} • ${ws.type.uppercase()}",
                                        fontSize = 12.sp,
                                        color = NeutralSlate
                                    )
                                }
                            },
                            onClick = {
                                viewModel.selectWorkspace(ws)
                                isWorkspaceMenuExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (ws.type == "notebook") NotebookIcon else Icons.Default.Code,
                                    contentDescription = null,
                                    tint = if (state.activeWorkspace?.id == ws.id) FoxOrange else NeutralSlate
                                )
                            },
                            modifier = Modifier.testTag("ws_item_${ws.id}")
                        )
                    }
                }
            }

            // Ingestion Overlay form Modal Dialog
            if (state.ingestDialogShowing) {
                IngestionDialog(
                    state = state,
                    onDismiss = { viewModel.showIngestionDialog(false) },
                    onUpdateInput = { name, url, type -> viewModel.updateIngestionInput(name, url, type) },
                    onConfirm = { viewModel.executePlatformIngestion() }
                )
            }

            // Floating AI Assistant Button & Chat Panel overlays
            FloatingAssistantBlock(
                state = state,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun WorkspaceSubHeader(
    state: GitFoxUiState,
    onSelectorClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(1.dp, BorderGrey),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectorClick() }
                    .testTag("workspace_selector_trigger")
            ) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = "Workspace Container",
                    tint = FoxOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = state.activeWorkspace?.name ?: "Provisioning Stack...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = state.activeWorkspace?.let { "Ready: ${it.containerIp}" } ?: "Configuring stack environment...",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTeal
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop active workspaces",
                    tint = NeutralSlate
                )
            }

            // Small Live hot module loading status
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (state.isCompiling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = CyberTeal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HOT REBUILD",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTeal,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LimeGlow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CONTAINER ALIVE",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = LimeGlow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WorkspaceIdePanel(
    state: GitFoxUiState,
    viewModel: GitFoxViewModel,
    isTerminalExpanded: Boolean,
    onToggleTerminal: () -> Unit,
    cliInput: String,
    onCliInputChange: (String) -> Unit,
    onCliSubmit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal File System Explorer Bar
        FileSystemExplorerBar(
            state = state,
            onFileSelect = { viewModel.selectFile(it) }
        )

        Divider(color = BorderGrey, thickness = 1.dp)

        // Principal Code Content Zone
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SlateDark)
        ) {
            val selected = state.selectedFile
            if (selected == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No file selected in Workspace Cluster", color = NeutralSlate)
                }
            } else if (selected.isNotebook) {
                // Interactive Notebook view with cell structure
                NotebookLayoutView(
                    cells = state.notebookCells,
                    onExecuteCell = { cell, code -> viewModel.executeNotebookCell(cell, code) }
                )
            } else {
                // Standard file editor with interactive co-pilot engine
                CodeEditorLayoutView(
                    file = selected,
                    state = state,
                    viewModel = viewModel
                )
            }
        }

        // Persistent collapsible drawer CLI
        TerminalDrawerContainer(
            isExpanded = isTerminalExpanded,
            onToggle = onToggleTerminal,
            logs = state.terminalLogs,
            input = cliInput,
            onInputChange = onCliInputChange,
            onSubmit = onCliSubmit
        )
    }
}

@Composable
fun FileSystemExplorerBar(
    state: GitFoxUiState,
    onFileSelect: (WorkspaceFile) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateElevated)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        state.activeFiles.forEach { file ->
            val isActive = state.selectedFile?.id == file.id
            val fileColor = if (isActive) FoxOrange else Color(0xFF49454F)
            val fileBg = if (isActive) Color(0xFFF3EDF7) else Color.Transparent

            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(fileBg)
                    .clickable { onFileSelect(file) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when {
                        file.isNotebook -> NotebookIcon
                        file.name.endsWith(".json") -> Icons.Default.Settings
                        file.name.contains("toml") -> Icons.Default.Settings
                        else -> Icons.Default.FileOpen
                    },
                    contentDescription = null,
                    tint = if (isActive) FoxOrange else NeutralSlate,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = file.name,
                    color = fileColor,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun CodeEditorLayoutView(
    file: WorkspaceFile,
    state: GitFoxUiState,
    viewModel: GitFoxViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var contentInMemory by remember(file.id) { mutableStateOf(file.content) }
    var isModelMenuExpanded by remember { mutableStateOf(false) }

    // Dynamic model registry mapping aliases
    val modelList = listOf(
        "gemini-3.5-flash" to "Gemini 3.5 Flash (Core)",
        "gemini-3.1-pro-preview" to "Gemini 3.1 Pro (Coding)",
        "gemini-2.5-flash-image" to "Gemini 2.5 Creative (UI)",
        "gemini-3.1-flash-image-preview" to "Gemini 3.1 Ultra (Art)",
        "gemini-1.5-pro" to "Gemini 1.5 Pro (Legacy)",
        "gemini-1.5-flash" to "Gemini 1.5 Flash (Legacy)"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Toolbar Header with Actions ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateDark)
                .border(BorderStroke(1.dp, BorderGrey))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = file.path,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberTeal,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Toolbar Actions: Auto-Suggest Completion, Share, Download code
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Auto-Suggest Completion Sparkler
                IconButton(
                    onClick = { viewModel.generateCodeSuggestions(contentInMemory) },
                    modifier = Modifier.size(28.dp).testTag("action_auto_suggest")
                ) {
                    if (state.isAiSuggesting) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = FoxOrange)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Suggestion", tint = FoxOrange, modifier = Modifier.size(18.dp))
                    }
                }

                // Share snippet chooser
                IconButton(
                    onClick = { viewModel.generateShareCodeLink(context) },
                    modifier = Modifier.size(28.dp).testTag("action_share_code")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share Code Link", tint = CyberTeal, modifier = Modifier.size(18.dp))
                }

                // Download completed source
                IconButton(
                    onClick = { viewModel.downloadCurrentCodeFile() },
                    modifier = Modifier.size(28.dp).testTag("action_download_code")
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download Code File", tint = LimeGlow, modifier = Modifier.size(18.dp))
                }
            }
        }

        // --- Download Source Completed Toast Banner ---
        if (state.exportCodeSuccessMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEADDFF)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Download Success", tint = Color(0xFF21005D))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.exportCodeSuccessMessage,
                            fontSize = 12.sp,
                            color = Color(0xFF21005D),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = { viewModel.dismissDownloadMessage() }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF21005D))
                    }
                }
            }
        }

        // --- Google AI Model & Actions Orchestrator Panel ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(1.dp, BorderGrey),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Model Dropdown Selection Trigger
                    Box {
                        TextButton(
                            onClick = { isModelMenuExpanded = true },
                            modifier = Modifier.testTag("model_selector_button")
                        ) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = FoxOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = modelList.find { it.first == state.selectedModel }?.second ?: state.selectedModel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FoxOrange
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = FoxOrange)
                        }

                        DropdownMenu(
                            expanded = isModelMenuExpanded,
                            onDismissRequest = { isModelMenuExpanded = false },
                            modifier = Modifier.background(SlateElevated).border(1.dp, BorderGrey)
                        ) {
                            modelList.forEach { modelTuple ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            modelTuple.second, 
                                            fontSize = 12.sp,
                                            color = if (state.selectedModel == modelTuple.first) FoxOrange else Color(0xFF1D1B20)
                                        ) 
                                    },
                                    onClick = {
                                        viewModel.selectModel(modelTuple.first)
                                        isModelMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Quick AI Execution Action Chips: Modify, Search, Debug
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Action A: Modify
                        AssistChip(
                            onClick = { viewModel.executeAiAction("modify") },
                            label = { Text("Modify", fontSize = 11.sp, color = CyberTeal, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(12.dp)) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color.Transparent),
                            modifier = Modifier.testTag("ai_action_modify")
                        )

                        // Action B: Search
                        AssistChip(
                            onClick = { viewModel.executeAiAction("search") },
                            label = { Text("Search", fontSize = 11.sp, color = FoxOrange, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FoxOrange, modifier = Modifier.size(12.dp)) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color.Transparent),
                            modifier = Modifier.testTag("ai_action_search")
                        )

                        // Action C: Debug
                        AssistChip(
                            onClick = { viewModel.executeAiAction("debug") },
                            label = { Text("Debug", fontSize = 11.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp)) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color.Transparent),
                            modifier = Modifier.testTag("ai_action_debug")
                        )
                    }
                }
            }
        }

        // --- Live AI Suggestions Results Overlay Panel ---
        if (state.codeSuggestions.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                border = BorderStroke(1.dp, FoxOrange.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = FoxOrange)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LSP Code Completion Recommendations:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FoxOrange
                            )
                        }
                        IconButton(onClick = { viewModel.clearSuggestions() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = NeutralSlate, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    state.codeSuggestions.forEachIndexed { idx, sugg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateDark),
                            border = BorderStroke(1.dp, BorderGrey),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = sugg,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF34D399)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.acceptSuggestionCompletion(sugg) },
                                    colors = ButtonDefaults.buttonColors(containerColor = FoxOrange),
                                    modifier = Modifier.align(Alignment.End).height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text("Accept & Inject Snippet", fontSize = 11.sp, color = SlateDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Live AI Generation Result Card ---
        if (state.aiAssistantLoading || state.aiResponse != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                border = BorderStroke(1.dp, CyberTeal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .heightIn(max = 200.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp).verticalScroll(rememberScrollState())) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberTeal)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gemini AI Orchestrator Result:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal
                            )
                        }
                        IconButton(onClick = { viewModel.dismissAiAssistant() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = NeutralSlate, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.aiAssistantLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = CyberTeal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compiling AI response stream...", fontSize = 12.sp, color = NeutralSlate)
                        }
                    } else if (state.aiResponse != null) {
                        Text(
                            text = state.aiResponse,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF1D1B20)
                        )
                    }
                }
            }
        }

        // --- Main Text Code Input Layer ----
        Row(modifier = Modifier.weight(1f)) {
            // Line count vertical sidebar
            Column(
                modifier = Modifier
                    .width(42.dp)
                    .fillMaxHeight()
                    .background(SlateElevated)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val linesCount = contentInMemory.split("\n").size
                for (i in 1..linesCount) {
                    Text(
                        text = "$i",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NeutralSlate.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }

            Divider(modifier = Modifier.fillMaxHeight().width(1.dp), color = BorderGrey)

            // Code input container
            BasicTextField(
                value = contentInMemory,
                onValueChange = { newValue ->
                    contentInMemory = newValue
                    viewModel.editAndCompileFile(newValue)
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFF1D1B20),
                    lineHeight = 18.sp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .background(SlateDark)
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("code_input_field")
            )
        }
    }
}

@Composable
fun NotebookLayoutView(
    cells: List<NotebookCell>,
    onExecuteCell: (NotebookCell, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cells, key = { it.id }) { cell ->
            NotebookCellItem(cell = cell, onExecute = { onExecuteCell(cell, it) })
        }
    }
}

@Composable
fun NotebookCellItem(
    cell: NotebookCell,
    onExecute: (String) -> Unit
) {
    var codeValue by remember(cell.id) { mutableStateOf(cell.inputCode) }

    Card(
        border = BorderStroke(1.dp, if (cell.isRunning) CyberTeal else BorderGrey),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "In [${cell.cellIndex}]:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (cell.isRunning) CyberTeal else NeutralSlate
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = cell.type.uppercase(),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (cell.type == "code") CodePurple else NeutralSlate,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (cell.type == "code") CodePurple.copy(alpha = 0.15f) else BorderGrey)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                if (cell.type == "code") {
                    IconButton(
                        onClick = { onExecute(codeValue) },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("run_cell_${cell.cellIndex}")
                    ) {
                        if (cell.isRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = CyberTeal
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run Cell",
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Cell editor
            BasicTextField(
                value = codeValue,
                onValueChange = { newValue -> codeValue = newValue },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = if (cell.type == "markdown") Color(0xFF49454F) else Color(0xFF1D1B20)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateDark)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, BorderGrey, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            )

            // Cell computed output streams
            if (cell.outputText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateDark.copy(alpha = 0.5f))
                        .clip(RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Stdout Output:",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AmberColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cell.outputText,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF34D399)
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalDrawerContainer(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    logs: List<String>,
    input: String,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Column {
            // Interactive Terminal bar trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Terminal",
                        tint = CyberTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "VIRTUAL MICROSERVICE TERMINAL",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF1D1B20)
                        )
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = NeutralSlate
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(SlateDark)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Terminal logs lazy container
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            reverseLayout = false
                        ) {
                            items(logs) { log ->
                                Text(
                                    text = log,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = when {
                                        log.startsWith("developer") -> CyberTeal
                                        log.startsWith("🦊") -> FoxOrange
                                        log.contains("Error") || log.contains("Failed") -> Color(0xFFEF4444)
                                        else -> Color(0xFF49454F)
                                    },
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(4.dp))
                            .background(SlateElevated)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "fox@sandbox:~$ ",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyberTeal,
                            fontWeight = FontWeight.Bold
                        )
                        BasicTextField(
                            value = input,
                            onValueChange = onInputChange,
                            textStyle = TextStyle(
                                color = Color(0xFF1D1B20),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("terminal_input_field")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewCanvasPanel(
    state: GitFoxUiState,
    viewModel: GitFoxViewModel
) {
    val workspace = state.activeWorkspace

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            border = BorderStroke(1.dp, BorderGrey),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Monitor,
                            contentDescription = null,
                            tint = CyberTeal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Container Hot-Reload Stream",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20),
                            fontSize = 15.sp
                        )
                    }

                    Text(
                        text = "VIRTUAL DOM",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = LimeGlow,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(LimeGlow.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Render matching canvas preview based on the workspace type description
                when (workspace?.type) {
                    "frontend" -> ReactAppPreviewLayout(state)
                    "notebook" -> NotebookDataPlotsPreviewLayout()
                    else -> BackendRouteSandboxLayout(state, viewModel)
                }
            }
        }
    }
}

@Composable
fun ReactAppPreviewLayout(
    state: GitFoxUiState,
) {
    var clicksCount by remember { mutableStateOf(128) }
    var scaleFactor by remember { mutableStateOf(38) }
    var balancerMode by remember { mutableStateOf("Dynamic Balance") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDark)
            .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🦊 GitFox Live Virtual Host",
            color = CyberTeal,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp
        )
        Text(
            text = "Instance IP: ${state.activeWorkspace?.containerIp ?: "10.128.42.115"} • Active Ports: 8080/443",
            color = NeutralSlate,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        Divider(color = BorderGrey, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated Render Canvas
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            border = BorderStroke(1.dp, BorderGrey),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Interactive statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Host Status:", fontSize = 13.sp, color = Color(0xFF1D1B20))
                    Text("ACTIVE", fontSize = 13.sp, color = LimeGlow, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Active Connections:", fontSize = 13.sp, color = Color(0xFF1D1B20))
                    Text("$clicksCount nodes", fontSize = 13.sp, color = CodeYellow, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cluster Response Rate:", fontSize = 13.sp, color = Color(0xFF1D1B20))
                    Text("$scaleFactor fps", fontSize = 13.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("HMR Sync:", fontSize = 13.sp, color = Color(0xFF1D1B20))
                    Text(balancerMode, fontSize = 13.sp, color = FoxOrange, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                clicksCount += 35
                scaleFactor = (40..98).random()
                if (clicksCount > 240) {
                    balancerMode = "Throttling high workload"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simulation: Trigger Connection Spike", color = SlateDark, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = {
                clicksCount = 128
                scaleFactor = 38
                balancerMode = "Dynamic Balance"
            },
            border = BorderStroke(1.dp, BorderGrey),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeutralSlate),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Metrics")
        }
    }
}

@Composable
fun NotebookDataPlotsPreviewLayout() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDark)
            .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "matplotlib.pyplot output renderer",
            color = CodePurple,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Draw custom Canvas telemetry graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(SlateElevated)
                .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(4.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pathPoints = listOf(
                    Offset(0f, 300f),
                    Offset(100f, 260f),
                    Offset(200f, 290f),
                    Offset(300f, 150f),
                    Offset(400f, 180f),
                    Offset(500f, 110f),
                    Offset(600f, 140f),
                    Offset(700f, 40f)
                )

                for (i in 0 until pathPoints.size - 1) {
                    drawLine(
                        color = CyberTeal,
                        start = pathPoints[i],
                        end = pathPoints[i + 1],
                        strokeWidth = 3.dp.toPx()
                    )
                }

                // Draw labels / grid lines
                drawLine(
                    color = BorderGrey,
                    start = Offset(0f, 150f),
                    end = Offset(size.width, 150f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Generated linear regression plot mapping container latency over dynamic data science computational cells.",
            fontSize = 12.sp,
            color = NeutralSlate,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun BackendRouteSandboxLayout(
    state: GitFoxUiState,
    viewModel: GitFoxViewModel
) {
    var routeTested by remember { mutableStateOf("/api/health") }
    var responseOutput by remember { mutableStateOf("") }
    var testingInProgress by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateDark)
            .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "REST Endpoint Verification sandbox",
            color = CodeYellow,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = routeTested,
            onValueChange = { routeTested = it },
            label = { Text("Route Pattern") },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = Color(0xFF1D1B20)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF1D1B20),
                unfocusedTextColor = Color(0xFF1D1B20),
                focusedBorderColor = FoxOrange,
                unfocusedBorderColor = BorderGrey
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                testingInProgress = true
                viewModel.dispatchTerminalCommand("curl -X GET $routeTested")
                responseOutput = "HTTP/1.1 200 OK\nContent-Type: application/json\n\n{\n  \"route\": \"$routeTested\",\n  \"status\": \"healthy\",\n  \"uptime\": \"3059s\"\n}"
                testingInProgress = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = FoxOrange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send API Sandbox Request (curl)", fontWeight = FontWeight.Bold)
        }

        if (responseOutput.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateElevated)
                    .clip(RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Sandbox Response:",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = LimeGlow
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = responseOutput,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF1D1B20)
                )
            }
        }
    }
}

@Composable
fun ExportDeploymentPanel(
    state: GitFoxUiState,
    viewModel: GitFoxViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Packaging & Deployment Center",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20)
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Convert active live repositories and workspaces into production wrappers instantly.",
            style = TextStyle(
                fontSize = 13.sp,
                color = NeutralSlate,
                lineHeight = 18.sp
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Rendering progress indicator during compiler phase
        if (state.isExporting) {
            Card(
                border = BorderStroke(1.dp, BorderGrey),
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "COMPILING BUNDLE TARGETS...",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = state.exportProgress,
                        color = CyberTeal,
                        trackColor = BorderGrey,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(state.exportProgress * 100).toInt()}% loaded",
                        color = Color(0xFF1D1B20),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (state.exportFinished) {
            Card(
                border = BorderStroke(2.dp, LimeGlow),
                colors = CardDefaults.cardColors(containerColor = SlateElevated),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Succeed", tint = LimeGlow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BUILD SUCCESSFUL", fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Standalone files synthesized. Here are your downloadable targets ready to execute:",
                        fontSize = 13.sp,
                        color = NeutralSlate
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateDark)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Download Links:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CyberTeal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Windows executable: gitfox-module.msi",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF1D1B20)
                        )
                        Text(
                            text = "macOS Bundle: gitfox-module.dmg",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF1D1B20)
                        )
                        Text(
                            text = "Docker Image ID: sha256:7bde93e0aa025",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF1D1B20)
                        )
                    }
                }
            }
        }

        // Action Options Cards
        ExportCardOption(
            title = "Download as Desktop App (Tauri)",
            description = "Uses lightweight Rust Tauri wrappers to package current workspace into native desktop programs for Windows and macOS with one click.",
            actionText = "Synthesize Tauri Binary",
            icon = Icons.Default.Computer,
            color = FoxOrange,
            onClick = { viewModel.launchWorkspaceExport("tauri") }
        )

        Spacer(modifier = Modifier.height(14.dp))

        ExportCardOption(
            title = "Export as Docker Container",
            description = "Compile workspace files and stateful environments into structured micro-containers perfectly matching production environments.",
            actionText = "Assemble Docker Hub Image",
            icon = Icons.Default.Cloud,
            color = CyberTeal,
            onClick = { viewModel.launchWorkspaceExport("docker") }
        )

        Spacer(modifier = Modifier.height(14.dp))

        ExportCardOption(
            title = "Publish Instant Web link",
            description = "Securely generate and copy dynamic sandbox web address so anyone can experiment with your running live DOM tree elements.",
            actionText = "Generate Sandbox URL",
            icon = Icons.Default.Share,
            color = LimeGlow,
            onClick = { viewModel.launchWorkspaceExport("web_link") }
        )
    }
}

@Composable
fun ExportCardOption(
    title: String,
    description: String,
    actionText: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        border = BorderStroke(1.dp, BorderGrey),
        colors = CardDefaults.cardColors(containerColor = SlateElevated),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20), fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = NeutralSlate,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = actionText, color = SlateDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun IngestionDialog(
    state: GitFoxUiState,
    onDismiss: () -> Unit,
    onUpdateInput: (String, String, String) -> Unit,
    onConfirm: () -> Unit
) {
    var rawName by remember { mutableStateOf(state.ingestedName) }
    var rawUrl by remember { mutableStateOf(state.ingestedUrl) }
    var selectedType by remember { mutableStateOf(state.ingestedType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Universal Ingestion Engine", color = Color(0xFF1D1B20)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Seamlessly import Git repositories, computational notebooks or systems codes from GitHub / Kaggle / Google Colab.",
                    fontSize = 12.sp,
                    color = NeutralSlate
                )

                OutlinedTextField(
                    value = rawName,
                    onValueChange = {
                        rawName = it
                        onUpdateInput(it, rawUrl, selectedType)
                    },
                    label = { Text("Workspace Name") },
                    textStyle = TextStyle(color = Color(0xFF1D1B20)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1D1B20),
                        unfocusedTextColor = Color(0xFF1D1B20),
                        focusedBorderColor = FoxOrange,
                        unfocusedBorderColor = BorderGrey
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ingest_name_input")
                )

                OutlinedTextField(
                    value = rawUrl,
                    onValueChange = {
                        rawUrl = it
                        onUpdateInput(rawName, it, selectedType)
                    },
                    label = { Text("Respository / Notebook URL") },
                    textStyle = TextStyle(color = Color(0xFF1D1B20)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1D1B20),
                        unfocusedTextColor = Color(0xFF1D1B20),
                        focusedBorderColor = FoxOrange,
                        unfocusedBorderColor = BorderGrey
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ingest_url_input")
                )

                Text(
                    text = "DETECTED ENVIROMENT STACK TYPE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NeutralSlate
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("frontend", "notebook", "backend").forEach { tp ->
                        val isSel = selectedType == tp
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) FoxOrange else SlateElevated
                            ),
                            border = BorderStroke(1.dp, if (isSel) FoxOrange else BorderGrey),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedType = tp
                                    onUpdateInput(rawName, rawUrl, tp)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tp.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) SlateDark else Color(0xFF1D1B20)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = FoxOrange)
            ) {
                Text("Provision Stack Instance", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NeutralSlate)
            }
        },
        containerColor = SlateElevated
    )
}

@Composable
fun FloatingAssistantBlock(
    state: GitFoxUiState,
    viewModel: GitFoxViewModel
) {
    var chatOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = chatOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .width(320.dp)
                .height(420.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(12.dp))
                .background(SlateElevated)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Assistant top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateDark)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(LimeGlow)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🦊 GitFox AI Co-Pilot",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20),
                            fontSize = 14.sp
                        )
                    }

                    IconButton(
                        onClick = { chatOpen = false },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NeutralSlate)
                    }
                }

                Divider(color = BorderGrey)

                // Assistant dialog box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "I am your integrated architectural co-pilot. I can evaluate your current code files and assist in diagnosing kernel terminal output streams.",
                        fontSize = 12.sp,
                        color = NeutralSlate,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.selectedFile == null) {
                        Text(
                            text = "Please open a repository file in the editor to activate Gemini diagnostics.",
                            color = CodeYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Column {
                            Text(
                                text = "Evaluating current open module:",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyberTeal
                            )
                            Text(
                                text = "Context: ${state.selectedFile.name}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.executeAiAssistantRefactor() },
                                colors = ButtonDefaults.buttonColors(containerColor = FoxOrange),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (state.aiAssistantLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = SlateDark)
                                } else {
                                    Text("Analyze Code with Gemini", color = SlateDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (state.aiResponse != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateDark)
                                .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Gemini Engine Insights:",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = LimeGlow,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.aiResponse,
                                fontSize = 12.sp,
                                color = Color(0xFF1D1B20),
                                lineHeight = 16.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { chatOpen = !chatOpen },
            containerColor = FoxOrange,
            contentColor = SlateDark,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("ai_helper_fab")
        ) {
            Icon(
                imageVector = if (chatOpen) Icons.Default.Close else Icons.Default.AutoAwesome,
                contentDescription = "Trigger AI Assistant"
            )
        }
    }
}

// Colors
val AmberColor = Color(0xFFFFBF00)
val NotebookIcon = Icons.Filled.MenuBook

@Composable
fun FirebaseSyncPanel(
    state: GitFoxUiState,
    viewModel: GitFoxViewModel
) {
    var emailInput by remember { mutableStateOf("pm.rajitha@gmail.com") }
    var passwordInput by remember { mutableStateOf("gitfox-master-secure") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Identity Node ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FoxOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = FoxOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "GitFox Cloud Synchronizer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = "Real-time Firebase Firestore DB Orchestration Engine",
                        fontSize = 12.sp,
                        color = NeutralSlate
                    )
                }
            }
        }

        // --- Sub-Section 1: Firebase Authentication Interface ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            border = BorderStroke(1.dp, BorderGrey),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Firebase Auth Single-Sign-On Manager",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                }

                Divider(color = BorderGrey)

                if (state.firebaseAuthEmail == null) {
                    // Sign-In Form State
                    Text(
                        text = "Authenticate this local sandbox with GitFox's cloud credentials to unlock continuous Firestore schema listener pipelines and user space DB nodes.",
                        fontSize = 12.sp,
                        color = NeutralSlate
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Developer Email") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("firebase_email_input")
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Firestore Token Prefix") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("firebase_token_input")
                    )

                    Button(
                        onClick = { viewModel.signInToFirebase(emailInput, passwordInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("btn_firebase_signin"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (state.isFirebaseSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = SlateDark, strokeWidth = 2.dp)
                        } else {
                            Text("Connect & Trigger Firebase Handshake", fontSize = 13.sp, color = SlateDark, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Active Authorized Session Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberTeal.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LimeGlow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTH SESSION ACTIVE",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = LimeGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Email Account: ${state.firebaseAuthEmail}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1D1B20)
                        )
                        Text(
                            text = "Firestore Access Token: ${state.firebaseAuthToken}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeutralSlate
                        )
                    }

                    Button(
                        onClick = { viewModel.signOutFromFirebase() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sign Out & Disconnect", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- Sub-Section 2: Firestore Live Database Synchronization Sync ---
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateElevated),
            border = BorderStroke(1.dp, BorderGrey),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = FoxOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Firestore DB Sync Engine Manager",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                }

                Divider(color = BorderGrey)

                Text(
                    text = "Backup current high-fidelity local workspace repositories with your Cloud Firestore, making schemas accessible from external deployment APIs automatically.",
                    fontSize = 12.sp,
                    color = NeutralSlate
                )

                // Mock schema map rows
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateDark, RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "Collection" to "Status" to "Mocks Schemas Count",
                        "/workspaces" to "Up-to-Date" to "${state.workspaces.size} JSON Nodes",
                        "/workspace_files" to "Up-to-Date" to "${state.activeFiles.size} Files",
                        "/notebook_cells" to "Up-to-Date" to "12 active cells"
                    ).forEachIndexed { idx, row ->
                        val (left, right) = row.first
                        val count = row.second
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                left,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (idx == 0) NeutralSlate else Color(0xFF1D1B20)
                            )
                            Row {
                                Text(
                                    right,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (idx == 0) NeutralSlate else LimeGlow
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    count,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (idx == 0) NeutralSlate else CyberTeal,
                                    fontWeight = if (idx == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.syncDatabaseToFirestore() },
                    enabled = state.firebaseAuthEmail != null && !state.isFirebaseSyncing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FoxOrange,
                        disabledContainerColor = NeutralSlate.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("btn_firebase_sync"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (state.isFirebaseSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = SlateDark, strokeWidth = 2.dp)
                    } else {
                        val isEnabled = state.firebaseAuthEmail != null
                        Text(
                            text = if (isEnabled) "Push Changes to Firestore Live" else "Connect Auth First To Sync DB",
                            fontSize = 13.sp,
                            color = if (isEnabled) SlateDark else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
