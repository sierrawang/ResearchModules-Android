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

package org.sagebionetworks.research.modules.psorcast.step.plaque_body_map_completion

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import org.sagebionetworks.research.modules.psorcast.R
import java.util.ArrayList

class PlaqueBodyMapCompletionImageView : AppCompatImageView {

    private var highlightPaint = Paint()
    private val bitmaps = ArrayList<Bitmap>()
    private val coordinates = ArrayList<Pair<Float,Float>>()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        highlightPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        highlightPaint.isAntiAlias = true

        // Populate the bitmaps array
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.srpm_psoriasis_draw_above_waist_front))
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.srpm_psoriasis_draw_below_waist_front))
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.srpm_psoriasis_draw_above_waist_back))
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.srpm_psoriasis_draw_below_waist_back))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until bitmaps.size) {
            canvas.drawBitmap(bitmaps[i], coordinates[i].first, coordinates[i].second, highlightPaint)
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        var changed = super.setFrame(l, t, r, b)
        setCoordinates(l.toFloat(), t.toFloat(), r.toFloat(), b.toFloat())
        return changed
    }

    private fun setCoordinates(l: Float, t: Float, r: Float, b: Float) {
        val w = r - l
        val h = b - t

        // All of these values are in relation to the original design
        val wOld = 375f
        val hOld = 376f
        val bodyWidth = 150f

        val leftFrontShift = 29f
        val leftBackShift = wOld - (leftFrontShift + bodyWidth)

        val topUpperShift = 42.37f
        val topLowerShiftFront = 146f
        val topLowerShiftBack = 156f

        coordinates.add(Pair(leftFrontShift/wOld * w, topUpperShift/hOld * h))
        val frontWidth = (bodyWidth/wOld) * w
        val frontHeight = frontWidth/bitmaps[0].width * bitmaps[0].height
        bitmaps[0] = Bitmap.createScaledBitmap(bitmaps[0], frontWidth.toInt(), frontHeight.toInt(), false)

        coordinates.add(Pair(leftFrontShift/wOld * w, topLowerShiftFront/hOld * h))
        val frontHeight2 = frontWidth/bitmaps[1].width * bitmaps[1].height
        bitmaps[1] = Bitmap.createScaledBitmap(bitmaps[1], frontWidth.toInt(), frontHeight2.toInt(), false)

        coordinates.add(Pair(leftBackShift/wOld * w, topUpperShift/hOld * h))
        val backWidth = (bodyWidth/wOld) * w
        val backHeight = backWidth/bitmaps[2].width * bitmaps[2].height
        bitmaps[2] = Bitmap.createScaledBitmap(bitmaps[2], backWidth.toInt(), backHeight.toInt(), false)

        coordinates.add(Pair(leftBackShift/wOld * w, topLowerShiftBack/hOld * h))
        val backHeight2 = backWidth/bitmaps[3].width * bitmaps[3].height
        bitmaps[3] = Bitmap.createScaledBitmap(bitmaps[3], backWidth.toInt(), backHeight2.toInt(), false)
    }

    fun addResult(bitmap: Bitmap, index: Int) {
        bitmaps[index] = bitmap
    }
}