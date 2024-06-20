package com.example.zadanie6.gameObjects

import com.example.zadanie6.BitmapLibrary
import com.example.zadanie6.FinishedStatus
import com.example.zadanie6.GameState

class Rock(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.rock
    }
    private var isFalling = false


    override fun onUpdate(deltaTime: Float) {
        if(gameMap.at(row+1,col) is Floor){
            // Down
            val other = gameMap.at(row+1,col)
            isFalling = true
            swap(other)
            return
        }
        if(gameMap.at(row+1,col) is Rockford){
            // Down Rockford and falling
            if(isFalling) {
                val other = gameMap.at(row+1,col)
                swap(other)
                other.replaceWithEmptyTile()
                GameState.finishedStatus = FinishedStatus.LOSS
                return
            }
        }

        if( gameMap.at(row+1,col) is Rock &&
            gameMap.at(row,col+1) is Floor &&
            gameMap.at(row+1,col+1) is Floor){
            // Right
            val other = gameMap.at(row,col+1)
            swap(other)
            isFalling = true
            return
        }
        if( gameMap.at(row+1,col) is Rock &&
            gameMap.at(row,col-1) is Floor &&
            gameMap.at(row+1,col-1) is Floor){
            // Left
            val other = gameMap.at(row,col-1)
            swap(other)
            isFalling = true
            return
        }
        isFalling = false
    }
}