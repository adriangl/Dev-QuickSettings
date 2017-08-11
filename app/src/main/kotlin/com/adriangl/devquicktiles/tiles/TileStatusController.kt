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

import android.service.quicksettings.TileService
import kotlin.reflect.KClass

/**
 * Interface used to keep track of the tiles' current status along the app.
 */
interface TileStatusController {
    /**
     * Stores the current tile status.
     */
    fun setTileStatus(tileClass: KClass<out TileService>, tileStatus: TileStatus)

    /**
     * Retrieves the saved status for a given tile
     */
    fun getTileStatus(tileClass: KClass<out TileService>): TileStatus?
}