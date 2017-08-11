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

package com.adriangl.devquicktiles.tiles.screenon

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Icon
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.Settings
import com.adriangl.devquicktiles.R
import com.adriangl.devquicktiles.base.AppScope
import com.adriangl.devquicktiles.tiles.DevelopmentSettingDelegate
import com.adriangl.devquicktiles.utils.SettingsUtils
import javax.inject.Inject

/**
 * A [DevelopmentSettingDelegate] that handles changing the "Keep screen on" development setting.
 */
@AppScope
class KeepScreenOnSettingDelegate
@Inject constructor(context: Context, contentResolver: ContentResolver) : DevelopmentSettingDelegate(context, contentResolver) {
    companion object {
        private const val SETTING = Settings.Global.STAY_ON_WHILE_PLUGGED_IN
        private const val DEFAULT_VALUE = "0"
    }

    override fun isActive(value: String): Boolean {
        return value.toInt() != 0
    }

    override fun getValueList(): List<String> {
        val savedValue = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(context.getString(R.string.pref_keep_screen_on_key), context.getString(R.string.pref_keep_screen_on_mode_any_value))
        return listOf("0", savedValue)
    }

    override fun queryValue(): String {
        return SettingsUtils.getStringFromGlobalSettings(contentResolver, SETTING) ?: DEFAULT_VALUE
    }

    override fun saveValue(value: String): Boolean {
        return SettingsUtils.setStringToGlobalSettings(contentResolver, SETTING, value)
    }

    override fun getIcon(value: String): Icon? {
        return Icon.createWithResource(context,
            if (value.toInt() != 0) R.drawable.ic_qs_keep_screen_on_enabled
            else R.drawable.ic_qs_keep_screen_on_disabled)
    }

    override fun getLabel(value: String): CharSequence? {
        return context.getString(R.string.qs_keep_screen_on)
    }

    override fun getSettingsUri(): List<Uri> {
        return listOf(Settings.Global.getUriFor(SETTING))
    }

}