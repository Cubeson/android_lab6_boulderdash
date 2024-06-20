package com.example.zadanie6.gameObjects

import android.graphics.Canvas
import android.graphics.Rect
import com.example.zadanie6.BitmapLibrary
import com.example.zadanie6.Camera

class Exit(row: Int, col: Int) : GameObject(row, col){
    init {
        bitmap = BitmapLibrary.exit
    }
    private var _isOpen = false
    val isOpen get() = _isOpen
    override fun onUpdate(deltaTime: Float) {

        if(!_isOpen && !gameMap.gameObjects.any { g -> g is Diamond }){
            open()
        }
    }
    private fun open(){
        _isOpen = true
    }

    override fun draw(canvas: Canvas, camera: Camera) {
        var bmpIndex = 0
        if(_isOpen){
            bmpIndex = 1
        }
        val rectLeft = bmpIndex * TILE_WIDTH
        val rectTop = 0 * TILE_HEIGHT
        val rectRight = rectLeft + TILE_WIDTH
        val rectBottom = rectTop + TILE_HEIGHT
        val srcRect = Rect(rectLeft,rectTop,rectRight,rectBottom)
        drawOnCanvas(canvas,camera,srcRect)
    }
}