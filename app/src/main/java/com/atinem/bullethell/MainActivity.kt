package com.atinem.bullethell

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    private lateinit var mBHGame : BulletHellGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        mBHGame = BulletHellGame(this, size.x.toFloat(), size.y.toFloat())
        setContentView(mBHGame)
    }

    override fun onResume() {
        super.onResume()
        mBHGame.resume()
    }

    override fun onPause() {
        super.onPause()
        mBHGame.pause()
    }

}
