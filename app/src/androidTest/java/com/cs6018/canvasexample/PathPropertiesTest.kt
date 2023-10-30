package com.cs6018.canvasexample

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cs6018.canvasexample.model.PathProperties
import com.cs6018.canvasexample.ui.theme.NudeBlue
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PathPropertiesTest {
    @Test
    fun testInitialization() {
        val pathProperties = PathProperties()
        Assert.assertEquals(50f, pathProperties.strokeWidth)
        Assert.assertEquals(NudeBlue, pathProperties.color)
        Assert.assertEquals(StrokeCap.Round, pathProperties.strokeCap)
        Assert.assertEquals(StrokeJoin.Round, pathProperties.strokeJoin)
    }
}