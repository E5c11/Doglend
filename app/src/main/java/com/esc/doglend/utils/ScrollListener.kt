package com.esc.doglend.utils

import androidx.recyclerview.widget.RecyclerView


open class ScrollListener : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> println("The RecyclerView is not scrolling")
            RecyclerView.SCROLL_STATE_DRAGGING -> println("Scrolling now")
            RecyclerView.SCROLL_STATE_SETTLING -> println("Scroll Settling")
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//        when {
//            dx > 0 -> {
//                println("Scrolled Right")
//            }
//            dx < 0 -> {
//                println("Scrolled Left")
//            }
//            else -> {
//                println("No Horizontal Scrolled")
//            }
//        }
        when {
            dy > 0 -> {
                println("Scrolled Downwards")
            }
            dy < 0 -> {
                println("Scrolled Upwards")
            }
            else -> {
                println("No Vertical Scrolled")
            }
        }
    }
}