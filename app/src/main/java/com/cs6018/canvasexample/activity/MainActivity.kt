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
import com.cs6018.canvasexample.ui.components.LoginScreen
import com.cs6018.canvasexample.DrawingApplication
import com.cs6018.canvasexample.network.ApiViewModel
import com.cs6018.canvasexample.network.ApiViewModelFactory
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiViewModel: ApiViewModel by viewModels { ApiViewModelFactory((application as DrawingApplication).apiRepository) }
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
                        apiViewModel,
                        pathPropertiesViewModel,
                        drawingInfoViewModel,
                        capturableImageViewModel,
                        rememberNavController(),
                        ::createUserWithEmailAndPassword,
                        ::signInWithEmailAndPassword
                    )
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    private fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = Firebase.auth.currentUser
                onSuccess(user)
            } else {
                try {
                    throw task.exception!!
                } catch (e: Exception) {
                    onFailure(e.message ?: "Sign up failed")
                }
            }
        }
    }

    private fun signInWithEmailAndPassword(
        email: String, password: String, onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = Firebase.auth.currentUser
                onSuccess(user)
            } else {
                try {
                    throw task.exception!!
                } catch (e: Exception) {
                    onFailure(e.message ?: "Log in failed")
                }
            }
        }
    }
}

@Composable
fun Navigation(
    apiViewModel: ApiViewModel,
    pathPropertiesViewModel: PathPropertiesViewModel,
    drawingInfoViewModel: DrawingInfoViewModel,
    capturableImageViewModel: CapturableImageViewModel,
    navController: NavHostController,
    createUserWithEmailAndPassword: (
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) -> Unit,
    signInWithEmailAndPassword: (
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) -> Unit,
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
    val navigateToGalleryScreen = {
        navController.navigate("gallery_screen")
    }
    val navigateToCanvasScreen = {
        navController.navigate("canvas_screen")
    }
    val navigateToPopBack = {
        navController.popBackStack()
    }

    val drawingInfoDataList by drawingInfoViewModel.allDrawingInfo.observeAsState()
    val currentUserDrawingHistory by apiViewModel.currentUserDrawingHistory.observeAsState()
    val currentUserExploreFeed by apiViewModel.currentUserExploreFeed.observeAsState()

    NavHost(navController = navController,
        startDestination = "splash_screen") {

        composable("login_screen") {
            LoginScreen(
                createUserWithEmailAndPassword,
                signInWithEmailAndPassword,
                navigateToGalleryScreen,
                apiViewModel::getCurrentUserDrawingHistory
            )
        }

        composable("splash_screen") {
            SplashScreen {
                val currentUser = Firebase.auth.currentUser
                val isSignedIn = currentUser != null
                if (isSignedIn) {
                    apiViewModel.getCurrentUserDrawingHistory(Firebase.auth.currentUser?.uid ?: "")
                    navController.navigate("gallery_screen")
                } else {
                    navController.navigate("login_screen")
                }
            }
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
fun SplashScreen(onSplashScreenComplete: ()-> Unit) {
    val scale = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        try {
            scale.animateTo(
                targetValue = 1f,
            )
            delay(1500)
        } catch (e: Exception) {
            Log.e("SplashScreen", "Splash screen image not loaded")
        } finally {
            coroutineScope.launch {
                onSplashScreenComplete()
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
