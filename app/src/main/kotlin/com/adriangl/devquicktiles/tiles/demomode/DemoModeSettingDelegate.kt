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

package com.adriangl.devquicktiles.tiles.demomode

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.adriangl.devquicktiles.R
import com.adriangl.devquicktiles.tiles.DevelopmentSettingDelegate
import com.adriangl.devquicktiles.utils.SettingsUtils
import javax.inject.Inject

/**
 * Created by adrian-macbook on 16/5/17.
 */
class DemoModeSettingDelegate @Inject constructor(context: Context, contentResolver: ContentResolver) : DevelopmentSettingDelegate(context, contentResolver) {
    override fun getSettingsUri(): List<Uri> {
        return listOf(
                Settings.Global.getUriFor(DemoMode.DEMO_MODE_ALLOWED),
                Settings.Global.getUriFor(DemoMode.DEMO_MODE_ON))
    }

    override fun isActive(value: String): Boolean {
        return value.toInt() != 0
    }

    override fun getValueList(): List<String> {
        return listOf("0", "1")
    }

    override fun queryValue(): String {
        val value = listOf(DemoMode.DEMO_MODE_ALLOWED, DemoMode.DEMO_MODE_ON)
                .fold(1, { current, key -> SettingsUtils.getStringFromGlobalSettings(contentResolver, key).toInt() and current })
        return value.toString()
    }

    override fun saveValue(value: String): Boolean {
        val isSettingEnabled =
                listOf(DemoMode.DEMO_MODE_ALLOWED, DemoMode.DEMO_MODE_ON)
                        .fold(true) {
                            initial, setting ->
                            initial && SettingsUtils.setStringToGlobalSettings(contentResolver, setting, value)
                        }
        if (isSettingEnabled) {
            if (value.toInt() != 0) {
                startDemoMode()
            } else {
                stopDemoMode()
            }
            return true
        } else {
            return false
        }
    }

    override fun getIcon(value: String): Icon? {
        return Icon.createWithResource(context,
                if (value.toInt() != 0) R.drawable.ic_qs_demo_mode_enabled else R.drawable.ic_qs_demo_mode_disabled)
    }

    override fun getLabel(value: String): CharSequence? {
        return context.getString(R.string.qs_demo_mode)
    }

    /**
     * Code adapted from AOSP:
     * https://github.com/android/platform_frameworks_base/blob/marshmallow-mr3-release/packages/SystemUI/src/com/android/systemui/tuner/DemoModeFragment.java
     *
     * Check protocol here: https://github.com/android/platform_frameworks_base/blob/master/packages/SystemUI/docs/demo_mode.md
     */
    private fun startDemoMode() {
        // Enable Demo mode (as per documentation, this is optional)
        DemoMode.sendCommand(context, DemoMode.COMMAND_ENTER)

        // Set fixed time (use Android's major version for hours)
        DemoMode.sendCommand(context, DemoMode.COMMAND_CLOCK) { Stringent ->
            Stringent.putExtra("hhmm", "0${Build.VERSION.RELEASE.split(".")[0]}00")
        }

        // Set fixed network-related notification icons
        DemoMode.sendCommand(context, DemoMode.COMMAND_NETWORK) { Stringent ->
            Stringent.putExtra("wifi", "show")
            Stringent.putExtra("mobile", "show")
            Stringent.putExtra("sims", "1")
            Stringent.putExtra("nosim", "false")
            Stringent.putExtra("level", "4")
            Stringent.putExtra("datatype", "")
        }

        // Sets MCS state to fully connected (true, false)
        // Need to send this after so that the sim controller already exists.
        DemoMode.sendCommand(context, DemoMode.COMMAND_NETWORK) { Stringent ->
            Stringent.putExtra("fully", "true")
        }

        // Set fixed battery options
        DemoMode.sendCommand(context, DemoMode.COMMAND_BATTERY) { Stringent ->
            Stringent.putExtra("level", "100")
            Stringent.putExtra("plugged", "false")
        }

        // Hide other icons
        DemoMode.sendCommand(context, DemoMode.COMMAND_STATUS) { Stringent ->
            DemoMode.STATUS_ICONS.forEach { icon ->
                Stringent.putExtra(icon, "hide")
            }
        }

        // Hide notifications
        DemoMode.sendCommand(context, DemoMode.COMMAND_NOTIFICATIONS) { Stringent ->
            Stringent.putExtra("visible", "false")
        }
    }

    private fun stopDemoMode() {
        // Exit demo mode
        DemoMode.sendCommand(context, DemoMode.COMMAND_EXIT)
    }

}