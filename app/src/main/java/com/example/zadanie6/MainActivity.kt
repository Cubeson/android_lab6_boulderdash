package com.example.zadanie6

import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zadanie6.map1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //val main = findViewById<ConstraintLayout>(R.id.main)
        val gameMap = createMap()
        gameView = GameView(this,gameMap)
        //main.addView(gameView)
        setContentView(gameView)
    }
    private lateinit var gameView : GameView

    //private fun createMap() : List<MutableList<GameObject>>{
    private fun createMap() : GameMap{

        val stringGameMap = map1.split('\n')
        //val gameObjectGrid = mutableListOf<MutableList<GameObject>>()
        val gameObjects = mutableListOf<GameObject>()

        val options = Options()
        options.inScaled = false
        val bmpLib = BitmapLibrary
        if(!bmpLib.isInitialized){
            bmpLib.initialize(resources,options)
        }

        for(row in stringGameMap.indices){
            //val rowList = mutableListOf<GameObject>()
            //gameObjectGrid.add(rowList)
            for(col in stringGameMap[row].indices){
                val c = stringGameMap[row][col]

                val gameObject: GameObject = when(c){
                    'W' -> Border(row,col)
                    'w' -> Wall(row,col)
                    ' ' -> Empty(row,col)
                    '.' -> Ground(row,col)
                    'X' -> Butterfly(row,col)
                    'd' -> Diamond(row,col)
                    'r' -> Rock(row,col)
                    'P' -> Rockford(row,col)
                    else -> Empty(row,col)
                }
                gameObjects.add(gameObject)
            }
        }
        val columns = stringGameMap[0].length
        val rows = stringGameMap.size
        val gameMap = GameMap(columns,rows,gameObjects)
        for (gameObject in gameObjects){
            gameObject.gameMap = gameMap
        }
        return gameMap
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }
}