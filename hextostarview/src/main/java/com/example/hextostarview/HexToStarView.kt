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
val parts : Int = 4
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 3.2f
val deg : Float = 60f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
