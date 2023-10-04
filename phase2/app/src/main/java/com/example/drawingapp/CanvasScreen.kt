package com.example.drawingapp


import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.drawingapp.gesture.MotionEvent
import com.example.drawingapp.gesture.dragMotionEvent
import com.example.drawingapp.ui.theme.BackgroundColor
import com.example.drawingapp.ui.theme.CanvasColor
import com.example.drawingapp.ui.theme.IcareBlack
import com.example.drawingapp.ui.theme.IcareBlue
import com.example.drawingapp.ui.theme.IcareRed
import com.example.drawingapp.ui.theme.IcareYellow
import com.example.drawingapp.ui.theme.LightNudeBlue
import com.example.drawingapp.ui.theme.NudeBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("L'Atelier Bleu",
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = NudeBlue,
                    titleContentColor = Color.White,
                ),
//                navigationIcon = {
//                    IconButton(onClick = {
//                    /* TODO: save current drawing and go back to gallery screen */
//                    }) {
//                        Icon(
//                            imageVector = Icons.Filled.ArrowBack,
//                            contentDescription = "Localized description"
//                        )
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                    /* TODO: save current drawing and go back to gallery screen */
//                    }) {
//                        Icon(
//                            imageVector = Icons.Filled.Done,
//                            contentDescription = "Localized description")
//                }},
            )
        }
    ) {
            paddingValues: PaddingValues ->
        DrawingApp(paddingValues)
    }
}

@Composable
fun DrawingApp(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    val pathsUndone = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    val canvasText = remember { StringBuilder() }
    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = IcareBlack.toArgb() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        val drawModifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .aspectRatio(1f)
            .weight(1f)
            .background(Color.White)
            .dragMotionEvent(
            onDragStart = { pointerInputChange ->
                motionEvent = MotionEvent.Down
                currentPosition = pointerInputChange.position
                pointerInputChange.consumeDownChange()
            },
            onDrag = { pointerInputChange ->
                motionEvent = MotionEvent.Move
                currentPosition = pointerInputChange.position
                if (drawMode == DrawMode.Touch) {
                    val change = pointerInputChange.positionChange()
                    paths.forEach { entry ->
                        val path: Path = entry.first
                        path.translate(change)
                    }
                    currentPath.translate(change)
                }
                pointerInputChange.consumePositionChange()
            },
            onDragEnd = { pointerInputChange ->
                motionEvent = MotionEvent.Up
                pointerInputChange.consumeDownChange()
            }
        )


        Canvas(modifier = drawModifier) {
            when (motionEvent) {
                MotionEvent.Down -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                    }
                    previousPosition = currentPosition
                }
                MotionEvent.Move -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )
                    }
                    previousPosition = currentPosition
                }
                MotionEvent.Up -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)
                        paths.add(Pair(currentPath, currentPathProperty))
                        currentPath = Path()
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )
                    }
                    pathsUndone.clear()
                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }
                else -> Unit
            }
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                paths.forEach {
                    val path = it.first
                    val property = it.second
                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                if (motionEvent != MotionEvent.Idle) {
                    if (!currentPathProperty.eraseMode) {
                        drawPath(
                            color = currentPathProperty.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }
        Row(horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = Color.White,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = CanvasColor,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CanvasColor)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = LightNudeBlue,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightNudeBlue)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = NudeBlue,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NudeBlue)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = IcareRed,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareRed)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = IcareYellow,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareYellow)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = IcareBlue,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareBlue)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    currentPathProperty = PathProperties(
                        strokeWidth = currentPathProperty.strokeWidth,
                        color = IcareBlack,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareBlack)) {
            }
        }
        Row {
            Slider(
                colors = SliderDefaults.colors(
                    thumbColor = NudeBlue,
                    activeTrackColor = LightNudeBlue,
                    inactiveTrackColor = Color.White,
                ),
                value = currentPathProperty.strokeWidth,
                onValueChange = {
                    currentPathProperty = PathProperties(
                        strokeWidth = it,
                        color = currentPathProperty.color,
                        strokeCap = currentPathProperty.strokeCap,
                        strokeJoin = currentPathProperty.strokeJoin,
                        eraseMode = currentPathProperty.eraseMode
                    )
                },
                valueRange = 10f..200f,
                onValueChangeFinished = {}
            )
        }
    }
}