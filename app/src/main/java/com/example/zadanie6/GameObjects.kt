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

open class GameObject(private val bitMap: Bitmap) {

    companion object{
        const val TILE_WIDTH = 32
        const val TILE_HEIGHT = 32
    }
    lateinit var gameObjects : List<MutableList<GameObject>>
    val mapColumns : Int
        get(){
            return gameObjects[0].size
        }
    val mapRows : Int
        get(){
            return gameObjects.size
        }
    var row = 0
    var col = 0
    var markedForDestruction = false
    open fun draw(canvas: Canvas, row:Int, col:Int, camera:Camera){
        drawOnCanvas(canvas,row,col,camera,null)
    }
    protected fun drawOnCanvas(canvas: Canvas, row: Int, col:Int, camera: Camera, srcRect:Rect? ){
        val zoom = camera.zoom


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
        canvas.drawBitmap(bitMap,srcRect,destRect,null)
    }
    fun swap(other: GameObject){
        gameObjects[other.row][other.col] = this
        gameObjects[this.row][this.col] = other

        val tmpCol = this.col
        val tmpRow = this.row

        this.col = other.col
        this.row = other.row

        other.col = tmpCol
        other.row = tmpRow
    }
    open fun onDestruction(){}
    open fun onUpdate(deltaTime: Float){}
}

class Rockford(bitmap: Bitmap) : GameObject(bitmap), Animable{
    private var bmpIndex = 0
    private var animationSetIndex = 0
    private var bmpIndexMax = -1


    init {
        bmpIndexMax = bitmap.width / TILE_WIDTH
    }

    override fun draw(canvas: Canvas, row:Int, col:Int, camera:Camera){
        val rectLeft = bmpIndex * TILE_WIDTH
        val rectTop = animationSetIndex * TILE_HEIGHT
        val rectRight = rectLeft + TILE_WIDTH
        val rectBottom = rectTop + TILE_HEIGHT
        val srcRect = Rect(rectLeft,rectTop,rectRight,rectBottom)
        println(bmpIndex)
        drawOnCanvas(canvas,row,col,camera,srcRect)
    }
    override fun onUpdate(deltaTime: Float){

    }
    fun moveUp(){
        if(row-1 >= mapRows) return
        val other = gameObjects[row-1][col]
        swap(other)
    }
    fun moveDown(){
        if(row+1 >= mapRows) return
        val other = gameObjects[row+1][col]
        swap(other)
    }
    fun moveRight(){
        if(col+1 >= mapColumns) return
        val other = gameObjects[row][col+1]
        swap(other)
    }
    fun moveLeft(){
        if(col-1 < 0) return
        val other = gameObjects[row][col-1]
        swap(other)
    }
    override fun changeAnimationIndex(){
        bmpIndex++
        if (bmpIndex>=bmpIndexMax){
            bmpIndex = 0
        }
    }
}
class Empty(bitMap: Bitmap) : GameObject(bitMap)
class Wall(bitMap: Bitmap) : GameObject(bitMap)
class Border(bitMap: Bitmap) : GameObject(bitMap)
class Ground(bitMap: Bitmap) : GameObject(bitMap)
class Diamond(bitMap: Bitmap) : GameObject(bitMap)
class Butterfly(bitMap: Bitmap) : GameObject(bitMap)
class Mob(bitMap: Bitmap) : GameObject(bitMap)
class Door(bitMap: Bitmap) : GameObject(bitMap)


class Rock(bitMap: Bitmap) : GameObject(bitMap){
    override fun onUpdate(deltaTime: Float) {
        super.onUpdate(deltaTime)
        if(row+1 > mapRows) return
        val other = gameObjects[row+1][col]
        if(other is Empty){
            swap(other)
        }else if(other is Rockford){
            swap(other)
            other.markedForDestruction = true

        }

    }
}