package com.tushar.ballclickergamecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tushar.ballclickergamecompose.ui.theme.BallClickerGameComposeTheme
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BallClickerGameComposeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var points by remember {
        mutableStateOf(0)
    }
    var isTimerRunning by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Points: $points",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    isTimerRunning = !isTimerRunning
                    points = 0
                }
            ) {
                Text(
                    text = if (isTimerRunning) "Reset" else "Start"
                )
            }
            CountdownTimer(isTimerRunning = isTimerRunning)
        }
        BallClickerScreen(
            enabled = isTimerRunning
        ){
            points++
        }
    }
}

@Composable
fun CountdownTimer(
    time: Int = 30000,
    isTimerRunning: Boolean,
    onTimerEnd: () -> Unit = {}
) {
    var currentTime by remember {
        mutableStateOf(time)
    }
    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if (!isTimerRunning) {
            currentTime = time
            return@LaunchedEffect
        }
        if (currentTime > 0) {
            delay(1000)
            currentTime -= 1000
        } else {
            onTimerEnd.invoke()
        }
    }
    Text(
        text = (currentTime / 1000).toString(),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun BallClickerScreen(
    radius: Float = 100f,
    enabled: Boolean = false,
    color: Color = Color.Green,
    onBallClick: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        var ballOffsetPosition by remember {
            mutableStateOf(
                generateRandomOffset(
                    radius = radius,
                    width = constraints.maxWidth,
                    height = constraints.maxHeight
                )
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(enabled) {
                    if (!enabled) {
                        return@pointerInput
                    }
                    detectTapGestures {
                        val distance = sqrt(
                            (it.x - ballOffsetPosition.x).pow(2) + (it.y - ballOffsetPosition.y).pow(
                                2
                            )
                        )
                        if (distance <= radius) {
                            ballOffsetPosition = generateRandomOffset(
                                radius = radius,
                                width = constraints.maxWidth,
                                height = constraints.maxHeight
                            )
                            onBallClick.invoke()
                        }
                    }
                }
        ) {
            drawCircle(
                color = color,
                radius = radius,
                center = ballOffsetPosition
            )
        }
    }
}

private fun generateRandomOffset(radius: Float, width: Int, height: Int): Offset {
    return Offset(
        x = Random.nextInt(radius.toInt(), width - radius.roundToInt()).toFloat(),
        y = Random.nextInt(radius.toInt(), height - radius.roundToInt()).toFloat()
    )
}