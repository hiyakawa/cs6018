package com.example.customviewdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paths = mutableListOf<CustomPath>()
    private lateinit var path : Path

    private val paint = Paint()

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 30f
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true
    }

    fun setPath(path: Path) {
        this.path = path
    }

    fun setWidth(width: Int) {
        this.paint.strokeWidth = width.toFloat()
    }

    fun getPaths(): List<CustomPath> {
        return paths
    }

    fun setPaths(customPaths: List<CustomPath>) {
        paths.clear()
        paths.addAll(customPaths)
        invalidate()
    }

    fun clearCanvas() {
        paths.clear()
        invalidate()
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun setWidth(width: Float) {
        paint.strokeWidth = width.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (customPath in paths) {
            paint.strokeWidth = customPath.strokeWidth
            canvas.drawPath(customPath.path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newPath = Path()
                newPath.moveTo(x, y)
                paths.add(CustomPath(newPath, paint.strokeWidth))
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val currentPath = paths.lastOrNull()?.path
                currentPath?.lineTo(x, y)
                invalidate()
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }
}
