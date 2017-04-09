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

package com.adriangl.devquicktiles

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.adriangl.devquicktiles.tiles.views.QsDescriptionsItem
import com.adriangl.devquicktiles.tiles.views.QsDescriptionsRecyclerViewAdapter
import com.adriangl.devquicktiles.utils.PermissionUtils
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder


class MainActivity : AppCompatActivity() {

    lateinit var qsDescriptionsRecyclerView: RecyclerView
    lateinit var grantPermissionsView: View
    lateinit var grantPermissionsText: TextView
    lateinit var permissionsShareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qsDescriptionsRecyclerView = findViewById(R.id.qs_tile_descriptions_recyclerview) as RecyclerView
        grantPermissionsView = findViewById(R.id.grant_permissions_container)
        grantPermissionsText = findViewById(R.id.grant_permissions_text) as TextView
        permissionsShareButton = findViewById(R.id.permissions_share) as Button

        permissionsShareButton.setOnClickListener { sharePermissions() }
    }

    override fun onResume() {
        super.onResume()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        qsDescriptionsRecyclerView.setHasFixedSize(true)
        qsDescriptionsRecyclerView.isNestedScrollingEnabled = false
        qsDescriptionsRecyclerView.layoutManager = layoutManager

        val adapter = QsDescriptionsRecyclerViewAdapter()
        adapter.setItems(getDescriptionItems())
        qsDescriptionsRecyclerView.adapter = adapter

        grantPermissionsText.text = getFormattedPermissions()

        if (hasNeededPermissions()) grantPermissionsView.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item?.itemId == R.id.menu_about -> showAbout()
        }
        return true
    }

    private fun showAbout() {
        LibsBuilder()
                .withFields(R.string::class.java.fields)
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTitle(getString(R.string.menu_about))
                .withAboutIconShown(true)
                .withAboutAppName(getString(R.string.app_name))
                .withAboutVersionShownName(true)
                .withAboutVersionShownCode(false)
                .withAutoDetect(true)
                .withExcludedLibraries("AndroidIconics", "fastadapter")
                .start(this)
    }

    private fun getDescriptionItems(): List<QsDescriptionsItem> {
        return listOf(
                QsDescriptionsItem(
                        R.drawable.ic_qs_animator_duration_enabled,
                        getString(R.string.qs_animator_duration),
                        getString(R.string.qs_animator_duration_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_demo_mode_enabled,
                        getString(R.string.qs_demo_mode),
                        getString(R.string.qs_demo_mode_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_finish_activities_enabled,
                        getString(R.string.qs_finish_activities),
                        getString(R.string.qs_finish_activities_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_keep_screen_on_enabled,
                        getString(R.string.qs_keep_screen_on),
                        getString(R.string.qs_keep_screen_on_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_usb_debugging_enabled,
                        getString(R.string.qs_usb_debugging),
                        getString(R.string.qs_usb_debugging_description))
        )
    }

    private fun getFormattedPermissions() = getString(R.string.permissions_to_grant, BuildConfig.APPLICATION_ID)

    private fun sharePermissions() {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getString(R.string.share_permissions_subject, getString(R.string.app_name)))
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getFormattedPermissions())
        startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.share_using)))
    }

    private fun hasNeededPermissions(): Boolean {
        return (PermissionUtils.checkSelfPermissionIsGranted(this, android.Manifest.permission.WRITE_SECURE_SETTINGS)
                and (PermissionUtils.checkSelfPermissionIsGranted(this, android.Manifest.permission.DUMP)))
    }
}
