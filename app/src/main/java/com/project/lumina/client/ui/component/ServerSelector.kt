/*
 * © Project Lumina 2025 — Licensed under GNU GPLv3
 * You are free to use, modify, and redistribute this code under the terms
 * of the GNU General Public License v3. See the LICENSE file for details.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * This is open source — not open credit.
 *
 * If you're here to build, welcome. If you're here to repaint and reupload
 * with your tag slapped on it… you're not fooling anyone.
 *
 * Changing colors and class names doesn't make you a developer.
 * Copy-pasting isn't contribution.
 *
 * You have legal permission to fork. But ask yourself — are you improving,
 * or are you just recycling someone else's work to feed your ego?
 *
 * Open source isn't about low-effort clones or chasing clout.
 * It's about making things better. Sharper. Cleaner. Smarter.
 *
 * So go ahead, fork it — but bring something new to the table,
 * or don't bother pretending.
 *
 * This message is philosophical. It does not override your legal rights under GPLv3.
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * GPLv3 Summary:
 * - You have the freedom to run, study, share, and modify this software.
 * - If you distribute modified versions, you must also share the source code.
 * - You must keep this license and copyright intact.
 * - You cannot apply further restrictions — the freedom stays with everyone.
 * - This license is irrevocable, and applies to all future redistributions.
 *
 * Full text: https://www.gnu.org/licenses/gpl-3.0.html
 */

package com.project.lumina.client.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.lumina.client.ui.theme.PColorItem1
import com.project.lumina.client.viewmodel.MainScreenViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

data class Server(
    val name: String,
    val serverAddress: String,
    val port: Int = 19132,
    val onClick: () -> Unit
)

data class SubServerInfo(
    val id: String,
    val region: String,
    val serverAddress: String,
    val serverPort: Int
)

@Composable
fun ServerSelector() {
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val captureModeModel by mainScreenViewModel.captureModeModel.collectAsState()


    val rawServers = listOf(
        Triple("2b2tpe", "2b2tpe.org", 19132),
        Triple("Sega MC", "segamc.net", 19132),
        Triple("The Hive", "geo.hivebedrock.network", 19132),
        Triple("Lifeboat MC", "play.lbsg.net", 19132),
        Triple("Nether Games", "ap.nethergames.org", 19132),
        Triple("Cube Craft", "play.cubecraft.net", 19132),
        Triple("Galaxite", "play.galaxite.net", 19132),
        Triple("Zeqa MC", "zeqa.net", 19132),
        Triple("Vanity", "play.venitymc.com", 19132),
        Triple("PixelBlock", "buzz.pixelblockmc.com", 19132)
    )

    val servers = rawServers.map { (name, address, port) ->
        Server(name, address, port) {
            mainScreenViewModel.selectCaptureModeModel(
                captureModeModel.copy(serverHostName = address, serverPort = port)
            )
        }
    }

    var selectedServer by remember { mutableStateOf<Server?>(null) }
    var showZeqaDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            items(servers) { server ->
                val isSelected = server == selectedServer ||
                        captureModeModel.serverHostName == server.serverAddress

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable {
                            if (server.name == "Zeqa MC") {
                                showZeqaDialog = true
                            } else {
                                selectedServer = server
                                server.onClick()
                            }
                        }
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) PColorItem1 else Color.Transparent,
                            shape = MaterialTheme.shapes.medium
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = server.name,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (isSelected) {
                            Text(
                                text = "✓",
                                fontSize = 16.sp,
                                color = PColorItem1,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (showZeqaDialog) {
        ZeqaSubServerDialog(
            onDismiss = { showZeqaDialog = false },
            onSelect = { subServer ->
                selectedServer = servers.find { it.name == "Zeqa MC" }
                mainScreenViewModel.selectCaptureModeModel(
                    captureModeModel.copy(serverHostName = subServer.serverAddress, serverPort = subServer.serverPort)
                )
                showZeqaDialog = false
            }
        )
    }
}

@Composable
fun ZeqaSubServerDialog(
    onDismiss: () -> Unit,
    onSelect: (SubServerInfo) -> Unit
) {
    val subServers = listOf(
        SubServerInfo("AS1", "Asia", "104.234.6.50", 10001),
        SubServerInfo("AS2", "Asia", "104.234.6.50", 10002),
        SubServerInfo("AS3", "Asia", "104.234.6.50", 10003),
        SubServerInfo("AS4", "Asia", "104.234.6.50", 10004),
        SubServerInfo("AS5", "Asia", "104.234.6.50", 10005),
        SubServerInfo("EU1", "Europe", "178.32.145.167", 10001),
        SubServerInfo("EU2", "Europe", "178.32.145.167", 10002),
        SubServerInfo("EU3", "Europe", "178.32.145.167", 10003),
        SubServerInfo("EU4", "Europe", "178.32.145.167", 10004),
        SubServerInfo("EU5", "Europe", "178.32.145.167", 10005),
        SubServerInfo("NA1", "North America", "51.79.62.8", 10001),
        SubServerInfo("NA2", "North America", "51.79.62.8", 10002),
        SubServerInfo("NA3", "North America", "51.79.62.8", 10003),
        SubServerInfo("NA4", "North America", "51.79.62.8", 10004),
        SubServerInfo("NA5", "North America", "51.79.62.8", 10005),
        SubServerInfo("SA1", "South Africa", "38.54.63.126", 10001),
        SubServerInfo("SA2", "South Africa", "38.54.63.126", 10002),
        SubServerInfo("SA3", "South Africa", "38.54.63.126", 10003),
        SubServerInfo("SA4", "South Africa", "38.54.63.126", 10004),
        SubServerInfo("SA5", "South Africa", "38.54.63.126", 10005)
    )


    val visible = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) {
        visible.targetState = true
    }
    

    AnimatedVisibility(
        visibleState = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Select Zeqa Sub-Server",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    TextButton(onClick = { 
                        visible.targetState = false
                        onDismiss() 
                    }) {
                        Text(
                            "Cancel",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Text(
                    "Choose a sub-server based on your region",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Divider(
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(subServers) { subServer ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clickable { 
                                    visible.targetState = false
                                    onSelect(subServer) 
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = subServer.id,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = subServer.region,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = subServer.serverAddress,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Port: ${subServer.serverPort}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}