package com.cs6018.canvasexample.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs6018.canvasexample.data.DrawingInfo
import com.cs6018.canvasexample.ui.theme.NudeBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DrawingList(
    navigateToCanvasPage: () -> Unit,
    dataList: List<DrawingInfo>?,
    setActiveDrawingInfoById: suspend (Int?) -> Unit,
    removeListItem: suspend (DrawingInfo, Context) -> Unit
) {
    if (dataList == null) {
        return
    }
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(dataList, key = {
            it.id
        }) { drawingInfo ->
            DrawingListItem(
                scope,
                drawingInfo,
                setActiveDrawingInfoById,
                removeListItem,
                navigateToCanvasPage
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    navigateToCanvasScreen: () -> Unit,
    setActiveCapturedImage: (Bitmap?) -> Unit,
    setActiveDrawingInfoById: suspend (Int?) -> Unit,
    dataList: List<DrawingInfo>?,
    removeListItem: suspend (DrawingInfo, Context) -> Unit
) {
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        coroutineScope.launch {
            delay(100)
            state.animateScrollToItem(0)
        }
        onDispose { }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Gallery",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif)
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = NudeBlue,
                    titleContentColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                setActiveCapturedImage(null)
                                setActiveDrawingInfoById(null)
                                navigateToCanvasScreen()
                            }
                        }
                    ) {
                        Text(text = "New",
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.padding(end = 5.dp))
                    }
                },
            )
        },
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 56.dp)
        ) {
            DrawingList(
                navigateToCanvasScreen,
                dataList,
                setActiveDrawingInfoById,
                removeListItem
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingPreview(drawingInfo: DrawingInfo,
                   onClick: () -> Unit) {
    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        modifier = Modifier
            .aspectRatio(1f)
            .testTag("DrawingCard${drawingInfo.id}"),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (drawingInfo.thumbnail != null) {
                val thumbnail = BitmapFactory
                    .decodeByteArray(drawingInfo.thumbnail, 0, drawingInfo.thumbnail!!.size)
                Image(
                    bitmap = thumbnail.asImageBitmap(),
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingListItem(
    scope: CoroutineScope,
    drawingInfo: DrawingInfo,
    setActiveDrawingInfoById: suspend (Int?) -> Unit,
    onRemove: suspend (DrawingInfo, Context) -> Unit,
    navigateToCanvasPage: () -> Unit
) {
    val context = LocalContext.current
    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(drawingInfo)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                show = false
                true
            } else false
        }, positionalThreshold = { 150.dp.toPx() }
    )
    AnimatedVisibility(
        show, exit = fadeOut(spring())
    ) {
        SwipeToDismiss(
            state = dismissState,
            modifier = Modifier,
            background = {
                DismissBackground(dismissState)
            },
            dismissContent = {
                DrawingPreview(
                    drawingInfo = currentItem,
                    onClick = {
                        scope.launch {
                            Log.d("DrawingList", "Clicked on drawing ${drawingInfo.id}")
                            setActiveDrawingInfoById(currentItem.id)
                        }
                        navigateToCanvasPage()
                    }
                )
            }
        )
    }

    LaunchedEffect(show) {
        if (!show) {
            delay(500)
            onRemove(currentItem, context)
        }
    }
}
