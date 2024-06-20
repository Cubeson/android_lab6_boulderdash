package com.example.zadanie6

import com.example.zadanie6.gameObjects.GameObject

class GameMap(val columns: Int, val rows:Int, val gameObjects: MutableList<GameObject>){
    fun getColumn(index: Int): Int{
        return index % columns
    }
    fun getRow(index: Int): Int{
        return index / columns
    }
    fun getIndex(row: Int, column:Int) : Int{
        return (row * columns) + column
    }

    fun at(index: Int) : GameObject{
        return gameObjects[index]
    }
    fun at(row: Int, column: Int) : GameObject{
        return at(getIndex(row,column))
    }
    fun set(index: Int, gameObject: GameObject){
        gameObjects[index] = gameObject
    }
    fun set(row: Int,column: Int, gameObject: GameObject){
        gameObjects[getIndex(row,column)] = gameObject
    }

    fun getCopy() : List<GameObject>{
        return gameObjects.toList()
    }


}