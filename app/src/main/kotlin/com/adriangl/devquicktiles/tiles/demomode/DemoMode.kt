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
import java.util.*
import kotlin.properties.Delegates

/**
 * Annotation used to mark DSL scopes for [DemoMode] declarations.
 */
@DslMarker
annotation class CommandDslMarker

/**
 * Basic class that holds a command definition in demo mode
 */
data class Command(val name: String = DemoMode.EXTRA_COMMAND, val value: String) {
    constructor(name: String, value: Boolean, show: Boolean = false) : this(name,
        if (show) {
            if (value) "show" else "hide"
        } else value.toString())

    constructor(name: String, value: Any) : this(name, value.toString())
}

/**
 * Base class that implements a hierarchical relationship between [Command]s.
 */
@CommandDslMarker
abstract class CommandHolder {
    val children = mutableListOf<CommandHolder>()

    /**
     * Initializes a given [CommandHolder] and adds it to this holder's children.
     */
    protected fun <T : CommandHolder> initCommand(commandHolder: T, init: T.() -> Unit): T {
        commandHolder.init()
        children.add(commandHolder)
        return commandHolder
    }

    /**
     * Returns the commands pertaining to this [CommandHolder] ignoring children.
     */
    abstract fun getOwnCommands(): List<List<Command>>

    /**
     * Returns the list of all commands held by this [CommandHolder], including children.
     */
    open fun getCommands(): List<List<Command>> {
        return getOwnCommands()
            .plus(
                children
                    .map { child -> child.getCommands() }
                    .flatten())
    }
}

/**
 * [CommandHolder] for battery-related parameters.
 */
class Battery(var plugged: Boolean? = null) : CommandHolder() {
    var level: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..100 }

    override fun getOwnCommands(): List<List<Command>> {
        return listOf(mutableListOf<Command>().apply {
            add(Command(value = "battery"))
            level?.let { add(Command("level", level!!)) }
            plugged?.let { add(Command("plugged", plugged!!)) }
        })
    }
}

/**
 * [CommandHolder] for network-related parameters.
 */
class Network(var airplane: Boolean? = null,
              var fully: Boolean? = null,
              var carrierNetworkChange: Boolean? = null,
              var noSim: Boolean? = null) : CommandHolder() {

    var sims: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 1..8 }

    override fun getOwnCommands(): List<List<Command>> {
        return mutableListOf(mutableListOf<Command>().apply {
            add(Command(value = "network"))
            airplane?.let {
                add(Command("airplane", airplane!!, true))
            }
            carrierNetworkChange?.let {
                add(Command("carriernetworkchange", carrierNetworkChange!!, true))
            }
            noSim?.let {
                add(Command("nosim", noSim!!, true))
            }
            if (!(noSim ?: false)) {
                sims?.let {
                    add(Command("sims", sims!!))
                }
            }
        })
    }

    override fun getCommands(): List<List<Command>> {
        var commandSetList = super.getCommands()
        // This is a special case: fully must be added last in the list so the command works as expected >_<
        fully?.let {
            commandSetList = commandSetList.plus<List<Command>>(
                mutableListOf<Command>().apply {
                    add(Command(value = "network"))
                    add(Command("fully", fully!!))
                })
        }
        return commandSetList
    }

    /**
     * [CommandHolder] for WiFi-related parameters.
     */
    class Wifi(var show: Boolean? = null) : CommandHolder() {
        var level: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..4 }

        override fun getOwnCommands(): List<List<Command>> {
            return listOf(mutableListOf<Command>().apply {
                add(Command(value = "network"))
                add(Command("wifi", show!!, true))
                if (show ?: false) {
                    level?.let { add(Command("level", level!!)) }
                }
            })
        }
    }

    /**
     * [CommandHolder] for mobile networks-related parameters.
     */
    class Mobile(var show: Boolean? = null, var dataType: DataType? = null) : CommandHolder() {
        var level: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..4 }

        /**
         * Enumerates possible data values for data type.
         */
        @SuppressWarnings("UndocumentedPublicClass")
        enum class DataType(val value: String) {
            TYPE_1X("1x"),
            TYPE_3G("3g"),
            TYPE_4G("4g"),
            TYPE_E("e"),
            TYPE_G("g"),
            TYPE_H("h"),
            TYPE_LTE("lte"),
            TYPE_ROAM("roam")
        }

        override fun getOwnCommands(): List<List<Command>> {
            return listOf(mutableListOf<Command>().apply {
                add(Command(value = "network"))
                show?.let {
                    add(Command("mobile", show!!, true))
                    if (show ?: false) {
                        dataType?.let { add(Command("datatype", dataType!!.value)) }
                        level?.let { add(Command("level", level!!)) }
                    }
                }
            })
        }
    }

    /**
     * Sets Wi-Fi related parameters.
     */
    fun wifi(init: Wifi.() -> Unit): Wifi = initCommand(Wifi(), init)

    /**
     * Sets mobile networks related parameters.
     */
    fun mobile(init: Mobile.() -> Unit): Mobile = initCommand(Mobile(), init)
}

