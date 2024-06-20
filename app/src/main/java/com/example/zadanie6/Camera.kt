package com.example.zadanie6

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