package com.example.zadanie6

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

class Vec2<T>(var x:T,var y:T)

class Camera(val position : Vec2<Float>){
    var zoom = 2f
    var lerp = 0.33f

    fun approachTo(newPosition : Vec2<Float>, deltaTime: Float){
        val dx = newPosition.x - position.x
        position.x += dx * lerp * deltaTime

        val dy = newPosition.y - position.y
        position.y += dy * lerp * deltaTime
    }
}

interface Animable{
    fun changeAnimationIndex()
}

abstract class GameObject(var row: Int,var col: Int) {

    companion object{
        const val TILE_WIDTH = 32
        const val TILE_HEIGHT = 32
    }
    //lateinit var gameObjects : List<MutableList<GameObject>>
    lateinit var gameMap : GameMap
    lateinit var bitmap: Bitmap
    var isRemoved = false
    open fun draw(canvas: Canvas, camera:Camera){
        drawOnCanvas(canvas,row,col,camera,null)
    }

    protected fun drawOnCanvas(canvas: Canvas, row: Int, col:Int, camera: Camera, srcRect:Rect? ){
        val zoom = camera.zoom
        val mapColumns = gameMap.columns
        val mapRows = gameMap.rows

        var offsetXUnscaled = (camera.position.x-mapColumns/2)
        var offsetYUnscaled = camera.position.y-mapRows/2
        offsetXUnscaled = offsetXUnscaled.coerceIn(0f, mapColumns.toFloat())
        offsetYUnscaled = offsetYUnscaled.coerceIn(0f,mapRows.toFloat())

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
        //gameObjects[other.row][other.col] = this
        //gameObjects[this.row][this.col] = other
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
        val goEmpty = Empty(row,col)
        goEmpty.gameMap = gameMap
        gameMap.set(row,col,goEmpty)
        println(javaClass)
        isRemoved = true

        //gameObjects[other.row][other.col] = goEmpty
    }
    open fun onUpdate(deltaTime: Float){}
}

class Rockford(row: Int,col: Int) : GameObject(row,col), Animable{
    private var bmpIndex = 0
    private var animationSetIndex = 0
    private var bmpIndexMax = -1

    var points = 0


    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.rockford
        bmpIndexMax = bitmap.width / TILE_WIDTH
    }

    override fun draw(canvas: Canvas, camera:Camera){
        val rectLeft = bmpIndex * TILE_WIDTH
        val rectTop = animationSetIndex * TILE_HEIGHT
        val rectRight = rectLeft + TILE_WIDTH
        val rectBottom = rectTop + TILE_HEIGHT
        val srcRect = Rect(rectLeft,rectTop,rectRight,rectBottom)
        drawOnCanvas(canvas,row,col,camera,srcRect)
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
            is Empty -> {
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
            is Rock -> {}
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
class Empty(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.empty
    }
}
class Wall(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.wall
    }
}
class Border(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.border
    }
}
class Ground(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.ground
    }
}
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
        drawOnCanvas(canvas,row,col,camera,srcRect)
    }

    override fun changeAnimationIndex() {
        bmpIndex++
        if(bmpIndex>=bmpIndexMax){
            bmpIndex = 0
        }
    }
}
class Butterfly(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.butterfly
    }
}


class Rock(row: Int,col: Int) : GameObject(row,col){
    init {
        val bmpLib = BitmapLibrary
        bitmap = bmpLib.rock
    }
    private var isFalling = false


    override fun onUpdate(deltaTime: Float) {
        if(gameMap.at(row+1,col) is Empty){
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
                return
            }
        }

        if(gameMap.at(row,col+1) is Empty && gameMap.at(row+1,col+1) is Empty){
            // Right
            val other = gameMap.at(row,col+1)
            swap(other)
            isFalling = true
            return
        }
        if(gameMap.at(row,col-1) is Empty && gameMap.at(row+1,col-1) is Empty){
            // Left
            val other = gameMap.at(row,col-1)
            swap(other)
            isFalling = true
            return
        }
        isFalling = false
    }

}