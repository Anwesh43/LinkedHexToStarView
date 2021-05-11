package com.example.hextostarview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.content.Context
import android.app.Activity

val colors : Array<Int> = arrayOf(
    "#f44336",
    "#2196F3",
    "#00BFA5",
    "#BF360C",
    "#311B92"
).map {
    Color.parseColor(it)
}.toTypedArray()
val lines : Int = 6
val parts : Int = 2 *  lines
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 3.2f
val deg : Float = 360f / lines
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawHexToStar(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    save()
    translate(w / 2, h / 2)
    for (j in 0..(lines - 1)) {
        val sf1j : Float = sf1.divideScale(j, lines)
        val sf2j : Float = sf2.divideScale(j, lines)
        val sf11 : Float = sf1j.divideScale(0, 2)
        val sf12 : Float = sf1j.divideScale(1, 2)
        val y : Float = size - size * 0.5f * sf2j
        save()
        rotate(deg * j)
        drawLine(size / 2, size, size * 0.5f * (1 - sf11), y, paint)
        drawLine(0f, y, -size * 0.5f * sf12, size, paint)
        restore()
    }
    restore()
}

fun Canvas.drawHTSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawHexToStar(scale, w, h, paint)
}

class HexToStarView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}