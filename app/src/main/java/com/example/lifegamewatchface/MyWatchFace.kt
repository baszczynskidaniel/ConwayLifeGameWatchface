package com.example.lifegamewatchface

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings.Global
import androidx.palette.graphics.Palette
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
//import kotlinx.coroutines.*
import kotlin.coroutines.*

import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.TimeZone
import kotlin.concurrent.thread


/**
 * Updates rate in milliseconds for interactive mode. We update once a second to advance the
 * second hand.
 */
private const val INTERACTIVE_UPDATE_RATE_MS = 1000

/**
 * Handler message id for updating the time periodically in interactive mode.
 */
private const val MSG_UPDATE_TIME = 0



private const val CENTER_GAP_AND_CIRCLE_RADIUS = 30f

private const val SHADOW_RADIUS = 6f

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 *
 *
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
class MyWatchFace : CanvasWatchFaceService() {

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: Engine) : Handler(Looper.myLooper()!!) {
        private val mWeakReference: WeakReference<Engine> = WeakReference(reference)

        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }

    inner class Engine : CanvasWatchFaceService.Engine() {

        private lateinit var mCalendar: Calendar

        private var mRegisteredTimeZoneReceiver = false
        private var mMuteMode: Boolean = false
        private var mCenterX: Float = 0F
        private var mCenterY: Float = 0F
        private var mWidth: Int = 0
        private var mHeight: Int = 0
        private var mSimulationBoardSize = 31
        private var mMinuteFirstDigit = 0
        private var mMinuteSecondDigit = 0
        private var mHourFirstDigit = 0
        private var mHourSecondDigit = 0
        private var mWatchfaceState: WatchfaceState = WatchfaceState.SHOW_TIME



        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private var mWatchHandColor: Int = 0
        private var mWatchHandHighlightColor: Int = 0
        private var mWatchHandShadowColor: Int = 0

        private lateinit var mBackgroundPaint: Paint
        private lateinit var mBackgroundBitmap: Bitmap
        private lateinit var mGrayBackgroundBitmap: Bitmap

        private var mAmbient: Boolean = false
        private var mLowBitAmbient: Boolean = false
        private var mBurnInProtection: Boolean = false

        private var mCells = Array(mSimulationBoardSize) { arrayOfNulls<Cell>(mSimulationBoardSize) }
        private var mCellNumOfNeighbours = Array(mSimulationBoardSize) { IntArray(mSimulationBoardSize)}
        private var mShape = Shape()
        /* Handler to update the time once a second in interactive mode. */
        private val mUpdateTimeHandler = EngineHandler(this)

        private val mTimeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }


        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            setWatchFaceStyle(WatchFaceStyle.Builder(this@MyWatchFace)
                    .setAcceptsTapEvents(true)
                    .build())

            mCalendar = Calendar.getInstance()
            updateTimeVariables()

            for (y in 0 until mSimulationBoardSize) {
                for (x in  0 until mSimulationBoardSize)
                {
                    mCells[y][x] = Cell(CellState.DEAD)
                }
            }
            initializeBackground()
            initializeWatchFace()
        }

        private fun updateTimeVariables(): Unit
        {
            var minute = mCalendar.get(Calendar.MINUTE)
            var hour = mCalendar.get(Calendar.HOUR_OF_DAY)

            mMinuteSecondDigit = minute / 10
            mMinuteFirstDigit = minute % 10
            mHourSecondDigit = hour / 10
            mHourFirstDigit = hour % 10
        }

        private fun initializeBackground() {
            mBackgroundPaint = Paint().apply {
                color = Color.BLACK
            }
            mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.watchface_service_bg)

            /* Extracts colors from background image to improve watchface style. */
            Palette.from(mBackgroundBitmap).generate {
                it?.let {
                    mWatchHandHighlightColor = it.getVibrantColor(Color.RED)
                    mWatchHandColor = it.getLightVibrantColor(Color.WHITE)
                    mWatchHandShadowColor = it.getDarkMutedColor(Color.BLACK)
                    updateWatchHandStyle()
                }
            }
        }

        private fun initializeWatchFace() {
            /* Set defaults for colors */
            mWatchHandColor = Color.WHITE
            mWatchHandHighlightColor = Color.RED
            mWatchHandShadowColor = Color.BLACK
        }

        override fun onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            mLowBitAmbient = properties.getBoolean(
                    PROPERTY_LOW_BIT_AMBIENT, false)
            mBurnInProtection = properties.getBoolean(
                    PROPERTY_BURN_IN_PROTECTION, false)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            mWatchfaceState = WatchfaceState.SHOW_TIME
            updateTimeVariables()
            for (y in 0 until mSimulationBoardSize) {
                for (x in  0 until mSimulationBoardSize)
                {
                    mCells[y][x] = Cell(CellState.DEAD)
                }

            }
            insertShape(7, 3, mShape.symbolsMap[mHourSecondDigit]!!)
            insertShape(16, 3, mShape.symbolsMap[mHourFirstDigit]!!)
            insertShape(7, 17, mShape.symbolsMap[mMinuteSecondDigit]!!)
            insertShape(16, 17, mShape.symbolsMap[mMinuteFirstDigit]!!)

            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            mAmbient = inAmbientMode
            updateWatchHandStyle()
            // Check and trigger whether or not timer should be running (only
            // in active mode).
            updateTimer()
        }

        private fun updateWatchHandStyle() {
        }

        override fun onInterruptionFilterChanged(interruptionFilter: Int) {
            super.onInterruptionFilterChanged(interruptionFilter)
            val inMuteMode = interruptionFilter == INTERRUPTION_FILTER_NONE

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode
                invalidate()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)

            mCenterX = width / 2f
            mCenterY = height / 2f
            mWidth = width
            mHeight = height
        }

        private fun initGrayBackgroundBitmap() {
            mGrayBackgroundBitmap = Bitmap.createBitmap(
                    mBackgroundBitmap.width,
                    mBackgroundBitmap.height,
                    Bitmap.Config.ARGB_8888)
            val canvas = Canvas(mGrayBackgroundBitmap)
            val grayPaint = Paint()
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            grayPaint.colorFilter = filter
            canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, grayPaint)
        }

