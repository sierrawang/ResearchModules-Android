/*
 * BSD 3-Clause License
 *
 * Copyright 2019  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.modules.psorcast

import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.Canvas
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import androidx.appcompat.widget.AppCompatImageView
import java.util.ArrayList

class PlaqueCoverageView : AppCompatImageView {

    private val TOUCH_TOLERANCE = 4f
    private var highlightPaint = Paint()
    private var mX = 0f
    private var mY = 0f
    private var drawings = ArrayList<Path>()

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        // Set up brush for drawing plaques
        highlightPaint.color = ContextCompat.getColor(getContext(), R.color.srpm_fern300)
        highlightPaint.isAntiAlias = true
        highlightPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        highlightPaint.style = Paint.Style.STROKE
        highlightPaint.strokeJoin = Paint.Join.ROUND
        highlightPaint.strokeCap = Paint.Cap.ROUND
        highlightPaint.strokeWidth = 30f

        scaleType = CENTER_INSIDE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (path in drawings) {
            canvas.drawPath(path, highlightPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(x, y)
            MotionEvent.ACTION_MOVE -> touchMove(x, y)
            MotionEvent.ACTION_UP -> touchUp()
        }
        invalidate()
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        var path = Path()
        path.moveTo(x,y)
        drawings.add(path)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            var path = drawings.removeAt(drawings.size - 1)
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            drawings.add(path)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        var path = drawings.removeAt(drawings.size - 1)
        path.lineTo(mX, mY)
        drawings.add(path)
    }

    fun getPaths() : ArrayList<Path> {
        return ArrayList(drawings)
    }
}