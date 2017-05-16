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

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.adriangl.devquicktiles.R
import com.adriangl.devquicktiles.base.App
import timber.log.Timber
import javax.inject.Inject

abstract class DevelopmentTileService : TileService() {

    @Inject lateinit var tileStatusController: TileStatusController
    lateinit var delegate: DevelopmentSettingDelegate

    private val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            Timber.d("Change received from uri: %s, updating tile...", uri)
            updateState()
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Inject Dagger data
        App.component.inject(this)
        delegate = App.component.settingsDelegateMap()[this.javaClass]!!
    }

    override fun onStartListening() {
        Timber.d("Tile started listening: {label=%s, state=%s}", qsTile.label, qsTile.state)

        delegate.getSettingsUri().forEach { uri ->
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
        val value = delegate.queryValue()
        val valueList = delegate.getValueList()

        val newIndex = ((valueList.indexOf(value) + 1) % valueList.size)
        val newValue = valueList[newIndex]
        Timber.d("New value: %s for tile: {label=%s, state=%s, value=%s}",
                newValue,
                qsTile.label,
                qsTile.state,
                value)

        // Disable tile while setting the value
        qsTile.state = Tile.STATE_UNAVAILABLE
        qsTile.updateTile()

        try {
            if (delegate.saveValue(newValue)) {
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
        val value = delegate.queryValue()

        qsTile.state = if (delegate.isActive(value)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.label = delegate.getLabel(value)
        qsTile.icon = delegate.getIcon(value)

        qsTile.updateTile()

        Timber.d("Tile updated: {label=%s, state=%s, value=%s}", qsTile.label, qsTile.state, value)
    }
}
