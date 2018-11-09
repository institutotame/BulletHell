package com.atinem.bullethell

import android.graphics.RectF
import android.util.Log

class Bullet(screenX : Float) {

    val mRect = RectF()

    private var mXVelocity = screenX / 5
    private var mYVelocity = screenX / 5

    private val mWidth : Float = screenX / 100
    private val mHeight : Float = screenX / 100

    fun update(fps : Long){
        mRect.left = mRect.left + (mXVelocity / fps)
        mRect.top = mRect.top + (mYVelocity / fps)

        mRect.right = mRect.left + mWidth
        mRect.bottom = mRect.top - mHeight
    }

    fun reverseYVelocity(){
        mYVelocity = -mYVelocity
    }

    fun reverseXVelocity(){
        mXVelocity = -mXVelocity
    }

    fun spawn(pX : Float, pY : Float, vX : Float, vY : Float){
        Log.d("Bullet", "SpawnMethod")
        mRect.left = pX
        mRect.top = pY
        mRect.right = pX + mWidth
        mRect.bottom = pY + mHeight

        mXVelocity *= vX
        mYVelocity *= vY
    }
}