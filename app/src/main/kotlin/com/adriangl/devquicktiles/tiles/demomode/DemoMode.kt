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

import android.content.Context
import android.content.Intent

/**
 * Code adapted from AOSP:
 * https://github.com/android/platform_frameworks_base/blob/marshmallow-mr3-release/packages/SystemUI/src/com/android/systemui/DemoMode.java
 */
class DemoMode {
    companion object {
        // Indicates that the demo mode is allowed, but it doesn't mean that it's active
        val DEMO_MODE_ALLOWED = "sysui_demo_allowed"
        // Activates demo mode if allowed
        val DEMO_MODE_ON = "sysui_tuner_demo_on"

        // Broadcast action for demo mode
        val ACTION_DEMO = "com.android.systemui.demo"

        // Various commands to use to control demo mode
        val EXTRA_COMMAND = "command"

        val COMMAND_ENTER = "enter"
        val COMMAND_EXIT = "exit"
        val COMMAND_CLOCK = "clock"
        val COMMAND_BATTERY = "battery"
        val COMMAND_NETWORK = "network"
        val COMMAND_BARS = "bars"
        val COMMAND_STATUS = "status"
        val COMMAND_NOTIFICATIONS = "notifications"
        val COMMAND_VOLUME = "volume"

        val STATUS_ICONS = arrayOf(
                "volume",
                "bluetooth",
                "location",
                "alarm",
                "zen",
                "sync",
                "tty",
                "eri",
                "mute",
                "speakerphone",
                "managed_profile")

        fun sendCommand(context: Context, command: String, intentBuilder: (Intent) -> Unit = {}) {
            // Prepare intent and command to send
            val intent = Intent(ACTION_DEMO)
            intent.putExtra(EXTRA_COMMAND, command)
            // Apply extras if they exist
            intent.apply(intentBuilder)
            // Send broadcast to system demo mode receiver
            context.sendBroadcast(intent)
        }
    }

    interface Command {
        fun build(): Intent
    }

    class EnterCommand : Command {
        override fun build(): Intent {
            return Intent(ACTION_DEMO)
                    .putExtra(EXTRA_COMMAND, COMMAND_ENTER)
        }
    }

    class ExitCommand : Command {
        override fun build(): Intent {
            return Intent(ACTION_DEMO)
                    .putExtra(EXTRA_COMMAND, COMMAND_EXIT)
        }
    }

    class ClockCommand(val hours: Int = 0, val minutes: Int = 0) : Command {
        override fun build(): Intent {
            return Intent(ACTION_DEMO)
                    .putExtra(EXTRA_COMMAND, COMMAND_CLOCK)
                    .putExtra("hhmm", buildClockFormat())
        }

        private fun buildClockFormat(): String {
            if ((hours !in 0..23) || (minutes !in 0..59)) throw UnsupportedOperationException("Invalid time format")
            return String.format("%02d%02d", hours, minutes)
        }
    }

    class BatteryCommand(val level: Int = -1, val plugged: Boolean = false) : Command {
        override fun build(): Intent {
            val intent = Intent(ACTION_DEMO)
                    .putExtra(EXTRA_COMMAND, COMMAND_BATTERY)
                    .putExtra("plugged", plugged)

            if (level >= 0) intent.putExtra("level", level)

            return intent
        }
    }

    class NetworkCommand(val showAirplane: Boolean = false,
                         val fully: Boolean = true,
                         val showWifi: Boolean = true,
                         val wifiLevel: Int = -1,
                         val showMobile: Boolean = true,
                         val mobileDataType: DataType = NetworkCommand.DataType.TYPE_NULL,
                         val mobileLevel: Int = -1,
                         val showCarrierNetworkChange: Boolean = false) : Command {
        enum class DataType(val value: String) {
            TYPE_NULL(""),
            TYPE_1X("1x"),
            TYPE_3G("3g"),
            TYPE_4G("4g"),
            TYPE_E("e"),
            TYPE_G("g"),
            TYPE_H("h"),
            TYPE_LTE("lte"),
            TYPE_ROAM("roam")
        }

        override fun build(): Intent {
            val intent = Intent(ACTION_DEMO)
                    .putExtra(EXTRA_COMMAND, COMMAND_NETWORK)
s

            return intent
        }
    }
}