/**
 * [CommandHolder] for status bar related parameters.
 */
class Bars(var mode: Mode? = null) : CommandHolder() {
    /**
     * Enumerates possible values for the mode parameter.
     */
    @SuppressWarnings("UndocumentedPublicClass")
    enum class Mode(val value: String) {
        OPAQUE("opaque"),
        SEMI_TRANSPARENT("semi-transparent"),
        TRANSPARENT("transparent");
    }

    override fun getOwnCommands(): List<List<Command>> {
        return listOf(mutableListOf<Command>().apply {
            add(Command(value = "bars"))
            mode?.let { add(Command("mode", mode!!.value)) }
        })
    }
}

/**
 * [CommandHolder] for status bar related icon visibilities and states.
 */
class Status(var volume: VolumeValues? = null,
             var bluetooth: BluetoothValues? = null,
             var location: Boolean? = null,
             var alarm: Boolean? = null,
             var zen: Boolean? = null,
             var sync: Boolean? = null,
             var tty: Boolean? = null,
             var eri: Boolean? = null,
             var mute: Boolean? = null,
             var speakerPhone: Boolean? = null,
             var managedProfile: Boolean? = null) : CommandHolder() {

    /**
     * Enumerates possible values for volume icon.
     */
    @SuppressWarnings("UndocumentedPublicClass")
    enum class VolumeValues(val value: String) {
        SILENT("silent"),
        VIBRATE("vibrate"),
        HIDE("hide")
    }

    /**
     * Enumerates possible valies for Bluetooth icon.
     */
    @SuppressWarnings("UndocumentedPublicClass")
    enum class BluetoothValues(val value: String) {
        CONNECTED("connected"),
        DISCONNECTED("disconnected"),
        HIDE("hide")
    }

    override fun getOwnCommands(): List<List<Command>> {
        return listOf(mutableListOf<Command>().apply {
            add(Command(value = "status"))
            volume?.let { add(Command("volume", volume!!.value)) }
            bluetooth?.let { add(Command("bluetooth", bluetooth!!.value)) }
            location?.let { add(Command("location", location!!, true)) }
            alarm?.let { add(Command("alarm", alarm!!, true)) }
            zen?.let { add(Command("zen", zen!!, true)) }
            sync?.let { add(Command("sync", sync!!, true)) }
            tty?.let { add(Command("tty", tty!!, true)) }
            eri?.let { add(Command("eri", eri!!, true)) }
            mute?.let { add(Command("mute", mute!!, true)) }
            speakerPhone?.let { add(Command("speakerphone", speakerPhone!!, true)) }
            managedProfile?.let { add(Command("managed_profile", speakerPhone!!, true)) }
        })
    }
}

