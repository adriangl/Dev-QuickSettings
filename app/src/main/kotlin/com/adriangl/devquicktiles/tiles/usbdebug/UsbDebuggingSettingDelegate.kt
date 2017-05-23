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
import com.adriangl.devquicktiles.tiles.DevelopmentSettingDelegate
import com.adriangl.devquicktiles.utils.SettingsUtils
import javax.inject.Inject

/**
 * Created by adrian-macbook on 23/5/17.
 */
class UsbDebuggingSettingDelegate @Inject constructor(context: Context, contentResolver: ContentResolver) : DevelopmentSettingDelegate(context, contentResolver) {
    companion object {
        val SETTING = Settings.Global.ADB_ENABLED
    }

    override fun isActive(value: String): Boolean {
        return value.toInt() != 0
    }

    override fun getValueList(): List<String> {
        return listOf("0", "1")
    }

    override fun queryValue(): String {
        var value = SettingsUtils.getStringFromGlobalSettings(contentResolver, SETTING)
        if (value.toInt() > 1) value = "1"
        return value
    }

    override fun saveValue(value: String): Boolean {
        return SettingsUtils.setStringToGlobalSettings(contentResolver, SETTING, value)
    }

    override fun getIcon(value: String): Icon? {
        return Icon.createWithResource(context,
                if (value.toInt() != 0) R.drawable.ic_qs_usb_debugging_enabled
                else R.drawable.ic_qs_usb_debugging_disabled)
    }

    override fun getLabel(value: String): CharSequence? {
        return context.getString(R.string.qs_usb_debugging)
    }

    override fun getSettingsUri(): List<Uri> {
        return listOf(Settings.Global.getUriFor(SETTING))
    }

}