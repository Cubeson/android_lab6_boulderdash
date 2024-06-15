package com.example.zadanie6

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat

enum class RockfordMovement{
    TOP,
    RIGHT,
    LEFT,
    BOTTOM,
    STOP
}
class GameView(context: Context?, private val gameMap: GameMap ) : SurfaceView(context), Runnable {

    private var ready = false
    private var running = false

    private var lastFrame = 0L
    private var deltaTime = 0f

    private val tps = 2
    private var tpsAccumulator = 0f

    private var animationChangeAccumulator = 0f

    private val camera = Camera(Vec2(0f,0f))
    //private lateinit var rockford : Rockford
    private var buttonHeight = 100
    private var buttonWidth = 100
    private val buttonMargin = 50
    private val buttonSpacing = 35

    private val buttons : MutableList<GameButton> = mutableListOf()

    private lateinit var gameThread : Thread

    var movement : RockfordMovement = RockfordMovement.STOP
    private var touchPosition : Vec2<Float>? = null

    init{
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if(!ready){
                    initializeGame()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {}

        })
    }

    private fun initializeGame(){

        //buttonWidth = (width * 0.15).toInt()
        //buttonHeight = (height *0.15).toInt()

        val buttonTop =     GameButton( Rect(buttonMargin,height/2,buttonMargin+buttonWidth,(height/2)+buttonHeight),RockfordMovement.TOP,BitmapLibrary.arrow_up)

        val buttonBot =     GameButton( Rect(buttonMargin,(height/2)+buttonHeight+buttonSpacing,buttonMargin+buttonWidth,(height/2)+buttonHeight+buttonSpacing+buttonHeight),RockfordMovement.BOTTOM,BitmapLibrary.arrow_down)

        val buttonRight =   GameButton( Rect(width-buttonMargin-buttonWidth,height/2,width-buttonMargin,(height/2)+buttonHeight),RockfordMovement.RIGHT,BitmapLibrary.arrow_right)

        val buttonLeft =    GameButton( Rect(width-buttonMargin-buttonWidth-buttonMargin-buttonWidth,height/2,width-buttonMargin-buttonWidth-buttonMargin,(height/2)+buttonHeight),RockfordMovement.LEFT,BitmapLibrary.arrow_left)


        buttons.add(buttonTop)
        buttons.add(buttonBot)
        buttons.add(buttonRight)
        buttons.add(buttonLeft)



        lastFrame = SystemClock.uptimeMillis()
        val rockford = gameMap.gameObjects.single{g -> g is Rockford} as Rockford
        camera.position.x = rockford.col.toFloat()
        camera.position.y = rockford.row.toFloat()
        ready = true
        resume()
    }


    private fun getDeltaTimeInSeconds() : Float{
        val now = SystemClock.uptimeMillis()
        val deltaTimeInMillis = now - lastFrame
        lastFrame = now
        //println(deltaTimeInMillis)
        return deltaTimeInMillis / 1000f
    }

    override fun run() {
        while(running){
            if(!ready){
                continue
            }
            deltaTime = getDeltaTimeInSeconds()

            if(touchPosition != null){
                for(btn in buttons){
                    if(btn.isClickInside(touchPosition!!.x.toInt(),touchPosition!!.y.toInt())){
                        movement = btn.movementType
                        break
                    }
                }
                touchPosition = null
            }
            animationChangeAccumulator += deltaTime
            if(animationChangeAccumulator > 0.5f){
                gameMap.gameObjects.filter { g -> g is Animable }.forEach { g ->
                    val a = g as Animable
                    a.changeAnimationIndex()
                }
                animationChangeAccumulator = 0f
            }
            val rockford = gameMap.gameObjects.singleOrNull{g -> g is Rockford} as Rockford?
            gameLoop(rockford)
            if(rockford != null){
                camera.approachTo(Vec2(rockford.col.toFloat(),rockford.row.toFloat()),deltaTime)
            }

            draw(rockford)


            //camera.zoom += deltaTime * 0.33f
            //if(camera.zoom >= 10f) camera.zoom = 1f
        }
    }

    private fun draw(rockford: Rockford?){
        val canvas = holder.lockCanvas()
        canvas.drawColor(ContextCompat.getColor(context,R.color.black))

        for (gameObject in gameMap.gameObjects){
            gameObject.draw(canvas,camera)
        }

        for (btn in buttons){
            btn.draw(canvas)
        }
        if(rockford == null){
            drawGameOver(canvas)
        }

        holder.unlockCanvasAndPost(canvas)
    }
    private fun drawGameOver(canvas:Canvas){
        val paintText = Paint()
        val paintColor = Color.rgb(255,0,0)
        paintText.setColor(paintColor)
        paintText.textSize = 150f
        paintText.textAlign = Paint.Align.CENTER
        val xPos = width/2f
        val yPos = height/2f
        canvas.drawText("GAME OVER",xPos,yPos,paintText)
    }
    private fun gameLoop(rockford: Rockford?){

        tpsAccumulator += deltaTime
        if(tpsAccumulator < 2f/tps){
            return
        }
        tpsAccumulator = 0f

        if(rockford != null){
            when(movement){
                RockfordMovement.LEFT -> rockford.moveLeft()
                RockfordMovement.RIGHT -> rockford.moveRight()
                RockfordMovement.TOP -> rockford.moveUp()
                RockfordMovement.BOTTOM -> rockford.moveDown()
                RockfordMovement.STOP -> rockford.stop()
            }
        }
        movement = RockfordMovement.STOP

        for (gameObject in gameMap.getCopy()){
            if(!gameObject.isRemoved){
                gameObject.onUpdate(deltaTime)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        if(x != null && y != null){
            touchPosition = Vec2(x,y)
        }

        return super.onTouchEvent(event)
    }

    fun resume(){
        running = true
        gameThread = Thread(this)
        gameThread.start()
    }
    fun pause(){
        running = false
        try{
            gameThread.join()
        } catch (e : InterruptedException){
            e.printStackTrace()
        }
    }
}