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

data class Command(val name: String = "command", val value: String) {
    constructor(name: String, value: Boolean, show: Boolean = false)
        : this(name, if (show) {
        if (value) "show" else "hide"
    } else value.toString())

    constructor(name: String, value: Any) : this(name, value.toString())
}

typealias CommandSet = Pair<String, List<Command>>

@DslMarker
annotation class CommandDslMarker

@CommandDslMarker
abstract class CommandHolder {
    abstract fun getCommandSets(): List<CommandSet>
}

interface SubCommand {
    fun getCommands(): List<Command>
}

class Battery(var plugged: Boolean? = null) : CommandHolder() {
    var level: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..100 }

    override fun getCommandSets(): List<CommandSet> {
        val commandList = mutableListOf<Command>().apply {
            level?.let { add(Command("level", level!!)) }
            plugged?.let { add(Command("plugged", plugged!!)) }
        }

        return listOf(CommandSet("battery", commandList))
    }
}

class Network(var airplane: Boolean? = null,
              var fully: Boolean? = null,
              var carrierNetworkChange: Boolean? = null,
              var noSim: Boolean? = null) : CommandHolder() {

    var sims: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 1..8 }

    private val commandSetList: MutableList<CommandSet> = mutableListOf()

    override fun getCommandSets(): List<CommandSet> {
        val initCommandList = mutableListOf<Command>()
        airplane?.let {
            initCommandList.add(Command("airplane", airplane!!, true))
        }
        carrierNetworkChange?.let {
            initCommandList.add(Command("carriernetworkchange", carrierNetworkChange!!, true))
        }
        noSim?.let {
            initCommandList.add(Command("nosim", noSim!!, true))
        }
        if (!(noSim ?: false)) {
            sims?.let {
                initCommandList.add(Command("sims", sims!!))
            }
        }

        commandSetList
            .plus(CommandSet("network", initCommandList))
            .let { list ->
                // This is a special situation: reading Android code about 'fully' parameter, it needs to be triggered after setting sims parameters
                fully?.let {
                    // TODO Review conditions for this flag when sim control is not enabled
                    return list.plus(CommandSet("network", listOf(Command("fully", fully!!, true))))
                }
                return list
            }
    }

    class Wifi(var show: Boolean? = null) : SubCommand {
        var level: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..4 }

        override fun getCommands(): List<Command> {
            val commandList = mutableListOf<Command>().apply {
                show?.let {
                    add(Command("wifi", show!!, true))
                    if (show ?: false) {
                        level?.let { add(Command("level", level!!)) }
                    }
                }
            }
            return commandList
        }
    }

    class Mobile(var show: Boolean? = null, var dataType: DataType? = null) : SubCommand {
        var level: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..4 }

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

        override fun getCommands(): List<Command> {
            return mutableListOf<Command>().apply {
                show?.let {
                    add(Command("mobile", show!!, true))
                    if (show ?: false) {
                        dataType?.let { add(Command("datatype", dataType!!.value)) }
                        level?.let { add(Command("level", level!!)) }
                    }
                }
            }
        }
    }

    fun wifi(init: Wifi.() -> Unit) {
        val wifi = Wifi()
        wifi.init()
        commandSetList.add(CommandSet("network", wifi.getCommands()))
    }

    fun mobile(init: Mobile.() -> Unit) {
        val mobile = Mobile()
        mobile.init()
        commandSetList.add(CommandSet("network", mobile.getCommands()))
    }
}

class Bars(var mode: Mode? = null) : CommandHolder() {
    enum class Mode(val value: String) {
        OPAQUE("opaque"),
        SEMI_TRANSPARENT("semi-transparent"),
        TRANSPARENT("transparent");
    }

    override fun getCommandSets(): List<CommandSet> {
        var commandSetList = emptyList<CommandSet>()
        mode?.let {
            val commandList = listOf(Command("mode", mode!!.value))
            commandSetList = listOf(CommandSet("bars", commandList))
        }
        return commandSetList
    }
}

