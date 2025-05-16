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

package com.project.lumina.client.overlay

import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.lumina.client.R
import com.project.lumina.client.ui.theme.SAccentColor
import com.project.lumina.client.ui.theme.SBAckgroundGradient1
import com.project.lumina.client.ui.theme.SBAckgroundGradient2
import com.project.lumina.client.ui.theme.SBaseColor
import kotlin.math.abs
import kotlinx.coroutines.delay

class SessionStatsOverlay : OverlayWindow() {

    private val _statLines = mutableStateOf<List<String>>(emptyList())
    private val _isVisible = mutableStateOf(true)

    private val _layoutParams by lazy {
        super.layoutParams.apply {
            flags = flags or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            x = 20 
            y = 50 
        }
    }

    override val layoutParams: WindowManager.LayoutParams
        get() = _layoutParams

    @Composable
    override fun Content() {
        SessionStatsCard(
            statLines = _statLines,
            isVisible = _isVisible,
            overlay = this
        )
    }

    fun updateStats(statLines: List<String>) {
        _statLines.value = statLines
    }

    fun addStat(statLine: String) {
        _statLines.value = _statLines.value + statLine
    }

    fun clearStats() {
        _statLines.value = emptyList()
    }

    fun dismiss() {
        _isVisible.value = false
        
    }

    companion object {
        private var currentOverlay: SessionStatsOverlay? = null

        fun showSessionStats(initialStats: List<String> = emptyList()): SessionStatsOverlay {
            
            currentOverlay?.let {
                OverlayManager.dismissOverlayWindow(it)
            }

            val overlay = SessionStatsOverlay().apply {
                updateStats(initialStats)
            }

            OverlayManager.showOverlayWindow(overlay)
            currentOverlay = overlay
            return overlay
        }
    }
}

@Composable
fun SessionStatsCard(
    statLines: MutableState<List<String>>,
    isVisible: MutableState<Boolean>,
    overlay: OverlayWindow
) {
    var visible by remember { isVisible }
    val baseColor = SBaseColor
    val accentColor = SAccentColor

    
    val shimmerTransition = rememberInfiniteTransition(label = "shimmerTransition")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(200),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .width(180.dp)
            .padding(top = 15.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SBAckgroundGradient1,
                        SBAckgroundGradient2
                    )
                )
            )
    ) {
        
        Box(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 0.05f),
                            Color.White.copy(alpha = 0f)
                        ),
                        start = Offset(shimmerOffset - 300f, 0f),
                        end = Offset(shimmerOffset, 0f)
                    )
                )
        )

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session Stats",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    style = TextStyle(
                        shadow = Shadow(
                            color = baseColor.copy(alpha = 0.7f),
                            offset = Offset(0f, 2f),
                            blurRadius = 4f
                        )
                    )
                )

                
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = accentColor,
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }

            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                baseColor,
                                accentColor,
                                baseColor
                            )
                        ),
                        shape = RoundedCornerShape(1.dp)
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            
            Column(modifier = Modifier.fillMaxWidth()) {
                statLines.value.forEachIndexed { index, statLine ->
                    val parts = statLine.split(":", limit = 2)
                    
                    if (parts.size == 2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = parts[0].trim(),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = parts[1].trim(),
                                fontSize = 12.sp,
                                color = accentColor,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End
                            )
                        }
                    } else {
                        Text(
                            text = statLine,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 4.dp),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Start
                        )
                    }

                    if (index < statLines.value.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    color = baseColor.copy(alpha = 0.1f)
                                )
                        )
                    }
                }
            }
        }
    }

    if (!visible) {
        LaunchedEffect(visible) {
            delay(300)
            OverlayManager.dismissOverlayWindow(overlay)
        }
    }
}


private fun hsvToRgb(h: Float, s: Float, v: Float): Color {
    val hDegrees = h * 360f
    val c = v * s
    val x = c * (1 - abs((hDegrees / 60f) % 2 - 1))
    val m = v - c

    val (r, g, b) = when {
        hDegrees < 60 -> Triple(c, x, 0f)
        hDegrees < 120 -> Triple(x, c, 0f)
        hDegrees < 180 -> Triple(0f, c, x)
        hDegrees < 240 -> Triple(0f, x, c)
        hDegrees < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f)
    )
}

