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

package com.adriangl.devquicktiles.settings.preferences

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import com.adriangl.devquicktiles.R
import java.util.*

/**
 * [DialogPreference] that shows a time picker following the user's current time format.
 *
 * In order to force an specific time format format, use [is24HoursFormat] (app:is24HoursFormat in XML)
 * to force it to display a 24 hours view or a 12 + (AM, PM) hours view.
 *
 * The value of the preference is saved in milliseconds; to recover the time set in the preference, use the [Calendar] class and set
 * [Calendar.setTimeInMillis] to properly recover hours and minutes from it.
 */
open class TimePreference
@JvmOverloads constructor(context: Context,
                          attrs: AttributeSet? = null,
                          defStyle: Int = android.R.attr.dialogPreferenceStyle) : DialogPreference(context, attrs, defStyle) {
    private val calendar: Calendar
    private lateinit var picker: TimePicker

    private var is24HoursFormat: Boolean = DateFormat.is24HourFormat(context)

    init {
        @Suppress("UsePropertyAccessSyntax")
        setDialogTitle(null)
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
        calendar = GregorianCalendar()

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimePreference)
            is24HoursFormat = typedArray.getBoolean(R.styleable.TimePreference_is24HoursFormat, is24HoursFormat)
            typedArray.recycle()
        }
    }

    override fun onCreateDialogView(): View {
        picker = TimePicker(context)
        picker.setIs24HourView(is24HoursFormat)
        return picker
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker.hour = calendar.get(Calendar.HOUR_OF_DAY)
        picker.minute = calendar.get(Calendar.MINUTE)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
            calendar.set(Calendar.MINUTE, picker.minute)

            summary = summary
            if (callChangeListener(calendar.timeInMillis)) {
                persistLong(calendar.timeInMillis)
                notifyChanged()
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? = a.getString(index)

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        if (restoreValue) {
            if (defaultValue == null) {
                calendar.timeInMillis = getPersistedLong(System.currentTimeMillis())
            } else {
                calendar.timeInMillis = java.lang.Long.parseLong(getPersistedString(defaultValue as String?))
            }
        } else {
            if (defaultValue == null) {
                calendar.timeInMillis = System.currentTimeMillis()
            } else {
                calendar.timeInMillis = java.lang.Long.parseLong(defaultValue as String?)
            }
        }
        summary = summary
    }

    override fun getSummary(): CharSequence? = DateFormat.getTimeFormat(context).format(Date(calendar.timeInMillis))
}