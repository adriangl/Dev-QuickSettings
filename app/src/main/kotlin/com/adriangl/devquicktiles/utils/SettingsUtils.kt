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
object SettingsUtils {
    fun getStringFromGlobalSettings(contentResolver: ContentResolver, key: String): String? {
        return Settings.Global.getString(contentResolver, key)
    }

    fun setStringToGlobalSettings(contentResolver: ContentResolver, key: String, value: String): Boolean {
        return Settings.Global.putString(contentResolver, key, value)
    }

    fun getStringFromSystemSettings(contentResolver: ContentResolver, key: String): String? {
        return Settings.System.getString(contentResolver, key)
    }

    fun setStringToSystemSettings(contentResolver: ContentResolver, key: String, value: String): Boolean {
        return Settings.System.putString(contentResolver, key, value)
    }
}