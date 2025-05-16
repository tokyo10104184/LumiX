package com.project.lumina.client.constructors


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shortcut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import com.project.lumina.client.R
import com.project.lumina.client.overlay.OverlayManager
import com.project.lumina.client.ui.theme.TextColorForModules
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ModuleContent(cheatCategory: CheatCategory) {
    val modules = GameManager.elements
        .fastFilter { it.category === cheatCategory && it.name != "ChatListener" } 

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(modules.size) {
            val module = modules[it]
            ModuleCard(module)
        }
    }
}

@Composable
private fun ModuleCard(element: Element) {
    val values = element.values

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(durationMillis = 100)),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Text(
                    text = if (element.displayNameResId != null) stringResource(id = element.displayNameResId!!) else element.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextColorForModules,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = element.isEnabled,
                    onCheckedChange = { element.isEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0x66FFFFFF),
                        uncheckedThumbColor = Color(0x66FFFFFF),
                        checkedTrackColor = Color(0x66AACCD8),
                        uncheckedTrackColor = Color(0x66AACCD8),
                        checkedBorderColor = Color(0x00AACCD8),
                        uncheckedBorderColor = Color(0x00AACCD8)
                    ),
                    modifier = Modifier
                        .size(40.dp, 20.dp)
                        .padding(0.dp),
                    thumbContent = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(0x66FFFFFF), shape = CircleShape)
                        )
                    }
                )
            }
            if (element.isEnabled) {
                values.fastForEach {
                    when (it) {
                        is BoolValue -> BoolValueContent(it)
                        is FloatValue -> FloatValueContent(it)
                        is IntValue -> IntValueContent(it)
                        is ListValue -> ChoiceValueContent(it)
                    }
                }
                ShortcutContent(element)
            }
        }
    }
}

@Composable
private fun ChoiceValueContent(value: ListValue) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = if (value.nameResId != 0) stringResource(id = value.nameResId) else value.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            value.listItems.forEach { item ->
                val isSelected = value.value == item
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (value.value != item) {
                            value.value = item
                        }
                    },
                    label = {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        labelColor = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloatValueContent(value: FloatValue) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (value.nameResId != 0) stringResource(id = value.nameResId) else value.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = String.format(Locale.US, "%.2f", value.value),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        val colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
        ) {
            Slider(
                value = animateFloatAsState(
                    targetValue = value.value,
                    label = "",
                ).value,
                valueRange = value.range,
                colors = colors,
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(),
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        colors = colors,
                        thumbSize = DpSize(20.dp, 20.dp),
                        enabled = true
                    )
                },
                onValueChange = {
                    val newValue = ((it * 100.0).roundToInt() / 100.0).toFloat()
                    if (value.value != newValue) {
                        value.value = newValue
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format(Locale.US, "%.1f", value.range.start),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(Locale.US, "%.1f", value.range.endInclusive),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntValueContent(value: IntValue) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (value.nameResId != 0) stringResource(id = value.nameResId) else value.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = value.value.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        val colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
        ) {
            Slider(
                value = animateFloatAsState(
                    targetValue = value.value.toFloat(),
                    label = "",
                ).value,
                valueRange = value.range.toFloatRange(),
                colors = colors,
                steps = (value.range.last - value.range.first - 1).coerceAtLeast(0),
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(),
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        colors = colors,
                        thumbSize = DpSize(20.dp, 20.dp),
                        enabled = true
                    )
                },
                onValueChange = {
                    val newValue = it.roundToInt()
                    if (value.value != newValue) {
                        value.value = newValue
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value.range.first.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value.range.last.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BoolValueContent(value: BoolValue) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .toggleable(
                value = value.value,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onValueChange = { value.value = it }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (value.nameResId != 0) stringResource(id = value.nameResId) else value.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Switch(
            checked = value.value,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        )
    }
}

@Composable
private fun ShortcutContent(element: Element) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .toggleable(
                value = element.isShortcutDisplayed,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onValueChange = {
                    element.isShortcutDisplayed = it
                    if (it) {
                        OverlayManager.showOverlayWindow(element.overlayShortcutButton)
                    } else {
                        OverlayManager.dismissOverlayWindow(element.overlayShortcutButton)
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shortcut,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Shortcut", 
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Switch(
            checked = element.isShortcutDisplayed,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        )
    }
}

private fun IntRange.toFloatRange() = first.toFloat()..last.toFloat()