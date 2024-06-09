package com.example.zadanie6

import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        val gameObjects = createMap()
        gameView = GameView(this,gameObjects)
        setContentView(gameView)
    }
    private lateinit var gameView : GameView

    private fun createMap() : List<MutableList<GameObject>>{

        val gameMap = map1.split('\n')
        val gameObjectGrid = mutableListOf<MutableList<GameObject>>()

        for(row in gameMap.indices){
            //println(row)
            val rowList = mutableListOf<GameObject>()
            gameObjectGrid.add(rowList)
            for(col in gameMap[row].indices){
                val c = gameMap[row][col]
                val options = Options()
                options.inScaled = false
                val gameObject: GameObject = when(c){
                    'W' -> Border(BitmapFactory.decodeResource(resources,R.drawable.border_32x32,options))
                    'w' -> Wall(BitmapFactory.decodeResource(resources,R.drawable.wall_32x32,options))
                    ' ' -> Empty(BitmapFactory.decodeResource(resources,R.drawable.empty_32x32,options))
                    '.' -> Ground(BitmapFactory.decodeResource(resources,R.drawable.ground_32x32,options))
                    'X' -> Butterfly(BitmapFactory.decodeResource(resources,R.drawable.butterfly_32x32,options))
                    'd' -> Diamond(BitmapFactory.decodeResource(resources,R.drawable.diamond_32x32,options))
                    'r' -> Rock(BitmapFactory.decodeResource(resources,R.drawable.rock_32x32,options))
                    'P' -> Rockford(BitmapFactory.decodeResource(resources,R.drawable.rockford_32x32,options))
                    else -> Empty(BitmapFactory.decodeResource(resources,R.drawable.empty_32x32,options))
                }
                //println("$c , $resourceId")
                gameObject.row = row
                gameObject.col = col
                gameObject.gameObjects = gameObjectGrid
                rowList.add(gameObject)
            }
        }
        return gameObjectGrid
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