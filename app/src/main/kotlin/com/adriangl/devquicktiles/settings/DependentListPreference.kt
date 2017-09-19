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

package com.adriangl.devquicktiles.settings

import android.content.Context
import android.preference.ListPreference
import android.util.AttributeSet
import com.adriangl.devquicktiles.R

/**
 * List preference that allows defining a dependent value to enable dependent preferences.
 * Use [dependentValue] (app:dependentValue in XML) to declare the value that will enable dependent settings.
 */
class DependentListPreference @JvmOverloads constructor(context: Context,
                                                        attrs: AttributeSet? = null) : ListPreference(context, attrs) {
    var dependentValue: String? = null
        set(newDependentValue) {
            val oldDependentValue = field
            field = newDependentValue
            if (newDependentValue != oldDependentValue) {
                notifyDependencyChange(shouldDisableDependents())
            }
        }

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DependentListPreference)
            dependentValue = typedArray.getString(R.styleable.DependentListPreference_dependentValue)
            typedArray.recycle()
        }
    }

    override fun setValue(value: String) {
        val oldValue = getValue()
        super.setValue(value)
        if (value != oldValue) {
            notifyDependencyChange(shouldDisableDependents())
        }
    }

    override fun shouldDisableDependents(): Boolean {
        val shouldDisableDependents = super.shouldDisableDependents()
        return shouldDisableDependents || value == null || value != dependentValue
    }
}