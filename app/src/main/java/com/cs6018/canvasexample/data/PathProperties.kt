package com.cs6018.canvasexample.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import com.cs6018.canvasexample.ui.theme.NudeBlue

class PathProperties(
    var strokeWidth: Float = 50f,
    var color: Color = NudeBlue,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
//    var eraseMode: Boolean = false
) {

    fun copy(
        strokeWidth: Float = this.strokeWidth,
        color: Color = this.color,
        strokeCap: StrokeCap = this.strokeCap,
        strokeJoin: StrokeJoin = this.strokeJoin,
//        eraseMode: Boolean = this.eraseMode
    ) = PathProperties(
        strokeWidth, color, strokeCap, strokeJoin
//        , eraseMode
    )
}