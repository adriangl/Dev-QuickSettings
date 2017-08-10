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

package com.adriangl.devquicktiles.utils

import android.content.ContentResolver
import android.provider.Settings

/**
 * Utils class to write and read from specific [Settings] scopes.
 */
@Suppress("UndocumentedPublicFunction")
class SettingsUtils {
    companion object {
        fun getIntFromGlobalSettings(contentResolver: ContentResolver, key: String): Int {
            return Settings.Global.getInt(contentResolver, key, 0)
        }

        fun setIntToGlobalSettings(contentResolver: ContentResolver, key: String, value: Int): Boolean {
            return Settings.Global.putInt(contentResolver, key, value)
        }

        fun getFloatFromGlobalSettings(contentResolver: ContentResolver, key: String): Float {
            return Settings.Global.getFloat(contentResolver, key, 0f)
        }

        fun setFloatToGlobalSettings(contentResolver: ContentResolver, key: String, value: Float): Boolean {
            return Settings.Global.putFloat(contentResolver, key, value)
        }

        fun getIntFromSystemSettings(contentResolver: ContentResolver, key: String): Int {
            return Settings.System.getInt(contentResolver, key, 0)
        }

        fun setIntToSystemSettings(contentResolver: ContentResolver, key: String, value: Int): Boolean {
            return Settings.System.putInt(contentResolver, key, value)
        }
    }
}
