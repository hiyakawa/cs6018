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

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    init {
        // 在 CustomView 初始化时设置默认数据
        val customPath = CustomPath(Path(), paint.strokeWidth)
        paths.add(customPath)
    }

    fun setPath(path: Path) {
        // 将路径添加到列表中
        val customPath = CustomPath(path, paint.strokeWidth)
        paths.add(customPath)
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