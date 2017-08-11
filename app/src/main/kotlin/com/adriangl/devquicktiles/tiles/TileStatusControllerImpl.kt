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

package com.adriangl.devquicktiles.tiles

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.service.quicksettings.TileService
import com.adriangl.devquicktiles.base.AppScope
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Basic implementation of [TileStatusController] that handles tile status and saves it in [android.content.SharedPreferences].
 */
@AppScope
class TileStatusControllerImpl @Inject constructor(context: Context) : TileStatusController {
    val sharedPreferencesInstance = PreferenceManager.getDefaultSharedPreferences(context)!!
    val tileStatusMap: HashMap<KClass<out TileService>, TileStatus> by lazy {
        unflattenStatusFromDisk()
    }

    override fun setTileStatus(tileClass: KClass<out TileService>, tileStatus: TileStatus) {
        // Save to memory map
        tileStatusMap[tileClass] = tileStatus
        // Save map to disk
        flattenStatusToDisk(tileStatusMap)
    }

    override fun getTileStatus(tileClass: KClass<out TileService>): TileStatus? {
        return tileStatusMap[tileClass]
    }

    @Suppress("UNCHECKED_CAST")
    private fun unflattenStatusFromDisk(): HashMap<KClass<out TileService>, TileStatus> {
        val tileStatusMap = hashMapOf<KClass<out TileService>, TileStatus>()
        val string = sharedPreferencesInstance.getString("tile_status", null)
        string?.split("|")?.forEach { flattenedStatus ->
            val splitStatus = flattenedStatus.split(":")
            tileStatusMap.put(
                Class.forName(splitStatus[0]).kotlin as KClass<out DevelopmentTileService>,
                TileStatus(
                    splitStatus[1].toBoolean(),
                    splitStatus[2].toInt(),
                    splitStatus[3]))
            return@forEach
        }
        return tileStatusMap
    }

    @SuppressLint("CommitPrefEdits")
    private fun flattenStatusToDisk(tileStatusMap: HashMap<KClass<out TileService>, TileStatus>) {
        val statusString = tileStatusMap.entries.map { (clazz, status) ->
            "%s:%s:%s:%s".format(clazz.qualifiedName, status.added, status.state, status.value)
        }.joinToString("|")

        sharedPreferencesInstance.edit().putString("tile_status", statusString).commit()
    }

}
