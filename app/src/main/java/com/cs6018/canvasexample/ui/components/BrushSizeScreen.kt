package com.cs6018.canvasexample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cs6018.canvasexample.data.PathProperties
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import com.cs6018.canvasexample.ui.theme.NudeBlue

@Composable
fun BrushSizeScreen(
    currentPathProperty: PathProperties,
    updateCurrentPathProperty: (newColor: Color?, newStrokeWidth: Float?) -> Unit,
    navigateToPopBack: () -> Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 50.dp, top = 100.dp, end = 50.dp, bottom = 100.dp)
    ) {
        Text(
            text = "Pick your brush size",
            fontFamily = FontFamily.Serif,
            modifier = Modifier.fillMaxWidth(),
            color = NudeBlue
        )
        Spacer(Modifier.size(50.dp))
        Slider(
            value = currentPathProperty.strokeWidth,
            onValueChange = {
                updateCurrentPathProperty(null, it)
            },
            valueRange = 10f..100f,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.size(50.dp))
        Button(shape = RectangleShape,
            onClick = {
            navigateToPopBack()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = NudeBlue
            )) {
            Text(text = "Done",
                fontFamily = FontFamily.Serif)
        }
    }
}