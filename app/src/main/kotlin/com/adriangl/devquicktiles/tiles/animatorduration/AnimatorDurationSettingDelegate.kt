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

package com.adriangl.devquicktiles.tiles.animatorduration

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

@AppScope
class AnimatorDurationSettingDelegate @Inject constructor(context: Context, contentResolver: ContentResolver) : DevelopmentSettingDelegate(context, contentResolver) {

    companion object {
        private const val SETTING = Settings.Global.ANIMATOR_DURATION_SCALE
        private const val DEFAULT_VALUE = "1"
    }

    override fun getSettingsUri(): List<Uri> {
        return listOf(Settings.Global.getUriFor(SETTING))
    }

    override fun isActive(value: String): Boolean {
        return value.toFloat() != 0f
    }

    override fun queryValue(): String {
        return SettingsUtils.getStringFromGlobalSettings(contentResolver, SETTING) ?: DEFAULT_VALUE
    }

    override fun saveValue(value: String): Boolean {
        return SettingsUtils.setStringToGlobalSettings(contentResolver, SETTING, value)
    }

    override fun getValueList(): List<String> {
        return listOf("0", "0.5", "1", "1.5", "2", "5", "10")
    }

    override fun getIcon(value: String): Icon? {
        val floatValue = value.toFloat()
        var iconResource = R.drawable.ic_qs_animator_duration_enabled
        if (floatValue <= 0f) {
            iconResource = R.drawable.ic_qs_animator_duration_disabled
        } else if (floatValue <= 0.5f) {
            iconResource = R.drawable.ic_qs_animator_duration_half_x
        } else if (floatValue <= 1f) {
            iconResource = R.drawable.ic_qs_animator_duration_1x
        } else if (floatValue <= 1.5f) {
            iconResource = R.drawable.ic_qs_animator_duration_1_5x
        } else if (floatValue <= 2f) {
            iconResource = R.drawable.ic_qs_animator_duration_2x
        } else if (floatValue <= 5f) {
            iconResource = R.drawable.ic_qs_animator_duration_5x
        } else if (floatValue <= 10f) {
            iconResource = R.drawable.ic_qs_animator_duration_10x
        }

        return Icon.createWithResource(context, iconResource)
    }

    override fun getLabel(value: String): CharSequence? {
        val floatValue = value.toFloat()
        var stringResource = R.string.qs_animator_duration
        if (floatValue <= 0f) {
            stringResource = R.string.qs_animator_off
        } else if (floatValue <= 0.5f) {
            stringResource = R.string.qs_animator_scale_0_5x
        } else if (floatValue <= 1f) {
            stringResource = R.string.qs_animator_scale_1x
        } else if (floatValue <= 1.5f) {
            stringResource = R.string.qs_animator_scale_1_5x
        } else if (floatValue <= 2f) {
            stringResource = R.string.qs_animator_scale_2x
        } else if (floatValue <= 5f) {
            stringResource = R.string.qs_animator_scale_5x
        } else if (floatValue <= 10f) {
            stringResource = R.string.qs_animator_scale_10x
        }

        return context.getString(stringResource)
    }
}