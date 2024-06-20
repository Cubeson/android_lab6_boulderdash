package com.example.zadanie6.gameObjects

import android.graphics.Canvas
import android.graphics.Rect
import com.example.zadanie6.BitmapLibrary
import com.example.zadanie6.Camera
import com.example.zadanie6.FinishedStatus
import com.example.zadanie6.GameState

class Rockford(row: Int,col: Int) : GameObject(row,col), Animable {
    private var bmpIndex = 0
    private var animationSetIndex = 0
    private var bmpIndexMax = -1

    var points = 0


    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.rockford
        bmpIndexMax = bitmap.width / TILE_WIDTH
    }

    override fun draw(canvas: Canvas, camera: Camera){
        val rectLeft = bmpIndex * TILE_WIDTH
        val rectTop = animationSetIndex * TILE_HEIGHT
        val rectRight = rectLeft + TILE_WIDTH
        val rectBottom = rectTop + TILE_HEIGHT
        val srcRect = Rect(rectLeft,rectTop,rectRight,rectBottom)
        drawOnCanvas(canvas,camera,srcRect)
    }
    override fun onUpdate(deltaTime: Float){

    }
    fun moveUp(){
        if(row-1 < 0) return
        val other = gameMap.at(row-1,col)
        animationSetIndex = 0
        onMove(other)
    }
    fun moveDown(){
        if(row+1 >= gameMap.rows) return
        val other = gameMap.at(row+1,col)
        animationSetIndex = 0
        onMove(other)
    }
    fun moveRight(){
        if(col+1 >= gameMap.columns) return
        val other = gameMap.at(row,col+1)
        animationSetIndex = 2
        onMove(other)
    }
    fun moveLeft(){
        if(col-1 < 0) return
        val other = gameMap.at(row,col-1)
        animationSetIndex = 1
        onMove(other)
    }
    fun stop() {
        animationSetIndex = 3
    }

    private fun onMove(other: GameObject){

        when(other){
            is Floor -> {
                swap(other)
            }
            is Wall -> {}
            is Border -> {}
            is Ground -> {
                swap(other)
                other.replaceWithEmptyTile()
            }
            is Diamond -> {
                swap(other)
                other.replaceWithEmptyTile()
                points++
            }
            is Butterfly -> {}
            is Rock -> {
                //val dy = row - other.row // positive: down, negative: up
                val dx = col - other.col // positive: left, negative: right
                //val pushRow = other.row - dy
                val pushCol = other.col - dx
                val objectToPush = gameMap.at(other.row,pushCol)
                if(objectToPush is Floor){
                    other.swap(objectToPush)
                    swap(objectToPush)
                    // Example:
                    // Floor,Rock,Player ---> Rock,Player,Floor
                }

            }
            is Exit -> {
                val exit = other as Exit
                if(exit.isOpen){
                    swap(other)
                    other.replaceWithEmptyTile()
                    GameState.finishedStatus = FinishedStatus.VICTORY
                }
            }
            is Rockford -> {
                throw Exception("How did that happen?")
            }
        }
    }
    override fun changeAnimationIndex(){
        bmpIndex++
        if (bmpIndex>=bmpIndexMax){
            bmpIndex = 0
        }
    }
}