package com.example.zadanie6

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class GameButton(private val rect:Rect, var movementType: RockfordMovement, val bitmap: Bitmap){
    private var paint:Paint = Paint()

    init {
        val color = Color.argb(128,255,255,255)
        paint.setColor(color)
    }
    fun draw(canvas: Canvas){

        canvas.drawBitmap(bitmap,null,rect,paint)
    }
    fun isClickInside(x:Int,y:Int) : Boolean{
        return(rect.contains(x,y))
    }
}