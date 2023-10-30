package com.cs6018.canvasexample.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.cs6018.canvasexample.model.CapturableImageViewModel
import com.cs6018.canvasexample.model.DrawingInfoViewModel
import com.cs6018.canvasexample.model.PathProperties
import com.cs6018.canvasexample.model.PathPropertiesViewModel
import com.cs6018.canvasexample.ui.theme.NudeBlue
import com.cs6018.canvasexample.gesture.MotionEvent
import com.cs6018.canvasexample.gesture.dragMotionEvent
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    pathPropertiesViewModel: PathPropertiesViewModel,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    navigateToBrushColor: () -> Unit,
    navigateToBrushSize: () -> Unit,
    navigateToPopBack: () -> Boolean
) {
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activeDrawingInfo by drawingInfoViewModel.activeDrawingInfo.observeAsState()
    var drawingTitle by rememberSaveable {
        mutableStateOf(
            activeDrawingInfo?.drawingTitle ?: "Untitled"
        )
    }

    LaunchedEffect(activeDrawingInfo?.drawingTitle) {
        val newDrawingTitle = activeDrawingInfo?.drawingTitle ?: "Untitled"
        drawingTitle = newDrawingTitle
    }
    BackHandler {
        customBackNavigation(
            scope,
            drawingInfoViewModel,
            pathPropertiesViewModel,
            navigateToPopBack
        )
    }
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
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            onShareClick(
                                scope,
                                context,
                                captureController,
                                drawingInfoViewModel,
                                capturableImageViewModel
                            )
                        }
                    ) {
                        Text(text = "Share",
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.padding(end = 5.dp))
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                content = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(shape = RectangleShape,
                            onClick = navigateToBrushColor,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NudeBlue)) {
                            Text(text = "Palette",
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Serif)
                        }
                        Button(shape = RectangleShape,
                            onClick = navigateToBrushSize,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NudeBlue)) {
                            Text(text = "Brush Size",
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Serif)
                        }
                        Button(shape = RectangleShape,
                            onClick = { saveCurrentDrawing(
                                drawingInfoViewModel,
                                coroutineScope,
                                context,
                                captureController,
                                pathPropertiesViewModel,
                                capturableImageViewModel,
                                drawingTitle,
                                navigateToPopBack
                            ) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NudeBlue)) {
                            Text(text = "Save",
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Serif)
                        }
                    }
                }
            )
        },
        content = {
            Playground(
                pathPropertiesViewModel,
                it,
                captureController,
                drawingInfoViewModel,
                capturableImageViewModel
            )
        }
    )
}
fun onShareClick(
    scope: CoroutineScope,
    context: Context,
    captureController: CaptureController,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel
) {
    scope.launch {
        captureController.capture()
    }
    scope.launch {
        capturableImageViewModel.signalChannel.value?.receive()
        capturableImageViewModel.setNewSignalChannel()
        val bitmap = drawingInfoViewModel.getActiveCapturedImage().value

        if (bitmap == null) {
            Log.e("CanvasScreen", "Bitmap is null")
        } else {
            val activeDrawingInfoDrawingTitle =
                drawingInfoViewModel.activeDrawingInfo.value?.drawingTitle
            val uri = saveBitmapAsTemporaryImage(context, bitmap)
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/jpeg"
            }
            val chooserIntent = Intent.createChooser(shareIntent, activeDrawingInfoDrawingTitle)
            chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(chooserIntent)
        }
    }
}

private fun saveBitmapAsTemporaryImage(context: Context, bitmap: Bitmap): Uri {
    val cacheDir = context.cacheDir
    val curTime = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(Date())
    val imageFile = File.createTempFile(curTime, ".jpg", cacheDir)
    val outputStream = FileOutputStream(imageFile)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    return FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
}

fun customBackNavigation(
    scope: CoroutineScope,
    drawingInfoViewModel: DrawingInfoViewModel,
    pathPropertiesViewModel: PathPropertiesViewModel,
    navigateToPopBack: () -> Boolean
) {
    navigateToPopBack()
    pathPropertiesViewModel.reset()
    scope.launch {
        drawingInfoViewModel.setActiveDrawingInfoById(null)
        drawingInfoViewModel.setActiveCapturedImage(null)
    }
}

fun saveCurrentDrawing(
    drawingInfoViewModel: DrawingInfoViewModel,
    coroutineScope: CoroutineScope,
    context: Context,
    captureController: CaptureController,
    pathPropertiesViewModel: PathPropertiesViewModel,
    captureableImageViewModel: CapturableImageViewModel,
    drawingTitle: String,
    navigateToPopBack: () -> Boolean
) {
    coroutineScope.launch {
        captureController.capture()
    }
    coroutineScope.launch {
        captureableImageViewModel.signalChannel.value?.receive()
        captureableImageViewModel.setNewSignalChannel()
        drawingInfoViewModel.addDrawingInfoWithRecentCapturedImage(context, drawingTitle)
        drawingInfoViewModel.setActiveDrawingInfoById(null)
        drawingInfoViewModel.setActiveCapturedImage(null)
        pathPropertiesViewModel.reset()
        navigateToPopBack()
    }
}

