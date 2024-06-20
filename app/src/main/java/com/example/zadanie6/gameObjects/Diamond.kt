package com.example.zadanie6.gameObjects

import android.graphics.Canvas
import android.graphics.Rect
import com.example.zadanie6.BitmapLibrary
import com.example.zadanie6.Camera

class Diamond(row: Int,col: Int) : GameObject(row,col), Animable{
    private var bmpIndex = 0
    private var bmpIndexMax = 0
    init {
        bitmap = BitmapLibrary.diamond
        bmpIndexMax = bitmap.width / TILE_WIDTH
    }


    override fun draw(canvas: Canvas, camera: Camera) {
        val rectLeft = bmpIndex * TILE_WIDTH
        val rectTop = 0 * TILE_HEIGHT
        val rectRight = rectLeft + TILE_WIDTH
        val rectBottom = rectTop + TILE_HEIGHT
        val srcRect = Rect(rectLeft,rectTop,rectRight,rectBottom)
        drawOnCanvas(canvas,camera,srcRect)
    }

    override fun changeAnimationIndex() {
        bmpIndex++
        if(bmpIndex>=bmpIndexMax){
            bmpIndex = 0
        }
    }
}