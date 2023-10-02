package com.example.drawingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.drawingapp.ui.theme.DrawingAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("gallery_screen") {
            GalleryScreen(navController)
        }
        composable("canvas_screen") {
            CanvasScreen(navController)
        }
    }
}

@Composable
private fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(1500)
        navController.navigate("canvas_screen")
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

@Composable
private fun GalleryScreen(navController: NavHostController) {
    Text(text = "gallery")
}

@Composable
private fun CanvasScreen(navController: NavHostController) {
    Text(text = "canvas")
}