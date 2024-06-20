package com.example.zadanie6

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.example.zadanie6.gameObjects.Animable
import com.example.zadanie6.gameObjects.Rockford

enum class RockfordMovement{
    TOP,
    RIGHT,
    LEFT,
    BOTTOM,
    STOP
}
enum class FinishedStatus{
    PLAYING,
    VICTORY,
    LOSS
}
object GameState{
    fun initialize(){
        finishedStatus = FinishedStatus.PLAYING
        ready = false
        running = false
    }
    var finishedStatus = FinishedStatus.PLAYING
    var ready = false
    var running = false
}
class GameView(context: Context?, private val gameMap: GameMap ) : SurfaceView(context), Runnable {

    private var lastFrame = 0L
    private var deltaTime = 0f

    private val tps = 4
    private var tpsAccumulator = 0f

    private var animationChangeAccumulator = 0f

    private val camera = Camera(Vec2(0f,0f))

    private val buttons : MutableList<GameButton> = mutableListOf()

    private lateinit var gameThread : Thread

    var movement : RockfordMovement = RockfordMovement.STOP
    private var touchPosition : Vec2<Float>? = null

    init{
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if(!GameState.ready){
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

        val buttonMargin = 50
        val buttonSpacing = 35

        val buttonWidth = (width * 0.10).toInt()
        val buttonHeight = (height *0.10).toInt()

        val buttonTop =     GameButton( Rect(buttonMargin,(3*height/4),buttonMargin+buttonWidth,(3*height/4)+buttonHeight),RockfordMovement.TOP,BitmapLibrary.arrow_up)

        val buttonBot =     GameButton( Rect(buttonMargin,(3*height/4)+buttonHeight+buttonSpacing,buttonMargin+buttonWidth,(3*height/4)+buttonHeight+buttonSpacing+buttonHeight),RockfordMovement.BOTTOM,BitmapLibrary.arrow_down)

        val buttonRight =   GameButton( Rect(width-buttonMargin-buttonWidth,(3*height/4),width-buttonMargin,(3*height/4)+buttonHeight),RockfordMovement.RIGHT,BitmapLibrary.arrow_right)

        val buttonLeft =    GameButton( Rect(width-buttonMargin-buttonWidth-buttonMargin-buttonWidth,(3*height/4),width-buttonMargin-buttonWidth-buttonMargin,(3*height/4)+buttonHeight),RockfordMovement.LEFT,BitmapLibrary.arrow_left)


        buttons.add(buttonTop)
        buttons.add(buttonBot)
        buttons.add(buttonRight)
        buttons.add(buttonLeft)


        lastFrame = SystemClock.uptimeMillis()
        val rockford = gameMap.gameObjects.singleOrNull{ g -> g is Rockford} as Rockford?
            ?: throw Exception("No Rockford GameObject found on this map")
        camera.position.x = rockford.col.toFloat()
        camera.position.y = rockford.row.toFloat()
        GameState.ready = true
        resume()
    }


    private fun getDeltaTimeInSeconds() : Float{
        val now = SystemClock.uptimeMillis()
        val deltaTimeInMillis = now - lastFrame
        lastFrame = now
        return deltaTimeInMillis / 1000f
    }
    private var changeMapThread : Thread? = null
    override fun run() {
        while(GameState.running){
            if(!GameState.ready){
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
            if(GameState.finishedStatus == FinishedStatus.PLAYING){
                gameLoop(rockford)
                if(rockford != null){
                    camera.approachTo(Vec2(rockford.col.toFloat(),rockford.row.toFloat()),deltaTime)
                }
            }
            draw()
        }
    }

    private fun draw(){
        val canvas = holder.lockCanvas()
        canvas.drawColor(ContextCompat.getColor(context,R.color.black))

        for (gameObject in gameMap.gameObjects){
            gameObject.draw(canvas,camera)
        }

        for (btn in buttons){
            btn.draw(canvas)
        }
        when(GameState.finishedStatus){
            FinishedStatus.LOSS -> {
                drawGameOver(canvas)
            }
            FinishedStatus.VICTORY ->{
                drawVictory(canvas)
            }
            else -> {}
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
    private fun drawVictory(canvas:Canvas){
        val paintText = Paint()
        val paintColor = Color.rgb(0,255,0)
        paintText.setColor(paintColor)
        paintText.textSize = 150f
        paintText.textAlign = Paint.Align.CENTER
        val xPos = width/2f
        val yPos = height/2f
        canvas.drawText("VICTORY",xPos,yPos,paintText)
    }
    private fun gameLoop(rockford: Rockford?){

        tpsAccumulator += deltaTime
        if(tpsAccumulator < 1f/tps){
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
        this.parent.requestDisallowInterceptTouchEvent(true)
        println(deltaTime)
        val x = event?.x
        val y = event?.y
        if(x != null && y != null){
            touchPosition = Vec2(x,y)
        }

        return true
    }

    fun resume(){
        GameState.running = true
        gameThread = Thread(this)
        gameThread.start()
    }
    fun pause(){
        GameState.running = false
        try{
            gameThread.join()
        } catch (e : InterruptedException){
            e.printStackTrace()
        }
    }
}