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
import android.view.MenuItem
import com.adriangl.devquicktiles.R

/**
 * This fragment shows demo mode preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class DemoModePreferenceFragment : PreferenceFragment() {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
        val stringValue = value.toString()

        if (preference is ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            val listPreference = preference
            val index = listPreference.findIndexOfValue(stringValue)

            // Set the summary to reflect the new value.
            preference.setSummary(
                if (index >= 0)
                    listPreference.entries[index]
                else
                    null)

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.summary = stringValue
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_demo_mode)
        setHasOptionsMenu(true)

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        val preferenceKeyStrings = listOf(
            getString(R.string.pref_demo_mode_clock_mode_key),
            getString(R.string.pref_demo_mode_battery_level_key),
            getString(R.string.pref_demo_mode_network_wifi_level_key),
            getString(R.string.pref_demo_mode_network_mobile_level_key),
            getString(R.string.pref_demo_mode_network_mobile_datatype_key),
            getString(R.string.pref_demo_mode_network_sims_key),
            getString(R.string.pref_demo_mode_bars_mode_key)
        )

        for (key in preferenceKeyStrings) {
            bindPreferenceSummaryToValue(findPreference(key))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            startActivity(Intent(activity, TileSettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.

     * @see .sBindPreferenceSummaryToValueListener
     */
    @Suppress("MaxLineLength")
    private fun bindPreferenceSummaryToValue(preference: Preference,
                                             listener: Preference.OnPreferenceChangeListener =
                                             Preference.OnPreferenceChangeListener { _, _ -> true }) {
        // Set the listener to watch for value changes.
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { changedPreference, newValue ->
            listener.onPreferenceChange(changedPreference, newValue) &&
                sBindPreferenceSummaryToValueListener.onPreferenceChange(changedPreference, newValue)
        }

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
            PreferenceManager
                .getDefaultSharedPreferences(preference.context)
                .getString(preference.key, ""))
    }
}
