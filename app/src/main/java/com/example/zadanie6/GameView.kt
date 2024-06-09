package com.example.zadanie6

import android.content.Context
import android.graphics.Canvas
import android.os.SystemClock
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat

class GameView(context: Context?, private val gameObjects: List<MutableList<GameObject>> ) : SurfaceView(context), Runnable {

    private var ready = false
    private var running = false

    private var lastFrame = 0L
    private var deltaTime = 0f

    private val tps = 2
    private var tpsAccumulator = 0f

    private var animationChangeAccumulator = 0f

    private val camera = Camera(Vec2(0f,0f))
    private lateinit var rockford : Rockford
    //private lateinit var mapSize:Vec2<Int>

    private lateinit var gameThread : Thread
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
        lastFrame = SystemClock.uptimeMillis()
        rockford = gameObjects.flatten().first{g -> g is Rockford} as Rockford
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
            gameObjects.flatten().filter { g->g.markedForDestruction }.forEach { g->g.onDestruction() }
            gameLoop()
            camera.approachTo(Vec2(rockford.col.toFloat(),rockford.row.toFloat()),deltaTime)
            draw()


            //camera.zoom += deltaTime * 0.33f
            //if(camera.zoom >= 10f) camera.zoom = 1f
        }
    }

    private fun draw(){
        val canvas = holder.lockCanvas()
        canvas.drawColor(ContextCompat.getColor(context,R.color.black))
        for (row in gameObjects.indices){
            for(col in gameObjects[row].indices){
                gameObjects[row][col].draw(canvas,row,col,camera)
            }
        }
        holder.unlockCanvasAndPost(canvas)
    }
    private fun gameLoop(){

        animationChangeAccumulator += deltaTime
        if(animationChangeAccumulator > 1f){
            gameObjects.flatten().filter { g -> g is Animable }.forEach { g ->
                val a = g as Animable
                a.changeAnimationIndex()
            }
            animationChangeAccumulator = 0f
        }


        tpsAccumulator += deltaTime
        if(tpsAccumulator < 1f/tps){
            return
        }
        tpsAccumulator = 0f

        rockford.moveRight()

        for (row in gameObjects.indices){
            for(col in gameObjects[row].indices){
                gameObjects[row][col].onUpdate(deltaTime)
            }
        }
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