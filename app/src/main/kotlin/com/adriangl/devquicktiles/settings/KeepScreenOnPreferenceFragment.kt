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

package com.adriangl.devquicktiles.settings

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.view.MenuItem
import com.adriangl.devquicktiles.R
import com.adriangl.devquicktiles.base.App
import com.adriangl.devquicktiles.tiles.TileStatusController
import com.adriangl.devquicktiles.tiles.screenon.KeepScreenOnTileService
import com.adriangl.devquicktiles.utils.addOnPrefChangeListener
import javax.inject.Inject

/**
 * This fragment shows keep screen on preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class KeepScreenOnPreferenceFragment : PreferenceFragment() {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
        val stringValue = value.toString()

        if (preference is ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            val index = preference.findIndexOfValue(stringValue)

            // Set the summary to reflect the new value.
            preference.setSummary(
                if (index >= 0)
                    preference.entries[index]
                else
                    null)

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.summary = stringValue
        }
        true
    }

    @Inject lateinit var tileStatusController: TileStatusController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component.inject(this)

        addPreferencesFromResource(R.xml.pref_keep_screen_on)
        setHasOptionsMenu(true)

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        val keepScreenOnPreference = findPreference(getString(R.string.pref_keep_screen_on_key))

        keepScreenOnPreference.addOnPrefChangeListener(Preference.OnPreferenceChangeListener { _, newValue ->
            val tileStatus = tileStatusController.getTileStatus(KeepScreenOnTileService::class)
            tileStatus?.let { (added, state) ->
                if (added && state == Tile.STATE_ACTIVE) {
                    App.component.settingsDelegateMap()[KeepScreenOnTileService::class.java]?.saveValue(newValue as String)
                }
            }
            true
        })

        bindSummaryToPreferenceChange(keepScreenOnPreference)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            startActivity(Intent(activity, TileSettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindSummaryToPreferenceChange(preference: Preference) {
        preference.addOnPrefChangeListener(sBindPreferenceSummaryToValueListener)
        // Call the listener immediately after loading so it changes the summary value as soon as we enter the fragment
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
            preference,
            PreferenceManager
                .getDefaultSharedPreferences(preference.context)
                .getString(preference.key, ""))
    }
}