@Composable
fun Playground(
    viewModel: PathPropertiesViewModel,
    paddingValues: PaddingValues,
    captureController: CaptureController,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel
) {
    val paths = viewModel.paths
    val pathsUndone = viewModel.pathsUndone
    val motionEvent = viewModel.motionEvent
    val currentPosition = viewModel.currentPosition
    var previousPosition = viewModel.previousPosition
    val currentPath = viewModel.currentPath
    val currentPathProperty = viewModel.currentPathProperty
    val activeDrawingInfo by drawingInfoViewModel.activeDrawingInfo.observeAsState()
    var backgroundImageUri: Uri? = null

    try {
        backgroundImageUri = Uri.parse(activeDrawingInfo?.imagePath)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val basePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(backgroundImageUri)
            .size(Size.ORIGINAL)
            .allowHardware(false)
            .build()
    )
    val baseImageLoadedState = basePainter.state
    var baseImageBitmap: ImageBitmap? = null
    if (
        baseImageLoadedState is AsyncImagePainter.State.Success
    ) {
        baseImageBitmap =
            baseImageLoadedState.result.drawable.toBitmap()
                .asImageBitmap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(paddingValues)
    ) {
        val drawModifier = Modifier
            .drawBehind {
                if (baseImageBitmap != null) {
                    drawImage(
                        image = baseImageBitmap,
                        topLeft = Offset.Zero,
                    )
                } else {
                    drawRect(
                        color = Color.White,
                        topLeft = Offset.Zero,
                        size = size
                    )
                }
            }
            .background(Color.Transparent)
            .padding(8.dp)
            .shadow(1.dp)
            .fillMaxWidth()
            .weight(1f)
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    viewModel.updateMotionEvent(MotionEvent.Down)
                    viewModel.updateCurrentPosition(pointerInputChange.position)
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                },
                onDrag = { pointerInputChange ->

                    viewModel.updateMotionEvent(MotionEvent.Move)
                    viewModel.updateCurrentPosition(pointerInputChange.position)

                    if (pointerInputChange.positionChange() != Offset.Zero) {
                        pointerInputChange.consume()
                    }

                },
                onDragEnd = { pointerInputChange ->
                    viewModel.updateMotionEvent(MotionEvent.Up)
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                }
            )
        Capturable(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            controller = captureController,
            onCaptured = { bitmap, error ->
                if (bitmap != null) {
                    val dataAsBitmap = bitmap.asAndroidBitmap()
                    try {
                        drawingInfoViewModel.setActiveCapturedImage(dataAsBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                capturableImageViewModel.fireSignal()
            }
        ) {
            Canvas(modifier = drawModifier) {
                when (motionEvent.value) {
                    MotionEvent.Down -> {
                        currentPath.value.moveTo(
                            currentPosition.value.x,
                            currentPosition.value.y
                        )
                        previousPosition = currentPosition
                    }
                    MotionEvent.Move -> {
                        currentPath.value.quadraticBezierTo(
                            previousPosition.value.x,
                            previousPosition.value.y,
                            (previousPosition.value.x + currentPosition.value.x) / 2,
                            (previousPosition.value.y + currentPosition.value.y) / 2
                        )
                        previousPosition = currentPosition
                    }
                    MotionEvent.Up -> {
                        currentPath.value.lineTo(
                            currentPosition.value.x,
                            currentPosition.value.y
                        )
                        paths.add(Pair(currentPath.value, currentPathProperty.value))
                        viewModel.updateCurrentPath(Path())
                        viewModel.updateCurrentPathProperty(
                            PathProperties(
                                strokeWidth = currentPathProperty.value.strokeWidth,
                                color = currentPathProperty.value.color,
                                strokeCap = currentPathProperty.value.strokeCap,
                                strokeJoin = currentPathProperty.value.strokeJoin
                            )
                        )
                        pathsUndone.clear()
                        viewModel.updatePreviousPosition(currentPosition.value)
                        viewModel.updateCurrentPosition(Offset.Unspecified)
                        viewModel.updateMotionEvent(MotionEvent.Idle)
                    }
                    else -> Unit
                }
                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)
                    paths.forEach {
                        val path = it.first
                        val property = it.second
                        val style = Stroke(
                            width = property.strokeWidth,
                            cap = property.strokeCap,
                            join = property.strokeJoin
                        )
                        drawPath(
                            color = property.color,
                            path = path,
                            style = style,
                            alpha = property.color.alpha
                        )
                    }
                    if (motionEvent.value != MotionEvent.Idle) {
                        val style = Stroke(
                            width = currentPathProperty.value.strokeWidth,
                            cap = currentPathProperty.value.strokeCap,
                            join = currentPathProperty.value.strokeJoin
                        )
                        drawPath(
                            color = currentPathProperty.value.color,
                            path = currentPath.value,
                            style = style,
                            alpha = currentPathProperty.value.color.alpha
                        )
                    }
                    restoreToCount(checkPoint)
                }
            }
        }
    }
}