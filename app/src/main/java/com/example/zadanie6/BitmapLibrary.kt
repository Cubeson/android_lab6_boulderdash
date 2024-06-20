package com.example.zadanie6

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options

object BitmapLibrary{
    private var _isInitialized = false
    val isInitialized : Boolean
        get() = _isInitialized
    fun initialize(resources: Resources,options: Options){
        border = BitmapFactory.decodeResource(resources,R.drawable.border_32x32,options)
        wall = BitmapFactory.decodeResource(resources,R.drawable.wall_32x32,options)
        empty = BitmapFactory.decodeResource(resources,R.drawable.empty_32x32,options)
        ground = BitmapFactory.decodeResource(resources,R.drawable.ground_32x32,options)
        butterfly = BitmapFactory.decodeResource(resources,R.drawable.butterfly_32x32,options)
        diamond = BitmapFactory.decodeResource(resources,R.drawable.diamond_32x32,options)
        rock = BitmapFactory.decodeResource(resources,R.drawable.rock_32x32,options)
        rockford = BitmapFactory.decodeResource(resources,R.drawable.rockford_32x32,options)
        arrow_down = BitmapFactory.decodeResource(resources,R.drawable.arrow_down)
        arrow_up = BitmapFactory.decodeResource(resources,R.drawable.arrow_up)
        arrow_left = BitmapFactory.decodeResource(resources,R.drawable.arrow_left)
        arrow_right = BitmapFactory.decodeResource(resources,R.drawable.arrow_right)
        exit = BitmapFactory.decodeResource(resources,R.drawable.exit)
        _isInitialized = true
    }
    lateinit var border : Bitmap
    lateinit var wall : Bitmap
    lateinit var empty : Bitmap
    lateinit var ground : Bitmap
    lateinit var butterfly : Bitmap
    lateinit var diamond : Bitmap
    lateinit var rock : Bitmap
    lateinit var rockford : Bitmap
    lateinit var exit : Bitmap

    lateinit var arrow_down : Bitmap
    lateinit var arrow_up : Bitmap
    lateinit var arrow_left : Bitmap
    lateinit var arrow_right : Bitmap
}
