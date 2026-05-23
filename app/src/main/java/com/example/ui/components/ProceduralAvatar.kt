package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProceduralAvatar(
    className: String,
    rank: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")
    
    // Slow rotating continuous angle
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse core
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Orbit coordinates list projected in 3D
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)
        val baseRadius = size.minDimension / 3.4f
        
        // Background radar grid lines (cyberpunk holographic feel)
        drawCircle(
            color = CyberCyan.copy(alpha = 0.08f),
            radius = baseRadius * 1.3f,
            style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), 0f))
        )
        drawCircle(
            color = CyberPurple.copy(alpha = 0.08f),
            radius = baseRadius * 0.8f,
            style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 10f), 0f))
        )
        // Horizontal scan line
        drawLine(
            color = CyberCyan.copy(alpha = 0.12f),
            start = Offset(center.x - baseRadius * 1.5f, center.y),
            end = Offset(center.x + baseRadius * 1.5f, center.y),
            strokeWidth = 2f
        )
        // Vertical axis guide
        drawLine(
            color = CyberPurple.copy(alpha = 0.08f),
            start = Offset(center.x, center.y - baseRadius * 1.5f),
            end = Offset(center.x, center.y + baseRadius * 1.5f),
            strokeWidth = 1f
        )

        // Theme colors based on Class & Rank
        val primaryColor = when (className) {
            "Warrior" -> CyberPink
            "Scholar" -> CyberCyan
            "Strategist" -> EliteGold
            "Creator" -> CyberPurple
            "Monk" -> TextPrimary
            else -> CyberCyan
        }
        val secondaryColor = when (className) {
            "Warrior" -> EliteGold
            "Scholar" -> CyberPurple
            "Strategist" -> CyberCyan
            "Creator" -> CyberPink
            "Monk" -> CyberCyan
            else -> CyberPurple
        }

        // Rank sparks/aura density
        val sparkCount = when (rank) {
            "Recruit" -> 4
            "Bronze" -> 8
            "Silver" -> 12
            "Gold" -> 18
            "Elite" -> 24
            "Apex" -> 32
            "Mythic" -> 45
            else -> 10
        }

        // 1. Draw glowing background rank aura
        val auraBrush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.25f * pulseScale),
                secondaryColor.copy(alpha = 0.06f),
                Color.Transparent
            ),
            center = center,
            radius = baseRadius * 1.5f
        )
        drawCircle(
            brush = auraBrush,
            radius = baseRadius * 1.5f
        )

        // 2. Draw rotating orbits - 3D projections
        val radAngle = Math.toRadians(rotationAngle.toDouble())
        
        // Rotate outer shield rings
        val shieldSegments = 4
        for (i in 0 until shieldSegments) {
            val angleOffset = i * (360f / shieldSegments)
            val finalAngle = Math.toRadians(rotationAngle.toDouble() + angleOffset)
            
            // X-tilt projection ring
            val rx = baseRadius * 1.1f
            val ry = baseRadius * 0.4f
            
            // Draw discrete holographic shield arcs
            val path = Path()
            val steps = 30
            val arcSize = 45f // angle arc length
            val startAngleRad = finalAngle
            
            for (step in 0..steps) {
                val stepAngle = startAngleRad + Math.toRadians((step * arcSize / steps).toDouble())
                val px = center.x + rx * cos(stepAngle).toFloat()
                // tilt on Y axis for 3D oblique perspective
                val py = center.y + ry * sin(stepAngle).toFloat() - (rx * 0.2f * cos(stepAngle).toFloat())
                
                if (step == 0) {
                    path.moveTo(px, py)
                } else {
                    path.lineTo(px, py)
                }
            }
            
            drawPath(
                path = path,
                color = secondaryColor.copy(alpha = 0.7f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // 3. Draw central holographic floating geometry (based on class!)
        when (className) {
            "Warrior" -> {
                // Procedural Glowing Sword/Shield Octonary Mesh
                val modelPath = Path()
                val nodes = 6
                val heightOffset = baseRadius * 0.7f
                
                // Draw connecting double triangle matrix (cyber sword spear)
                for (j in 0..1) {
                    val side = if (j == 0) 1f else -1f
                    modelPath.reset()
                    modelPath.moveTo(center.x, center.y - heightOffset)
                    modelPath.lineTo(center.x + (baseRadius * 0.25f * side), center.y + (heightOffset * 0.1f))
                    modelPath.lineTo(center.x + (baseRadius * 0.08f * side), center.y + (heightOffset * 0.8f))
                    modelPath.lineTo(center.x, center.y + (heightOffset * 0.4f))
                    modelPath.close()
                    
                    drawPath(
                        path = modelPath,
                        color = CyberPink.copy(alpha = 0.35f + (pulseScale * 0.1f)),
                        style = Fill
                    )
                    drawPath(
                        path = modelPath,
                        color = CyberPink,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                
                // Outer stabilizer wings
                drawLine(
                    color = EliteGold,
                    start = Offset(center.x - baseRadius * 0.4f, center.y + heightOffset * 0.2f),
                    end = Offset(center.x + baseRadius * 0.4f, center.y + heightOffset * 0.2f),
                    strokeWidth = 3f
                )
            }
            "Scholar" -> {
                // Procedural Octahedron with orbital calculations (Cyan)
                val octaPoints = mutableListOf<Offset>()
                val numFacets = 5
                val scaleZ = baseRadius * 0.65f
                
                // Upper pyramid lines
                val topY = center.y - scaleZ * 1.1f
                val botY = center.y + scaleZ * 1.1f
                
                for (step in 0 until numFacets) {
                    val theta = radAngle + (step * (2 * Math.PI / numFacets))
                    val px = center.x + (scaleZ * cos(theta)).toFloat()
                    val py = center.y + (scaleZ * 0.35f * sin(theta)).toFloat()
                    
                    octaPoints.add(Offset(px, py))
                    
                    // Draw lines from top and bottom to base rings
                    drawLine(color = CyberCyan, start = Offset(px, py), end = Offset(center.x, topY), strokeWidth = 1.5.dp.toPx())
                    drawLine(color = CyberCyan.copy(alpha = 0.6f), start = Offset(px, py), end = Offset(center.x, botY), strokeWidth = 1.5.dp.toPx())
                }
                // Connect base ring facets
                for (step in 0 until numFacets) {
                    val current = octaPoints[step]
                    val next = octaPoints[(step + 1) % numFacets]
                    drawLine(color = CyberPurple, start = current, end = next, strokeWidth = 2.dp.toPx())
                }
                // Core pulse
                drawCircle(color = CyberCyan.copy(alpha = 0.4f * pulseScale), radius = baseRadius * 0.2f, center = center)
            }
            "Strategist" -> {
                // Multi-tiered revolving crystalline matrices (Yellow/Cyan)
                val scaleZ = baseRadius * 0.5f
                for (tier in -1..1 step 2) {
                    val tierY = center.y + (baseRadius * 0.4f * tier)
                    val rAngle = radAngle * tier
                    
                    val points = mutableListOf<Offset>()
                    for (k in 0..3) {
                        val th = rAngle + (k * (Math.PI / 2))
                        val px = center.x + (scaleZ * cos(th)).toFloat()
                        val py = tierY + (scaleZ * 0.25f * sin(th)).toFloat()
                        points.add(Offset(px, py))
                        
                        // Draw crystals
                        drawRect(
                            color = EliteGold,
                            topLeft = Offset(px - 6f, py - 6f),
                            size = Size(12f, 12f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                    // Connect tier lattices
                    for (k in 0..3) {
                        drawLine(
                            color = CyberCyan.copy(alpha = 0.4f),
                            start = points[k],
                            end = points[(k + 1) % 4],
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = EliteGold.copy(alpha = 0.3f),
                            start = points[k],
                            end = center,
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
                drawCircle(color = EliteGold, radius = 5f, center = center)
            }
            "Creator" -> {
                // Double Ring Gyroscope pulsing intensely on vertical coordinates (Purple/Pink)
                val scale = baseRadius * 0.8f
                
                // Ring 1
                drawOval(
                    color = CyberPurple,
                    topLeft = Offset(center.x - scale, center.y - scale * 0.3f * pulseScale),
                    size = Size(scale * 2, scale * 0.6f * pulseScale),
                    style = Stroke(width = 3.dp.toPx())
                )
                
                // Ring 2 tilted opposite
                drawOval(
                    color = CyberPink,
                    topLeft = Offset(center.x - scale * 0.3f * pulseScale, center.y - scale),
                    size = Size(scale * 0.6f * pulseScale, scale * 2),
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f)))
                )
                
                // Spark core
                drawCircle(
                    brush = Brush.radialGradient(colors = listOf(CyberPink, Color.Transparent), radius = baseRadius * 0.4f),
                    radius = baseRadius * 0.4f,
                    center = center
                )
            }
            "Monk" -> {
                // concentric mandala rings & absolute calm geometry (Clean White/Indigo)
                drawCircle(
                    color = TextPrimary.copy(alpha = 0.8f),
                    radius = baseRadius * 0.4f * pulseScale,
                    style = Stroke(width = 2.dp.toPx())
                )
                // radiating star-lines
                val count = 8
                for (a in 0 until count) {
                    val th = Math.toRadians((a * (360f / count) + rotationAngle / 2f).toDouble())
                    drawLine(
                        color = CyberCyan.copy(alpha = 0.5f),
                        start = center,
                        end = Offset(center.x + (baseRadius * 0.65f * cos(th)).toFloat(), center.y + (baseRadius * 0.65f * sin(th)).toFloat()),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 10f))
                    )
                }
                drawCircle(color = TextPrimary, radius = 8f, center = center)
            }
            else -> { // Hybrid / Recruit
                // Pulsing spherical wireframe globe
                drawCircle(
                    color = CyberCyan.copy(alpha = 0.5f),
                    radius = baseRadius * 0.7f * pulseScale,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = CyberPurple.copy(alpha = 0.3f),
                    radius = baseRadius * 0.5f / pulseScale,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // 4. Draw orbiting rank particles (Sparks/Aura) as game cosmetics
        for (idx in 0 until sparkCount) {
            val offsetAngle = idx * (360f / sparkCount)
            val orbitalSpeed = if (idx % 2 == 0) 1.2f else -0.8f
            val currentRad = Math.toRadians((rotationAngle * orbitalSpeed + offsetAngle).toDouble())
            val distance = baseRadius * (1.1f + 0.3f * sin(currentRad * 3.14).toFloat())
            
            val px = center.x + distance * cos(currentRad).toFloat()
            val py = center.y + distance * 0.6f * sin(currentRad).toFloat() - (0.2f * baseRadius * cos(currentRad).toFloat())
            
            // Draw particles
            val sparkColor = if (idx % 2 == 0) primaryColor else secondaryColor
            val sparkSize = if (rank == "Mythic" || rank == "Apex") 5.dp.toPx() else 3.dp.toPx()
            
            drawCircle(
                color = sparkColor,
                radius = sparkSize,
                center = Offset(px, py)
            )
            // Draw brief glow trail
            drawCircle(
                color = sparkColor.copy(alpha = 0.3f),
                radius = sparkSize * 2f,
                center = Offset(px, py)
            )
        }
    }
}
