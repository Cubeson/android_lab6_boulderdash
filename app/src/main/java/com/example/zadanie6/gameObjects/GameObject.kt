package com.example.zadanie6.gameObjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.example.zadanie6.Camera
import com.example.zadanie6.GameMap

interface Animable{
    fun changeAnimationIndex()
}
abstract class GameObject(var row: Int,var col: Int) {

    companion object{
        const val TILE_WIDTH = 32
        const val TILE_HEIGHT = 32
    }
    lateinit var gameMap : GameMap
    lateinit var bitmap: Bitmap
    var isRemoved = false
    open fun draw(canvas: Canvas, camera: Camera){
        drawOnCanvas(canvas,camera,null)
    }

    protected fun drawOnCanvas(canvas: Canvas, camera: Camera, srcRect: Rect? ){
        val zoom = camera.zoom
        val mapColumns = gameMap.columns
        val mapRows = gameMap.rows

        var offsetXUnscaled = (camera.position.x-mapColumns/2)
        var offsetYUnscaled = camera.position.y-mapRows/2
        //offsetXUnscaled = offsetXUnscaled.coerceIn(0f, mapColumns.toFloat())
        //offsetYUnscaled = offsetYUnscaled.coerceIn(0f,mapRows.toFloat())

        val offsetX = (offsetXUnscaled * TILE_WIDTH).toInt()
        val offsetY = (offsetYUnscaled * TILE_HEIGHT).toInt()

        val tileWidthLocal = (TILE_WIDTH * zoom).toInt()
        val tileHeightLocal = (TILE_HEIGHT * zoom).toInt()

        val posX = (col * tileWidthLocal) - (offsetX*zoom).toInt()
        val posY = (row * tileHeightLocal) - (offsetY*zoom).toInt()

        val destRect = Rect(posX,posY,posX+tileWidthLocal,posY+tileHeightLocal)
        canvas.drawBitmap(bitmap,srcRect,destRect,null)
    }
    fun swap(other: GameObject){
        gameMap.set(other.row,other.col,this)
        gameMap.set(this.row,this.col,other)

        val tmpCol = this.col
        val tmpRow = this.row

        this.col = other.col
        this.row = other.row

        other.col = tmpCol
        other.row = tmpRow
    }
    fun replaceWithEmptyTile(){
        val goFloor = Floor(row,col)
        goFloor.gameMap = gameMap
        gameMap.set(row,col,goFloor)
        isRemoved = true

        //gameObjects[other.row][other.col] = goEmpty
    }
    open fun onUpdate(deltaTime: Float){}
}