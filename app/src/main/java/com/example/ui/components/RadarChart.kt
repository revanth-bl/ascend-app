package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
@Composable
fun RadarChart(
    discipline: Float,     // 0.0f to 1.0f
    focus: Float,          // 0.0f to 1.0f
    intelligence: Float,   // 0.0f to 1.0f
    strength: Float,       // 0.0f to 1.0f
    creativity: Float,     // 0.0f to 1.0f
    consistency: Float,    // 0.0f to 1.0f
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)
        val maxRadius = size.minDimension / 2.8f

        val axes = listOf("Discipline", "Focus", "Intelligence", "Strength", "Creativity", "Consistency")
        val values = listOf(discipline, focus, intelligence, strength, creativity, consistency)
        val numAxes = axes.size

        // 1. Draw web grid circles / concentric hexagons
        val rings = 4
        for (ring in 1..rings) {
            val ringRadius = maxRadius * (ring.toFloat() / rings.toFloat())
            val path = Path()
            for (i in 0 until numAxes) {
                // start pointing up at -90 degrees
                val angleRad = Math.toRadians((i * (360f / numAxes) - 90f).toDouble())
                val x = center.x + (ringRadius * cos(angleRad)).toFloat()
                val y = center.y + (ringRadius * sin(angleRad)).toFloat()
                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()
            drawPath(
                path = path,
                color = GlassTintBorder.copy(alpha = 0.5f),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 2. Draw axis lines radiating outwards
        for (i in 0 until numAxes) {
            val angleRad = Math.toRadians((i * (360f / numAxes) - 90f).toDouble())
            val endX = center.x + (maxRadius * cos(angleRad)).toFloat()
            val endY = center.y + (maxRadius * sin(angleRad)).toFloat()
            drawLine(
                color = GlassTintBorder.copy(alpha = 0.4f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.5f
            )
        }

        // 3. Draw player data polygon
        val polyPath = Path()
        val dataPoints = mutableListOf<Offset>()
        for (i in 0 until numAxes) {
            val valueScale = values[i].coerceIn(0.12f, 1.0f) // Keep a micro minimum so it looks like a polygon
            val currentRadius = maxRadius * valueScale
            val angleRad = Math.toRadians((i * (360f / numAxes) - 90f).toDouble())
            val x = center.x + (currentRadius * cos(angleRad)).toFloat()
            val y = center.y + (currentRadius * sin(angleRad)).toFloat()
            dataPoints.add(Offset(x, y))
            if (i == 0) {
                polyPath.moveTo(x, y)
            } else {
                polyPath.lineTo(x, y)
            }
        }
        polyPath.close()

        // Fill with futuristic dual cyan-purple gradient
        drawPath(
            path = polyPath,
            brush = Brush.radialGradient(
                colors = listOf(CyberCyan.copy(alpha = 0.5f), CyberPurple.copy(alpha = 0.6f)),
                center = center,
                radius = maxRadius
            ),
            style = Fill
        )
        // Outline data polygon in solid cyber cyan
        drawPath(
            path = polyPath,
            color = CyberCyan,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw points at indices
        for (point in dataPoints) {
            drawCircle(color = Color.White, radius = 3.dp.toPx(), center = point)
            drawCircle(color = CyberCyan, radius = 5.dp.toPx(), center = point, style = Stroke(width = 1.dp.toPx()))
        }

        // 4. Draw text labels at the endpoints of the axes
        for (i in 0 until numAxes) {
            val angleRad = Math.toRadians((i * (360f / numAxes) - 90f).toDouble())
            val offsetMultiplier = 1.25f
            val labelX = center.x + (maxRadius * offsetMultiplier * cos(angleRad)).toFloat()
            val labelY = center.y + (maxRadius * 1.15f * sin(angleRad)).toFloat()

            // Quick measurement
            val labelText = axes[i]
            val textLayoutResult = textMeasurer.measure(
                text = labelText,
                style = TextStyle(
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )

            // Calculate bounding alignment offsets
            val textWidth = textLayoutResult.size.width
            val textHeight = textLayoutResult.size.height
            val finalX = labelX - (textWidth / 2f)
            val finalY = labelY - (textHeight / 2f)

            // Draw text
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(finalX, finalY)
            )
        }
    }
}
