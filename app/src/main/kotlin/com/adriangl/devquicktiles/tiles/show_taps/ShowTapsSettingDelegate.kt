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

package com.adriangl.devquicktiles.tiles.finishactivities

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.Settings
import com.adriangl.devquicktiles.R
import com.adriangl.devquicktiles.base.AppScope
import com.adriangl.devquicktiles.tiles.DevelopmentSettingDelegate
import com.adriangl.devquicktiles.utils.SettingsUtils
import javax.inject.Inject

/**
 * Created by adrian-macbook on 23/5/17.
 */
@AppScope
class ShowTapsSettingDelegate @Inject constructor(context: Context, contentResolver: ContentResolver) : DevelopmentSettingDelegate(context, contentResolver) {
    companion object {
        private const val SETTING = "show_touches" // This is hidden for developers, so we use the string resource
        private const val DEFAULT_VALUE = "0"
    }

    override fun isActive(value: String): Boolean {
        return value.toInt() != 0
    }

    override fun getValueList(): List<String> {
        return listOf("0", "1")
    }

    override fun queryValue(): String {
        var value = SettingsUtils.getStringFromSystemSettings(contentResolver, SETTING) ?: DEFAULT_VALUE
        if (value.toInt() > 1) value = "1"
        return value
    }

    override fun saveValue(value: String): Boolean {
        /*
         * The proper way to do this would be to check Settings.System.canWrite().
         * If we can write there then write the setting and if we can't, then launch an Intent to
         * Settings.ACTION_MANAGE_WRITE_SETTINGS to enable the app to write system settings.
         *
         * The problem is that from API 23+ we can't do that with the "show_touches" setting
         * (and others I suppose) as it throws an IllegalArgumentException:
         * You cannot change private secure settings.
         *
         * So we have to fall back to use a targetSdkVersion of 22 so we can write the setting.
         * Kinda hacky, but it works ¯\_(ツ)_/¯.
         */
        return SettingsUtils.setStringToSystemSettings(contentResolver, SETTING, value)
    }

    override fun getIcon(value: String): Icon? {
        return Icon.createWithResource(context,
            if (value.toInt() != 0) R.drawable.ic_qs_show_taps_enabled
            else R.drawable.ic_qs_show_taps_disabled)
    }

    override fun getLabel(value: String): CharSequence? {
        return context.getString(R.string.qs_show_taps)
    }

    override fun getSettingsUri(): List<Uri> {
        return listOf(Settings.Global.getUriFor(SETTING))
    }

}