package com.project.lumina.client.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.project.lumina.client.R
import com.project.lumina.client.constructors.GameManager
import com.project.lumina.client.game.module.config.ConfigManagerElement
import android.content.Intent
import com.project.lumina.client.activity.MainActivity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResult
import androidx.activity.compose.rememberLauncherForActivityResult
import android.app.Activity

@Composable
 fun ConfigCategoryContent() {
    val configManagerModule = GameManager.elements.find { it.name == "config_manager" } as? ConfigManagerElement
        ?: return

    
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_config),
                    style = MaterialTheme.typography.titleMedium
                )

                
                Button(
                    onClick = {
                        configManagerModule.saveConfig()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Current Configuration")
                }
                
                
                val context = LocalContext.current
                
                Button(
                    onClick = {
                        try {
                            
                            MainActivity.launchConfigImport()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error opening file picker: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Import Configuration")
                }
            }
        }

        
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.load_config),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (configManagerModule.configFiles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No configuration files found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(configManagerModule.configFiles.size) { index ->
                            val configFile = configManagerModule.configFiles[index]
                            ConfigFileItem(
                                configFile = configFile,
                                isSelected = configFile == configManagerModule.selectedConfig,
                                onLoadClick = { configManagerModule.loadConfig(configFile) },
                                onDeleteClick = { configManagerModule.deleteConfig(configFile) },
                                configManagerModule = configManagerModule
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ConfigFileItem(
    configFile: ConfigManagerElement.ConfigFile,
    isSelected: Boolean,
    onLoadClick: () -> Unit,
    onDeleteClick: () -> Unit,
    configManagerModule: ConfigManagerElement
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onLoadClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = configFile.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = configFile.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLoadClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(stringResource(R.string.load))
                }

                Button(
                    onClick = { configManagerModule.exportConfig(configFile) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(stringResource(R.string.export))
                }

                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
}
