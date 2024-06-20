package com.example.zadanie6

import android.graphics.BitmapFactory.Options
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zadanie6.gameObjects.Border
import com.example.zadanie6.gameObjects.Butterfly
import com.example.zadanie6.gameObjects.Diamond
import com.example.zadanie6.gameObjects.Floor
import com.example.zadanie6.gameObjects.Exit
import com.example.zadanie6.gameObjects.GameObject
import com.example.zadanie6.gameObjects.Ground
import com.example.zadanie6.gameObjects.Rock
import com.example.zadanie6.gameObjects.Rockford
import com.example.zadanie6.gameObjects.Wall

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
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN

        }
        val options = Options()
        options.inScaled = false
        BitmapLibrary.initialize(resources,options)
        val gameMap = createMap()
        gameView = GameView(this,gameMap)
        setContentView(gameView)
    }
    private lateinit var gameView : GameView

    private fun createMap() : GameMap{

        val stringGameMap = map1.split('\n')
        val gameObjects = mutableListOf<GameObject>()

        for(row in stringGameMap.indices){
            for(col in stringGameMap[row].indices){
                val c = stringGameMap[row][col]

                val gameObject: GameObject = when(c){
                    'W' -> Border(row,col)
                    'w' -> Wall(row,col)
                    ' ' -> Floor(row,col)
                    '.' -> Ground(row,col)
                    'X' -> Butterfly(row,col)
                    'd' -> Diamond(row,col)
                    'r' -> Rock(row,col)
                    'P' -> Rockford(row,col)
                    'E' -> Exit(row,col)
                    else -> Floor(row,col)
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