/**
         * Captures tap event (and tap type). The [WatchFaceService.TAP_TYPE_TAP] case can be
         * used for implementing specific logic to handle the gesture.
         */
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            if (mWatchfaceState == WatchfaceState.SHOW_TIME) {

                when (tapType) {
                    TAP_TYPE_TAP -> {

                        mWatchfaceState = WatchfaceState.GAME_OF_LIFE
                    }
                    else -> {}
                }

            }
            else
            {
                when (tapType) {
                    TAP_TYPE_TAP -> {

                        mWatchfaceState = WatchfaceState.SHOW_TIME
                        for (y in 0 until mSimulationBoardSize) {
                            for (x in  0 until mSimulationBoardSize)
                            {
                                mCells[y][x] = Cell(CellState.DEAD)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
        private fun startGameOfLife() {



            while (mWatchfaceState == WatchfaceState.GAME_OF_LIFE) {

            updateNumOfNeighbours()
            updateCells()
            invalidate()

            }



        }

        private fun updateNumOfNeighbours() : Unit
        {
            for (y in 0 until mSimulationBoardSize) {
                for (x in 0 until mSimulationBoardSize) {
                    var numOfNeighbours = 0
                    if (isInMatrix(y + 1, x + 1))
                        if (mCells[y + 1][x + 1]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    if (isInMatrix(y + 1, x))
                        if (mCells[y + 1][x]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    if (isInMatrix(y + 1, x - 1))
                        if (mCells[y + 1][x - 1]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    if (isInMatrix(y, x + 1))
                        if (mCells[y][x + 1]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    if (isInMatrix(y, x - 1))
                        if (mCells[y][x - 1]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    if (isInMatrix(y - 1, x + 1))
                        if (mCells[y - 1][x + 1]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    if (isInMatrix(y - 1, x))
                        if (mCells[y - 1][x]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    if (isInMatrix(y - 1, x - 1))
                        if (mCells[y - 1][x - 1]?.state == CellState.ALIVE)
                            numOfNeighbours++
                    mCellNumOfNeighbours[y][x] = numOfNeighbours
                }
            }
        }

        private fun updateCells() : Unit
        {
            for (y in 0 until mSimulationBoardSize) {
                for (x in 0 until mSimulationBoardSize) {
                    mCells[y][x]?.update(mCellNumOfNeighbours[y][x])
                }
            }
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            val now = System.currentTimeMillis()
            mCalendar.timeInMillis = now

            drawBackground(canvas)
            drawWatchFace(canvas)
        }

        private fun drawBackground(canvas: Canvas) {


        }
        private fun isInMatrix(row: Int, col: Int) : Boolean
        {
            if(row < 0 || col < 0 || row >= mSimulationBoardSize || col >= mSimulationBoardSize)
                return false
            return true
        }
        private fun insertShape(startx: Int, starty: Int, shape: Array<IntArray>): Unit
        {
            val endx = startx + shape[0].size
            val endy = starty + shape.size

            for (y in starty until endy){
                for(x in startx until endx){
                    if( shape[y - starty][x - startx] == 1)
                        mCells[y][x] = Cell(CellState.ALIVE)
                    else
                        mCells[y][x] = Cell(CellState.DEAD)
                }
            }
        }

        private fun drawWatchFace(canvas: Canvas) {
            val setTimeToStartLife = 6
            val seconds = mCalendar.get(Calendar.SECOND)
            if(60 - seconds < setTimeToStartLife)
                mWatchfaceState = WatchfaceState.GAME_OF_LIFE

            if(mWatchfaceState == WatchfaceState.SHOW_TIME)
            {
                updateTimeVariables()
                for (y in 0 until mSimulationBoardSize) {
                    for (x in  0 until mSimulationBoardSize)
                    {
                        mCells[y][x] = Cell(CellState.DEAD)
                    }

                }
                insertShape(7, 3, mShape.symbolsMap[mHourSecondDigit]!!)
                insertShape(16, 3, mShape.symbolsMap[mHourFirstDigit]!!)
                insertShape(7, 17, mShape.symbolsMap[mMinuteSecondDigit]!!)
                insertShape(16, 17, mShape.symbolsMap[mMinuteFirstDigit]!!)

            }
            if(mWatchfaceState == WatchfaceState.GAME_OF_LIFE)
            {
                updateNumOfNeighbours()
                updateCells()
                Thread.sleep(100)
                if(mWatchfaceState == WatchfaceState.GAME_OF_LIFE)
                    invalidate()
            }
            val gap = 4f
            var radius = ((mWidth - gap * mSimulationBoardSize)/ mSimulationBoardSize) / 2
            val paint = Paint()
            paint.color = Color.WHITE
            val paint2 = Paint()
            paint2.color = Color.BLACK
            val paintPalette = Array(7 ){Paint()}
            paintPalette[0].color = Color.argb(255, 255, 255, 255)
            paintPalette[1].color = Color.argb(255, 162, 34, 193)
            paintPalette[2].color = Color.argb(255, 193, 34, 167)
            paintPalette[3].color = Color.argb(255, 193, 34, 103)
            paintPalette[4].color = Color.argb(255, 255, 0, 0)

            for (y in 0 until mSimulationBoardSize) {
               for (x in  0 until mSimulationBoardSize)
               {
                   if (mCells[y][x]?.state == CellState.ALIVE) {
                       var paint3 = Paint()
                       when(mCells[y][x]?.lifetime){
                           0 -> paint3 = paintPalette[0]
                           1 -> paint3 = paintPalette[0]
                           2 -> paint3 = paintPalette[1]
                           3 -> paint3 = paintPalette[3]
                           else -> paint3 = paintPalette[4]
                       }
                       canvas.drawCircle(
                           gap / 2 + radius + radius * x * 2 + gap * x,
                           gap / 2 + radius + radius * 2 * y + gap * y, radius, paint3
                       )
                   }
                   else
                   {
                       canvas.drawCircle(
                           gap / 2 + radius + radius * x * 2 + gap * x,
                           gap / 2 + radius + radius * 2 * y + gap * y, radius, paint2 )
                   }
               }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            mWatchfaceState = WatchfaceState.SHOW_TIME
            updateTimeVariables()
            for (y in 0 until mSimulationBoardSize) {
                for (x in  0 until mSimulationBoardSize)
                {
                    mCells[y][x] = Cell(CellState.DEAD)
                }

            }
            insertShape(7, 3, mShape.symbolsMap[mHourSecondDigit]!!)
            insertShape(16, 3, mShape.symbolsMap[mHourFirstDigit]!!)
            insertShape(7, 17, mShape.symbolsMap[mMinuteSecondDigit]!!)
            insertShape(16, 17, mShape.symbolsMap[mMinuteFirstDigit]!!)
            if (visible) {
                registerReceiver()
                /* Update time zone in case it changed while we weren"t visible. */
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            } else {
                unregisterReceiver()
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer()
        }

        private fun registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = true
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED)

            this@MyWatchFace.registerReceiver(mTimeZoneReceiver, filter)


        }

        private fun unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = false
            this@MyWatchFace.unregisterReceiver(mTimeZoneReceiver)
        }

        /**
         * Starts/stops the [.mUpdateTimeHandler] timer based on the state of the watch face.
         */
        private fun updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer
         * should only run in active mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !mAmbient
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }
}