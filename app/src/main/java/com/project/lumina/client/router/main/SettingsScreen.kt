/*
 * © Project Lumina 2025 — Licensed under GNU GPLv3
 * You are free to use, modify, and redistribute this code under the terms
 * of the GNU General Public License v3. See the LICENSE file for details.
 */

package com.project.lumina.client.router.main

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.lumina.client.essentials.NetworkOptimizer
import com.project.lumina.client.viewmodel.MainScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.Socket

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val captureModeModel by mainScreenViewModel.captureModeModel.collectAsState()

    
    val sharedPreferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)

    
    var optimizeNetworkEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("optimizeNetworkEnabled", false))
    }
    var priorityThreadsEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("priorityThreadsEnabled", false))
    }
    var fastDnsEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("fastDnsEnabled", false))
    }
    var injectNekoPackEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("injectNekoPackEnabled", false))
    }
    var selectedGUI by remember {
        mutableStateOf(sharedPreferences.getString("selectedGUI", "OverlayClickGUI") ?: "OverlayClickGUI")
    }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showServerConfigDialog by remember { mutableStateOf(false) }

    var serverIp by remember { mutableStateOf(captureModeModel.serverHostName) }
    var serverPort by remember { mutableStateOf(captureModeModel.serverPort.toString()) }

    
    fun saveToggleState(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun saveGUISelection(value: String) {
        with(sharedPreferences.edit()) {
            putString("selectedGUI", value)
            apply()
        }
    }

    
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        
                        DropdownMenu(
                            modifier = Modifier.fillMaxWidth(),
                            options = listOf("GraceGUI", "OverlayClickGUI", "ProtohaxUi"),
                            selectedOption = selectedGUI,
                            onOptionSelected = { newSelection ->
                                selectedGUI = newSelection
                                saveGUISelection(newSelection)
                            }
                        )

                        Divider()

                        
                        SettingToggle(
                            title = "Optimize Network",
                            description = "Initialize network optimization and configure sockets for better performance",
                            checked = optimizeNetworkEnabled,
                            onCheckedChange = { isEnabled ->
                                if (isEnabled) {
                                    scope.launch(Dispatchers.IO) {
                                        val success = NetworkOptimizer.init(context)
                                        if (success) {
                                            optimizeNetworkEnabled = true
                                            saveToggleState("optimizeNetworkEnabled", true)
                                            val socket = Socket()
                                            NetworkOptimizer.optimizeSocket(socket)
                                        } else {
                                            
                                            scope.launch(Dispatchers.Main) {
                                                showPermissionDialog = true
                                            }
                                        }
                                    }
                                } else {
                                    optimizeNetworkEnabled = false
                                    saveToggleState("optimizeNetworkEnabled", false)
                                }
                            }
                        )

                        Divider()

                        
                        SettingToggle(
                            title = "High Priority Threads",
                            description = "Set application threads to foreground priority for better performance",
                            checked = priorityThreadsEnabled,
                            onCheckedChange = { isEnabled ->
                                priorityThreadsEnabled = isEnabled
                                saveToggleState("priorityThreadsEnabled", isEnabled)
                                scope.launch(Dispatchers.IO) {
                                    if (isEnabled) {
                                        NetworkOptimizer.setThreadPriority()
                                    }
                                }
                            }
                        )

                        Divider()

                        
                        SettingToggle(
                            title = "Use Fast DNS",
                            description = "Use Google's DNS servers for faster name resolution",
                            checked = fastDnsEnabled,
                            onCheckedChange = { isEnabled ->
                                fastDnsEnabled = isEnabled
                                saveToggleState("fastDnsEnabled", isEnabled)
                                scope.launch(Dispatchers.IO) {
                                    if (isEnabled) {
                                        NetworkOptimizer.useFastDNS()
                                    }
                                }
                            }
                        )

                        Divider()

                        
                        SettingToggle(
                            title = "Inject Neko Pack",
                            description = "Enable injection of Neko pack for enhanced features",
                            checked = injectNekoPackEnabled,
                            onCheckedChange = { isEnabled ->
                                injectNekoPackEnabled = isEnabled
                                saveToggleState("injectNekoPackEnabled", isEnabled)
                            }
                        )
                    }
                }
            }

            
            Column(
                modifier = Modifier
                    .weight(0.8f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Server Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Configure server IP address and port",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "IP: $serverIp",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Port: $serverPort",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Button(
                                onClick = { showServerConfigDialog = true },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Config")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onRequestPermission = {
                NetworkOptimizer.openWriteSettingsPermissionPage(context)
                showPermissionDialog = false
            }
        )
    }

    if (showServerConfigDialog) {
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ServerConfigDialog(
                initialIp = serverIp,
                initialPort = serverPort,
                onDismiss = { showServerConfigDialog = false },
                onSave = { ip, port ->
                    serverIp = ip
                    serverPort = port
                    showServerConfigDialog = false

                    try {
                        val portInt = port.toInt()
                        mainScreenViewModel.selectCaptureModeModel(
                            captureModeModel.copy(serverHostName = ip, serverPort = portInt)
                        )
                        Toast.makeText(context, "Server configuration updated", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Invalid port number", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun ServerConfigDialog(
    initialIp: String,
    initialPort: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var ip by remember { mutableStateOf(initialIp) }
    var port by remember { mutableStateOf(initialPort) }
    val coroutineScope = rememberCoroutineScope()

    
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isCompactScreen = screenWidth < 600.dp

    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dialogScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "dialogAlpha"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "dialogOffsetY"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f * alpha.coerceIn(0f, 1f)))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    isVisible = false
                    delay(300)
                    onDismiss()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        
        Card(
            modifier = Modifier
                .widthIn(min = 280.dp, max = min(screenWidth * 0.9f, 400.dp))
                .heightIn(max = screenHeight * 0.8f)
                .scale(scale)
                .alpha(alpha)
                .offset(y = offsetY.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume click to prevent propagation */ },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(if (isCompactScreen) 16.dp else 24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Server Configuration",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip = it },
                        label = { Text("IP Address") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("Port") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isVisible = false
                                    delay(300)
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel"
                                )
                                Text("Cancel")
                            }
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isVisible = false
                                    delay(300)
                                    onSave(ip, port)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Apply"
                                )
                                Text("Apply")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = { Text("Network optimization requires special permissions. Would you like to open settings to grant them?") },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun DropdownMenu(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    Column(modifier = modifier) {
        Text(
            text = "Overlay GUI",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { expanded = !expanded },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedOption,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.rotate(rotationState)
                    )
                }
            }

            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(240.dp) 
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedOption
                    
                    androidx.compose.material3.DropdownMenuItem(
                        text = { 
                            Text(
                                text = option,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            ) 
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        trailingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier.background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) 
                            else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}