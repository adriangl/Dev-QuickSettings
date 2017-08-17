/*
 * Copyright (C) 2017 Adrián García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adriangl.devquicktiles.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

/**
 * Extension of ImageView that correctly applies maxWidth and maxHeight.
 *
 * Extracted from [com.android.internal.widget.PreferenceImageView].
 */
class MaxSizeImageView @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0,
                                                 defStyleRes: Int = 0) : ImageView(context, attrs, defStyleAttr, defStyleRes) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newWidthMeasureSpec = widthMeasureSpec
        var newHeightMeasureSpec = heightMeasureSpec
        val widthMode = View.MeasureSpec.getMode(newWidthMeasureSpec)
        if (widthMode == View.MeasureSpec.AT_MOST || widthMode == View.MeasureSpec.UNSPECIFIED) {
            val widthSize = View.MeasureSpec.getSize(newWidthMeasureSpec)
            val maxWidth = maxWidth
            if (maxWidth != Integer.MAX_VALUE && (maxWidth < widthSize || widthMode == View.MeasureSpec.UNSPECIFIED)) {
                newWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST)
            }
        }

        val heightMode = View.MeasureSpec.getMode(newHeightMeasureSpec)
        if (heightMode == View.MeasureSpec.AT_MOST || heightMode == View.MeasureSpec.UNSPECIFIED) {
            val heightSize = View.MeasureSpec.getSize(newHeightMeasureSpec)
            val maxHeight = maxHeight
            if (maxHeight != Integer.MAX_VALUE && (maxHeight < heightSize || heightMode == View.MeasureSpec.UNSPECIFIED)) {
                newHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST)
            }
        }

        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }
}
