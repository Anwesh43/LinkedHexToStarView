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
val parts : Int = 2 *  lines + 1
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
    val sf1 : Float = sf.divideScale(0, 2)
    val sf2 : Float = sf.divideScale(1, 2)
    save()
    translate(w / 2, h / 2)
    for (j in 0..(lines - 1)) {
        val sf1j : Float = sf1.divideScale(j, lines)
        val sf2j : Float = sf2.divideScale(j, lines)
        val sf11 : Float = sf1j.divideScale(0, 2)
        val sf12 : Float = sf1j.divideScale(1, 2)
        val ySize : Float = size * Math.sin(deg * Math.PI / 180).toFloat()
        val y : Float = ySize - ySize * 0.5f * sf2j
        save()
        rotate(deg * j)
        if (sf11 > 0f) {
            drawLine(size / 2, ySize, size * 0.5f * (1 - sf11), y, paint)
        }
        if (sf12 > 0f) {
            drawLine(0f, y, -size * 0.5f * sf12, ySize, paint)
        }
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class HTSNode(var i : Int, val state : State = State()) {

        private var next : HTSNode? = null
        private var prev : HTSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = HTSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawHTSNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : HTSNode {
            var curr : HTSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class HexToStar(var i : Int) {

        private var curr : HTSNode = HTSNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : HexToStarView) {

        private val animator : Animator = Animator(view)
        private val hts : HexToStar = HexToStar(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            hts.draw(canvas, paint)
            animator.animate {
                hts.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            hts.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : HexToStarView {
            val view : HexToStarView = HexToStarView(activity)
            activity.setContentView(view)
            return view
        }
    }
}