/**
 * [CommandHolder] for notification icons visibility parameters.
 */
class Notifications(var visible: Boolean? = null) : CommandHolder() {
    override fun getOwnCommands(): List<List<Command>> {
        return listOf(mutableListOf<Command>().apply {
            add(Command(value = "notifications"))
            visible?.let { add(Command("visible", visible.toString())) }
        })
    }
}

/**
 * [CommandHolder] for clock-related parameters.
 */
class Clock : CommandHolder() {
    var hours: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..23 }
    var minutes: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..59 }

    override fun getOwnCommands(): List<List<Command>> {
        val calendar = Calendar.getInstance()
        val commandHours = hours ?: calendar.get(Calendar.HOUR_OF_DAY)
        val commandMinutes = minutes ?: calendar.get(Calendar.MINUTE)

        return listOf(mutableListOf<Command>().apply {
            add(Command(value = "clock"))
            add(Command("hhmm", "%02d%02d".format(commandHours, commandMinutes)))
        })
    }
}

/**
 * Main class to create demo mode builders.
 *
 * Code adapted from AOSP:
 * https://github.com/android/platform_frameworks_base/blob/marshmallow-mr3-release/packages/SystemUI/src/com/android/systemui/DemoMode.java
 */
class DemoMode private constructor() : CommandHolder() {
    companion object {
        // Indicates that the demo mode is allowed, but it doesn't mean that it's active
        val DEMO_MODE_ALLOWED = "sysui_demo_allowed"
        // Activates demo mode if allowed
        val DEMO_MODE_ON = "sysui_tuner_demo_on"

        // Broadcast action for demo mode
        val ACTION_DEMO = "com.android.systemui.demo"

        // Various commands to use to control demo mode
        val EXTRA_COMMAND = "command"

        /**
         * Creates a new instance of [DemoMode].
         */
        fun create(init: DemoMode.() -> Unit): DemoMode {
            val demoModeDsl = DemoMode()
            demoModeDsl.init()
            return demoModeDsl
        }
    }

    override fun getOwnCommands(): List<List<Command>> {
        return emptyList()
    }

    /**
     * Sets battery parameters
     */
    fun battery(init: Battery.() -> Unit): Battery = initCommand(Battery(), init)

    /**
     * Sets network parameters.
     */
    fun network(init: Network.() -> Unit): Network = initCommand(Network(), init)

    /**
     * Sets status bar state parameters
     */
    fun bars(init: Bars.() -> Unit): Bars = initCommand(Bars(), init)

    /**
     * Sets status bar phone status parameters
     */
    fun status(init: Status.() -> Unit): Status = initCommand(Status(), init)

    /**
     * Sets notification parameters
     */
    fun notifications(init: Notifications.() -> Unit): Notifications = initCommand(Notifications(), init)

    /**
     * Sets clock parameters
     */
    fun clock(init: Clock.() -> Unit): Clock = initCommand(Clock(), init)

    /**
     * Enters demo mode. This requires enabling the demo mode first.
     */
    fun enter(ctx: Context) {
        sendCommandList(ctx, listOf(Command(value = "enter")))
        getCommands().forEach { commandList ->
            if (commandList.isNotEmpty()) sendCommandList(ctx, commandList)
        }
    }

    /**
     * Exits demo mode. This requires enabling the demo mode first.
     */
    fun exit(ctx: Context) {
        // Just launch the exit intent
        sendCommandList(ctx, listOf(Command(value = "exit")))
    }

    private fun sendCommandList(ctx: Context, commandList: List<Command>) {
        val intent = Intent(DemoMode.ACTION_DEMO)
            .addCommandList(commandList)
        ctx.sendBroadcast(intent)
    }

    private fun <T : Intent> T.addCommandList(setList: List<Command>): T {
        setList.isNotEmpty().let {
            setList.forEach { putExtra(it.name, it.value) }
        }
        return this
    }
}