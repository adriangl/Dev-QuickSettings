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
import com.adriangl.devquicktiles.base.AppScope
import com.adriangl.devquicktiles.tiles.DevelopmentSettingDelegate
import com.adriangl.devquicktiles.utils.SettingsUtils
import timber.log.Timber
import javax.inject.Inject

/**
 * A [DevelopmentSettingDelegate] that handles enabling or disabling demo mode.
 */
@AppScope
class DemoModeSettingDelegate
@Inject constructor(context: Context, contentResolver: ContentResolver) : DevelopmentSettingDelegate(context, contentResolver) {

    companion object {
        private const val DEFAULT_VALUE = "0"
    }

    private var demoModeInstance: DemoMode? = null

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
            .fold(1, { current, key ->
                (SettingsUtils.getStringFromGlobalSettings(contentResolver, key) ?: DEFAULT_VALUE).toInt() and current
            })
        return value.toString()
    }

    override fun saveValue(value: String): Boolean {
        when (value.toInt()) {
            0 -> {
                // We're trying to disable Demo Mode, so exit and only then disable the system settings
                stopDemoMode()
                return saveDemoModeValue(value)
            }
            1 -> {
                // Save the setting first and then enable demo mode
                saveDemoModeValue(value).let {
                    startDemoMode()
                    return true
                }
            }
            else -> return false
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
     * platform_frameworks_base/packages/SystemUI/src/com/android/systemui/tuner/DemoModeFragment.java
     *
     * Check protocol here: https://github.com/android/platform_frameworks_base/blob/master/packages/SystemUI/docs/demo_mode.md
     */
    private fun startDemoMode() {
        demoModeInstance = DemoMode.create {
            clock {
                hours = Build.VERSION.RELEASE.split(".")[0].toInt()
                minutes = 0
            }

            network {
                wifi {
                    show = true
                }
                mobile {
                    show = true
                    level = 4
                    dataType = Network.Mobile.DataType.TYPE_LTE
                }
                sims = 1
                noSim = false
                fully = true
            }

            battery {
                level = 100
                plugged = false
            }

            status {
                volume = Status.VolumeValues.HIDE
                bluetooth = Status.BluetoothValues.HIDE
                location = false
                alarm = false
                zen = false
                sync = false
                tty = false
                eri = false
                mute = false
                speakerPhone = false
                managedProfile = false
            }

            notifications {
                visible = false
            }
        }

        demoModeInstance?.let {
            Timber.d("DemoMode: starting demo mode with commands: ${demoModeInstance!!.getCommands()}")
            demoModeInstance!!.enter(context)
        }
    }

    private fun stopDemoMode() {
        // Exit demo mode
        demoModeInstance?.exit(context)
    }

    private fun saveDemoModeValue(value: String): Boolean {
        return listOf(DemoMode.DEMO_MODE_ALLOWED, DemoMode.DEMO_MODE_ON)
            .fold(true) {
                initial, setting ->
                initial && SettingsUtils.setStringToGlobalSettings(contentResolver, setting, value)
            }
    }

}