class Status(var volume: VolumeValues? = null,
             var bluetooth: BluetoothValues? = null,
             var location: Boolean? = null,
             var alarm: Boolean? = null,
             var sync: Boolean? = null,
             var tty: Boolean? = null,
             var eri: Boolean? = null,
             var mute: Boolean? = null,
             var speakerPhone: Boolean? = null) : CommandHolder() {

    enum class VolumeValues(val value: String) {
        SILENT("silent"),
        VIBRATE("vibrate"),
        HIDE("hide")
    }

    enum class BluetoothValues(val value: String) {
        CONNECTED("connected"),
        DISCONNECTED("disconnected"),
        HIDE("hide")
    }

    override fun getCommandSets(): List<CommandSet> {
        val commandSetList = mutableListOf<Command>()
        volume?.let { commandSetList.add(Command("volume", volume!!.value)) }
        bluetooth?.let { commandSetList.add(Command("bluetooth", bluetooth!!.value)) }
        location?.let { commandSetList.add(Command("location", location!!, true)) }
        alarm?.let { commandSetList.add(Command("alarm", alarm!!, true)) }
        sync?.let { commandSetList.add(Command("sync", sync!!, true)) }
        tty?.let { commandSetList.add(Command("tty", tty!!, true)) }
        eri?.let { commandSetList.add(Command("eri", eri!!, true)) }
        mute?.let { commandSetList.add(Command("mute", mute!!, true)) }
        speakerPhone?.let { commandSetList.add(Command("speakerphone", speakerPhone!!, true)) }

        return listOf(CommandSet("status", commandSetList))
    }

}

class Notifications(var visible: Boolean? = null) : CommandHolder() {
    override fun getCommandSets(): List<CommandSet> {
        val commandList = mutableListOf<Command>().apply {
            visible?.let { add(Command("visible", visible.toString())) }
        }

        return listOf(CommandSet("notifications", commandList))
    }
}

class Clock : CommandHolder() {
    var hours: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..23 }
    var minutes: Int? by Delegates.vetoable(null as Int?) { _, _, newValue -> newValue in 0..59 }

    override fun getCommandSets(): List<CommandSet> {
        val calendar = Calendar.getInstance()
        val commandHours = hours ?: calendar.get(Calendar.HOUR_OF_DAY)
        val commandMinutes = minutes ?: calendar.get(Calendar.MINUTE)

        val commandList = listOf(
            Command("hhmm", "%02d%02d".format(commandHours, commandMinutes)))

        return listOf(CommandSet("clock", commandList))
    }

}

class DemoModeDsl {

    companion object {
        fun create(init: DemoModeDsl.() -> Unit): DemoModeDsl {
            val demoModeDsl = DemoModeDsl()
            demoModeDsl.init()
            return demoModeDsl
        }
    }

    var commandSetList = emptyList<CommandSet>()

    private fun <T : CommandHolder> initCommand(commandHolder: T, init: T.() -> Unit): T {
        commandHolder.init()
        commandSetList += commandHolder.getCommandSets()
        return commandHolder
    }

    fun battery(init: Battery.() -> Unit): Battery = initCommand(Battery(), init)

    fun network(init: Network.() -> Unit): Network = initCommand(Network(), init)

    fun bars(init: Bars.() -> Unit): Bars = initCommand(Bars(), init)

    fun status(init: Status.() -> Unit): Status = initCommand(Status(), init)

    fun notifications(init: Notifications.() -> Unit): Notifications = initCommand(Notifications(), init)

    fun clock(init: Clock.() -> Unit): Clock = initCommand(Clock(), init)

    fun enter(ctx: Context) {
        commandSetList.forEach { (setName, setCommands) ->
            // Prepare intent and command to send
            val intent = Intent(DemoMode.ACTION_DEMO)
                .addCommandSet(setName, setCommands)
            // Send broadcast to system demo mode receiver
            ctx.sendBroadcast(intent)
        }
    }

    fun exit(ctx: Context) {
        // Just launch the exit intent
        val intent = Intent(DemoMode.ACTION_DEMO)
            .addCommand(Command("command", "exit"))
        ctx.sendBroadcast(intent)
    }

    private fun Intent.addCommandSet(setName: String, setList: List<Command>): Intent {
        setList.isNotEmpty().let {
            addCommand(Command(DemoMode.EXTRA_COMMAND, setName))
            setList.forEach { addCommand(it) }
        }
        return this
    }

    private fun Intent.addCommand(command: Command): Intent {
        putExtra(command.name, command.value)
        return this
    }
}

fun main(args: Array<String>) {
    val dsl = DemoModeDsl.create {
        battery {
            level = 100
            plugged = false
            network {}
        }
        network {
            airplane = true
            fully = true
            wifi {
                show = true
                level = 4
            }
            mobile {
                show = true
                dataType = Network.Mobile.DataType.TYPE_1X
                level = 4
            }
            carrierNetworkChange = true
            sims = 8
            noSim = false
        }
        bars {
            mode = Bars.Mode.OPAQUE
        }
        status {
            volume = Status.VolumeValues.VIBRATE
            bluetooth = Status.BluetoothValues.CONNECTED
            location = true
            alarm = true
            tty = true
            eri = true
            mute = true
            speakerPhone = true
        }
        notifications {
            visible = true // true or false
        }
        clock {
            hours = 11
            minutes = 59
        }
    }
}