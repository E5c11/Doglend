package com.esc.doglend.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CopyOnWriteArrayList

class MultiListenerMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    private val listeners = CopyOnWriteArrayList<TransitionListener>()

    /**
     * Add a [TransitionListener] which will be called as appropriate.
     */
    override fun addTransitionListener(listener: TransitionListener) {
        listeners.addIfAbsent(listener)
    }

    /**
     * Remove a previously added [TransitionListener].
     */
    override fun removeTransitionListener(listener: TransitionListener): Boolean {
        listeners.remove(listener)
        return true
    }

    init {
        super.setTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {
                listeners.forEach {
                    it.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                }
            }

            override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) {
                listeners.forEach {
                    it.onTransitionStarted(motionLayout, startId, endId)
                }
            }

            override fun onTransitionChange(motionLayout: MotionLayout, startId: Int, endId: Int, progress: Float) {
                listeners.forEach {
                    it.onTransitionChange(motionLayout, startId, endId, progress)
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                listeners.forEach {
                    it.onTransitionCompleted(motionLayout, currentId)
                }
            }
        })
    }

    @Deprecated("Use addTransitionListener instead")
    override fun setTransitionListener(listener: TransitionListener) {
        throw IllegalArgumentException("Use addTransitionListener instead")
    }
}

suspend fun MultiListenerMotionLayout.awaitTransitionComplete(transitionId: Int, timeout: Long = 5000L) {
    // If we're already at the specified state, return now
    if (currentState == transitionId) return

    var listener: MotionLayout.TransitionListener? = null

    try {
        withTimeout(timeout) {
            suspendCancellableCoroutine<Unit> { continuation ->
                val l = object : TransitionAdapter() {
                    override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                        if (currentId == transitionId) {
                            removeTransitionListener(this)
                            continuation.resume(Unit) {}
                        }
                    }
                }
                // If the coroutine is cancelled, remove the listener
                continuation.invokeOnCancellation {
                    removeTransitionListener(l)
                }
                // And finally add the listener
                addTransitionListener(l)
                listener = l
            }
        }
    } catch (tex: TimeoutCancellationException) {
        // Transition didn't happen in time. Remove our listener and throw a cancellation
        // exception to let the coroutine know
        listener?.let(::removeTransitionListener)
        throw CancellationException("Transition to state with id: $transitionId did not" +
                " complete in timeout.", tex)
    }
}

suspend fun RecyclerView.awaitScrollEnd() {
    // If a smooth scroll has just been started, it won't actually start until the next
    // animation frame, so we'll await that first
    awaitAnimationFrame()
    // Now we can check if we're actually idle. If so, return now
    if (scrollState == RecyclerView.SCROLL_STATE_IDLE) return

    suspendCancellableCoroutine<Unit> { continuation ->
        continuation.invokeOnCancellation {
            // If the coroutine is cancelled, remove the scroll listener
//            recyclerView.removeOnScrollListener(this)
            // We could also stop the scroll here if desired
        }

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Make sure we remove the listener so we don't leak the
                    // coroutine continuation
                    recyclerView.removeOnScrollListener(this)
                    // Finally, resume the coroutine
                    continuation.resume(Unit) {}
                }
            }
        })
    }
}

suspend fun View.awaitAnimationFrame() = suspendCancellableCoroutine<Unit> { continuation ->
    val runnable = Runnable {
        continuation.resume(Unit) {}
    }
    // If the coroutine is cancelled, remove the callback
    continuation.invokeOnCancellation { removeCallbacks(runnable) }
    // And finally post the runnable
    postOnAnimation(runnable)
}