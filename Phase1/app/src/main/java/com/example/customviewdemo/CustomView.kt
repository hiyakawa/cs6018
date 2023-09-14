package com.example.customviewdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paths = mutableListOf<CustomPath>()
    private lateinit var path : Path
    private val paint = Paint()
    private var eraserMode = false

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 30f
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true
        paint.color = Color.BLACK
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
        paint.strokeWidth = width
    }

    fun toggleEraserMode(){
        eraserMode = !eraserMode
        if(eraserMode){
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }else{
            paint.xfermode = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (customPath in paths) {
            paint.strokeWidth = customPath.strokeWidth
            if (customPath.eraserMode) {
                paint.color = Color.TRANSPARENT
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else {
                paint.color = customPath.color
                paint.xfermode = null
            }
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
                val customPath = CustomPath(newPath, paint.strokeWidth, paint.color, eraserMode)
                paths.add(customPath)
                if (eraserMode) {
                    customPath.color = Color.TRANSPARENT
                }
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
