package com.cs6018.canvasexample.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs6018.canvasexample.R
import com.cs6018.canvasexample.model.CapturableImageViewModel
import com.cs6018.canvasexample.model.DrawingInfoViewModel
import com.cs6018.canvasexample.model.DrawingInfoViewModelFactory
import com.cs6018.canvasexample.model.PathPropertiesViewModel
import com.cs6018.canvasexample.ui.components.BrushSizeScreen
import com.cs6018.canvasexample.ui.components.CanvasScreen
import com.cs6018.canvasexample.ui.components.GalleryScreen
import com.cs6018.canvasexample.ui.components.BrushColorScreen
import com.cs6018.canvasexample.ui.theme.CanvasExampleTheme
import com.cs6018.canvasexample.DrawingApplication
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val capturableImageViewModel: CapturableImageViewModel by viewModels()
        val pathPropertiesViewModel: PathPropertiesViewModel by viewModels()
        val drawingInfoViewModel: DrawingInfoViewModel by viewModels { DrawingInfoViewModelFactory((application as DrawingApplication).drawingInfoRepository) }

        setContent {
            CanvasExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        pathPropertiesViewModel,
                        drawingInfoViewModel,
                        capturableImageViewModel,
                        rememberNavController()
                    )
                }
            }
        }
    }
}

@Composable
fun Navigation(
    pathPropertiesViewModel: PathPropertiesViewModel,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    navController: NavHostController,
    isTest:Boolean = false
) {
    val currentPathProperty by pathPropertiesViewModel.currentPathProperty.collectAsState()
    rememberColorPickerController()

    val navigateToBrushColor = {
        navController.navigate("brush_color")
    }
    val navigateToBrushSize = {
        navController.navigate("brush_size")
    }
    val navigateToCanvasScreen = {
        navController.navigate("canvas_screen")
    }
    val navigateToPopBack = {
        navController.popBackStack()
    }

    val drawingInfoDataList by drawingInfoViewModel.allDrawingInfo.observeAsState()

    NavHost(navController = navController,
        startDestination = "splash_screen") {

        composable("splash_screen") {
            SplashScreen({ navController.navigate("gallery_screen") }, isTest)
        }

        composable("gallery_screen") {
            GalleryScreen(
                navigateToCanvasScreen,
                drawingInfoViewModel::setActiveCapturedImage,
                drawingInfoViewModel::setActiveDrawingInfoById,
                drawingInfoDataList,
                drawingInfoViewModel::deleteDrawingInfoWithId
            )
        }

        composable("canvas_screen") {
            CanvasScreen(
                pathPropertiesViewModel,
                drawingInfoViewModel,
                capturableImageViewModel,
                navigateToBrushColor,
                navigateToBrushSize,
                navigateToPopBack
            )
        }

        composable("brush_color") {
            BrushColorScreen(
                pathPropertiesViewModel::updateHexColorCode,
                pathPropertiesViewModel::updateCurrentPathProperty,
                navigateToPopBack
            )
        }

        composable("brush_size") {
            BrushSizeScreen(
                currentPathProperty,
                pathPropertiesViewModel::updateCurrentPathProperty,
                navigateToPopBack
            )
        }
    }
}

@Composable
fun SplashScreen(onSplashScreenComplete: ()-> Unit,
    isTest: Boolean = false) {
    val scale = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        try {
            scale.animateTo(
                targetValue = 1f,
            )
            delay(1500)
        } catch (e: Exception) {
            if(!isTest) {
                Log.e("SplashScreen", "Splash screen image not loaded")
            }
        } finally {
            if(!isTest) {
                coroutineScope.launch {
                    onSplashScreenComplete()
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.logo),
            contentDescription = null)
        Text(text = "L'Atelier Bleu",
            color = Color.White,
            fontSize = 30.sp,
            fontFamily = FontFamily.Serif)
    }
}
