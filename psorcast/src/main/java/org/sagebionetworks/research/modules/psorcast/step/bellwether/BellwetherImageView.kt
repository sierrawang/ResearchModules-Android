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

package org.sagebionetworks.research.modules.psorcast.step.bellwether

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Region
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import org.sagebionetworks.research.modules.psorcast.R

class BellwetherImageView : AppCompatImageView {

    private var isFront = true
    private var selectedRegion = -1
    private var frontRegions = ArrayList<Region>()
    private var backRegions = ArrayList<Region>()
    private var highlightPaint = Paint()

    // Use to determine body regions on screen
    private var drawableWidth = 0f
    private var drawableHeight = 0f
    private lateinit var bellwetherPlacement: BellwetherPlacement

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // Initialize paint
        highlightPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        highlightPaint.isAntiAlias = true
    }

    fun setBellwetherPlacement(bellwetherPlacement: BellwetherPlacement) {
        this.bellwetherPlacement = bellwetherPlacement
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        var changed = super.setFrame(l, t, r, b)

        // Determine the bounds of the screen
        val matrixValues = FloatArray(9)
        imageMatrix.getValues(matrixValues)
        drawableWidth = drawable.bounds.width() * matrixValues[Matrix.MSCALE_X]
        drawableHeight = drawable.bounds.height() * matrixValues[Matrix.MSCALE_Y]

        // Add all regions given in the json to
        val scale = bellwetherPlacement.targetHeight/bellwetherPlacement.srcHeight
        for (region in bellwetherPlacement.coordinates) {
            addRegion(region.value, scale)
        }

        return changed
    }

    private fun addRegion(dimensions: Map<String, Float>, scale: Float) {
        val x = dimensions["x"] as Float
        val y = dimensions["y"] as Float
        val width = dimensions["width"] as Float
        val height = dimensions["height"] as Float

        val l = scaleDimen(x, scale, bellwetherPlacement.leftShift, bellwetherPlacement.targetWidth, paddingLeft, drawableWidth)
        val r = scaleDimen(x + width, scale, bellwetherPlacement.leftShift, bellwetherPlacement.targetWidth, paddingLeft, drawableWidth)
        val t = scaleDimen(y, scale, bellwetherPlacement.topShift, bellwetherPlacement.targetHeight, paddingTop, drawableHeight)
        val b = scaleDimen(y + height, scale, bellwetherPlacement.topShift, bellwetherPlacement.targetHeight, paddingTop, drawableHeight)
        frontRegions.add(Region(l.toInt(),t.toInt(),r.toInt(),b.toInt()))
    }

    private fun scaleDimen(unscaled: Float, scale: Float, shift: Float, denom: Float,
            padding: Int, size: Float): Float {
        return padding + (unscaled*scale+shift)/denom * size
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (selectedRegion >= 0) {
            var regions = if (isFront) frontRegions else backRegions
            canvas.drawPath(regions.get(selectedRegion).boundaryPath, highlightPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        determineRegion(event.x, event.y)
        invalidate()
        return true
    }

    fun determineRegion(x : Float, y: Float) {
        var regions = if (isFront) frontRegions else backRegions
        for (i in 0 until regions.size) {
            if (regions.get(i).contains(x.toInt(), y.toInt())) {
                selectedRegion = i
                break
            }
        }
    }
}