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
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.support.v4.app.NavUtils
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import com.adriangl.devquicktiles.R

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class TileSettingsActivity : AppCompatPreferenceActivity() {

    private var headers: MutableList<Header>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = getSupportActionBar()
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setListAdapter(adapter: ListAdapter) {
        if (headers == null) {
            headers = ArrayList()
            // When the saved state provides the list of headers, onBuildHeaders is not called
            // so we build it from the adapter given, then use our own adapter

            for (i in 0..adapter.count) {
                headers!!.add(adapter.getItem(i) as PreferenceActivity.Header)
            }
        }

        super.setListAdapter(HeaderAdapter(this, headers!!, android.R.layout.simple_list_item_1, true))
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    /** {@inheritDoc}  */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /** {@inheritDoc}  */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        headers = mutableListOf<PreferenceActivity.Header>().apply { addAll(target) }
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
            || KeepScreenOnPreferenceFragment::class.java.name == fragmentName
            || DemoModePreferenceFragment::class.java.name == fragmentName
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private fun isXLargeTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >=
            Configuration.SCREENLAYOUT_SIZE_XLARGE
    }

    private class HeaderAdapter(context: Context, objects: List<Header>, private val mLayoutResId: Int,
                                private val mRemoveIconIfEmpty: Boolean) : ArrayAdapter<Header>(context, 0, objects) {
        private class HeaderViewHolder {
            internal var icon: ImageView? = null
            internal var title: TextView? = null
            internal var summary: TextView? = null
        }

        private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val holder: HeaderViewHolder
            val view: View

            if (convertView == null) {
                view = mInflater.inflate(mLayoutResId, parent, false)
                holder = HeaderViewHolder()
                /*
                holder.icon = view.findViewById(com.android.internal.R.id.icon) as ImageView
                holder.title = view.findViewById(com.android.internal.R.id.title) as TextView
                holder.summary = view.findViewById(com.android.internal.R.id.summary) as TextView
                */
                view.tag = holder
            } else {
                view = convertView
                holder = view.tag as HeaderViewHolder
            }

            // All view fields must be updated every time, because the view may be recycled
            val header = getItem(position)
            if (mRemoveIconIfEmpty) {
                if (header!!.iconRes == 0) {
                    holder.icon!!.visibility = View.GONE
                } else {
                    holder.icon!!.visibility = View.VISIBLE
                    holder.icon!!.setImageResource(header.iconRes)
                }
            } else {
                holder.icon!!.setImageResource(header!!.iconRes)
            }
            holder.title!!.text = header.getTitle(context.resources)
            val summary = header.getSummary(context.resources)
            if (!TextUtils.isEmpty(summary)) {
                holder.summary!!.visibility = View.VISIBLE
                holder.summary!!.text = summary
            } else {
                holder.summary!!.visibility = View.GONE
            }

            return view
        }
    }
}
