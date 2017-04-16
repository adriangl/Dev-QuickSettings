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

import android.database.ContentObserver;
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Handler
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.adriangl.devquicktiles.R
import timber.log.Timber

abstract class DevelopmentTileService<T : Any> : TileService() {

    private val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            Timber.d("Change received from uri: %s, updating tile...", uri)
            updateState()
        }
    }

    override fun onStartListening() {
        Timber.d("Tile started listening: {label=%s, state=%s}", qsTile.label, qsTile.state)

        getSettingsUri().forEach { uri ->
            Timber.d("Registering content observer for tile: {label=%s} with uri %s", qsTile.label, uri)
            contentResolver.registerContentObserver(uri, false, contentObserver)
        }

        updateState()
    }

    override fun onStopListening() {
        Timber.d("Unregistering content observer for tile: {label=%s}", qsTile.label)
        contentResolver.unregisterContentObserver(contentObserver)
    }

    override fun onClick() {
        Timber.d("Tile clicked: {label=%s, state=%s}", qsTile.label, qsTile.state)
        setNextValue()
    }

    private fun setNextValue() {
        val value = queryValue()
        val newIndex = ((getValueList().indexOf(value) + 1) % getValueList().size)
        val newValue = getValueList()[newIndex]
        Timber.d("New value: %s, Tile: {label=%s, state=%s}", value, qsTile.label, qsTile.state)

        // Disable tile while setting the value
        qsTile.state = Tile.STATE_UNAVAILABLE
        qsTile.updateTile()

        try {
            if (saveValue(newValue)) {
                Timber.d("Tile value saved: {label=%s, value=%s}", qsTile.label, newValue)
                // New value change should arrive via content observer, so no need to update the tile
                // here
            } else {
                Timber.w("Tile value could not be saved: {label=%s, value=%s}", qsTile.label, newValue)
                updateState()
            }
        } catch (e: Exception) {
            val permissionNotGrantedString = getString(R.string.qs_permissions_not_granted)
            Toast.makeText(applicationContext, permissionNotGrantedString, Toast.LENGTH_LONG)
                    .show()
            Timber.e(e, permissionNotGrantedString)
        }
    }

    private fun updateState() {
        val value = queryValue()

        qsTile.state = if (isActive(value)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.label = getLabel(value)
        qsTile.icon = getIcon(value)

        qsTile.updateTile()

        Timber.d("Tile updated: {label=%s, state=%s, value=%s}", qsTile.label, qsTile.state, value)
    }

    abstract fun isActive(value: T): Boolean

    abstract fun getValueList(): List<T>

    abstract fun queryValue(): T

    abstract fun saveValue(value: T): Boolean

    abstract fun getIcon(value: T): Icon?

    abstract fun getLabel(value: T): CharSequence?

    abstract fun getSettingsUri(): List<Uri>

}
