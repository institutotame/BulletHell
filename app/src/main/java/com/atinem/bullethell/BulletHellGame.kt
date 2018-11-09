package com.atinem.bullethell

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

class BulletHellGame(context: Context?, x : Float, y : Float) : SurfaceView(context) {
    val DEBUGGING = true

    private lateinit var job : Job

    private var mPlaying : Boolean = false
    private var mPaused : Boolean = true

    private val mOurHolder = holder
    //private val mCanvas : Canvas
    private val mPaint = Paint()

    private var mFPS : Long = 0
    private val MILLIS_IN_SECOND = 1000

    private val mScreenX = x
    private val mScreenY = y

    private val mFontSize = mScreenX / 20
    private val mFontMargin = mScreenX / 50

    private var mSP : SoundPool
    private var mBeepID : Int = -1
    private var mTeleportID : Int = -1

    private val mBullets = List(10000){Bullet(mScreenX)}

    private var mNumBullets = 0

    private val mBob = Bob(context?.resources, mScreenX, mScreenY)
    private var mHit = false
    private var mNumHits = 0
    private var mShield = 10

    private var mStartGameTime : Long = 0
    private var mBestGameTime : Long = 0
    private var mTotalGameTime : Long = 0

    init {
        mSP = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            val attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            SoundPool.Builder().setMaxStreams(5).setAudioAttributes(attributes).build()
        }else{
            SoundPool(5,AudioManager.STREAM_MUSIC, 0)
        }
        GlobalScope.launch {

            try{
                val assetManager = context?.assets
                var descriptor = assetManager?.openFd("beep.ogg")
                mBeepID = mSP.load(descriptor, 0)
                descriptor = assetManager?.openFd("teleport.ogg")
                mTeleportID = mSP.load(descriptor,0)
            }catch(e : IOException){
                Log.e("Error", "failed to load sound files")
            }
            startGame()
        }
    }

    fun startGame(){

    }

    fun spawnBullet(){
        mNumBullets++

        val spawnX = Random.nextInt(mScreenX.toInt())
        val spawnY = Random.nextInt(mScreenY.toInt())

        var velocityX = 1
        if(Random.nextInt(2)==0){
            velocityX = -1
        }
        var velocityY = 1
        if(Random.nextInt(2)==0){
            velocityY = -1
        }
        Log.d("BulletHellGame", "SpawnBullet: $mNumBullets")
        mBullets[mNumBullets-1].spawn(spawnX.toFloat(),spawnY.toFloat(), velocityX.toFloat(), velocityY.toFloat())
    }

    fun runJob(){
        job = GlobalScope.launch {
            while(mPlaying){
                val frameStartTime = System.currentTimeMillis()

                if(!mPaused){
                    update()
                    detectCollisions()
                }

                draw()

                val timeThisFrame = System.currentTimeMillis() - frameStartTime
                if(timeThisFrame >= 1){
                    mFPS = MILLIS_IN_SECOND / timeThisFrame
                }
            }
        }
    }

    fun update(){
        for(i in 0..mNumBullets){
            mBullets[i].update(mFPS)
        }
    }

    fun detectCollisions(){
        for(i in 0..mNumBullets){
            val bullet = mBullets[i]
            when {
                bullet.mRect.bottom > mScreenY -> bullet.reverseYVelocity()
                bullet.mRect.top < 0 -> bullet.reverseYVelocity()
                bullet.mRect.left < 0 -> bullet.reverseXVelocity()
                bullet.mRect.right > mScreenX -> bullet.reverseXVelocity()
                RectF.intersects(bullet.mRect,mBob.mRect) -> {
                    mSP.play(mBeepID, 1f, 1f,0,0,1f)

                    mHit = true
                    bullet.reverseXVelocity()
                    bullet.reverseYVelocity()
                    mNumHits++
                    if(mNumHits == mShield){
                        mPaused = true
                        mTotalGameTime = System.currentTimeMillis() - mStartGameTime
                        startGame()
                    }
                }
            }
        }
    }

    fun draw(){
        if(mOurHolder.surface.isValid){
            Log.d("BulletHellGame", mNumBullets.toString())
            val canvas = mOurHolder.lockCanvas()
            canvas.drawColor(Color.argb(255,243,111,36))
            mPaint.color = Color.argb(255,255,255,255)

            for(i in 0 until mNumBullets){
                Log.d("BulletHellGame", "for loop")
                canvas.drawRect(mBullets[i].mRect, mPaint)
            }

            if(DEBUGGING){
                printDebuggingText(canvas)
            }

            mOurHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun printDebuggingText(canvas : Canvas){
        val debugSize = 35f
        val debugStart = 150f
        mPaint.textSize = debugSize
        canvas.drawText("FPS: $mFPS", 10f, debugStart +debugSize, mPaint)
    }

    fun resume(){
        mPlaying = true
        runJob()
    }

    fun pause(){
        mPlaying = false
        job.cancel()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(MotionEvent.ACTION_DOWN == event?.action){
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        mPaused = false
        spawnBullet()
        return super.performClick()
    }
}