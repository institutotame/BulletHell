package com.atinem.bullethell

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Bob(res : Resources?, screenX : Float, screenY : Float) {


    val mBobHeight = screenY / 10
    val mBobWidth = mBobHeight / 2

    val mRect = RectF(screenX/2, screenY/2, (screenX/2)+mBobWidth, (screenY/2)+mBobHeight)
    var mTeleporting = false

    var mBitmap : Bitmap? = null

    init {
        GlobalScope.launch {
            mBitmap = BitmapFactory.decodeResource(res,R.drawable.bob)
        }
    }

    fun teleport(newX : Float, newY : Float) : Boolean{
        var success = false
        if(!mTeleporting){
            mRect.left = newX - mBobWidth / 2
            mRect.top = newY - mBobHeight / 2
            mRect.bottom = mRect.top + mBobHeight
            mRect.right = mRect.right + mBobWidth

            mTeleporting = true

            success = true
        }

        return success
    }

    fun setTeleportAvaiable(){
        mTeleporting = false
    }


}