package com.example.zadanie6

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class GameButton(private val rect:Rect, var movementType: RockfordMovement, val bitmap: Bitmap){
    fun draw(canvas: Canvas){
        val bmpLib = BitmapLibrary
        canvas.drawBitmap(bitmap,null,rect,null)
    }
    fun isClickInside(x:Int,y:Int) : Boolean{
        return(rect.contains(x,y))
    }
}