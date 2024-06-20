package com.example.zadanie6.gameObjects

import com.example.zadanie6.BitmapLibrary

class Wall(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.wall
    }
}