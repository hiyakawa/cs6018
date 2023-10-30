package com.cs6018.canvasexample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.cs6018.canvasexample.ui.theme.CanvasColor
import com.cs6018.canvasexample.ui.theme.IcareBlack
import com.cs6018.canvasexample.ui.theme.IcareBlue
import com.cs6018.canvasexample.ui.theme.IcareRed
import com.cs6018.canvasexample.ui.theme.IcareYellow
import com.cs6018.canvasexample.ui.theme.LightNudeBlue
import com.cs6018.canvasexample.ui.theme.NudeBlue

@Composable
fun BrushColorScreen(
    updateHexColorCode: (Color) -> Unit,
    updateCurrentPathProperty: (newColor: Color?, newStrokeWidth: Float?) -> Unit,
    navigateToPopBack: () -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 50.dp, top = 100.dp, end = 50.dp, bottom = 100.dp)
    ) {
        Row {
            Text(text = "The Blue Nude Palette",
                fontFamily = FontFamily.Serif,
                modifier = Modifier.fillMaxWidth(),
                color = NudeBlue)
        }
        Spacer(Modifier.size(50.dp))
        Row {
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(Color.White)
                    updateCurrentPathProperty(Color.White,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(CanvasColor)
                    updateCurrentPathProperty(CanvasColor,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CanvasColor)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(LightNudeBlue)
                    updateCurrentPathProperty(LightNudeBlue,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightNudeBlue)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(NudeBlue)
                    updateCurrentPathProperty(NudeBlue,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NudeBlue)) {
            }
        }
        Spacer(Modifier.size(100.dp))
        Row {
            Text(text = "The Icare Palette",
                fontFamily = FontFamily.Serif,
                modifier = Modifier.fillMaxWidth(),
                color = IcareBlack)
        }
        Spacer(Modifier.size(50.dp))
        Row {
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(IcareRed)
                    updateCurrentPathProperty(IcareRed,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareRed)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(IcareYellow)
                    updateCurrentPathProperty(IcareYellow,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareYellow)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(IcareBlue)
                    updateCurrentPathProperty(IcareBlue,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareBlue)) {
            }
            Button(shape = RectangleShape,
                onClick = {
                    updateHexColorCode(IcareBlack)
                    updateCurrentPathProperty(IcareBlack,null)
                    navigateToPopBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IcareBlack)) {
            }
        }
    }
}