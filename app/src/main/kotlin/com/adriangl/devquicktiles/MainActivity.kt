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
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.adriangl.devquicktiles.tiles.views.QsDescriptionsItem
import com.adriangl.devquicktiles.tiles.views.QsDescriptionsRecyclerViewAdapter
import com.adriangl.devquicktiles.utils.PermissionUtils
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.qs_tile_descriptions_recyclerview)
    lateinit var qsDescriptionsRecyclerView: RecyclerView
    @BindView(R.id.grant_permissions_container)
    lateinit var grantPermissionsView: View
    @BindView(R.id.grant_permissions_text)
    lateinit var grantPermissionsText: TextView
    @BindView(R.id.permissions_share)
    lateinit var permissionsShareButton: Button

    private lateinit var drawer : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

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

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_settings -> showSettings()
            R.id.nav_about -> showAbout()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getDescriptionItems(): List<QsDescriptionsItem> {
        return listOf(
                QsDescriptionsItem(
                        R.drawable.ic_qs_usb_debugging_enabled,
                        getString(R.string.qs_usb_debugging),
                        getString(R.string.qs_usb_debugging_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_demo_mode_enabled,
                        getString(R.string.qs_demo_mode),
                        getString(R.string.qs_demo_mode_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_keep_screen_on_enabled,
                        getString(R.string.qs_keep_screen_on),
                        getString(R.string.qs_keep_screen_on_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_animator_duration_enabled,
                        getString(R.string.qs_animator_duration),
                        getString(R.string.qs_animator_duration_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_show_taps_enabled,
                        getString(R.string.qs_show_taps),
                        getString(R.string.qs_show_taps_description)),
                QsDescriptionsItem(
                        R.drawable.ic_qs_finish_activities_enabled,
                        getString(R.string.qs_finish_activities),
                        getString(R.string.qs_finish_activities_description))
        )
    }

    private fun getFormattedPermissions() = getString(R.string.permissions_to_grant, BuildConfig.APPLICATION_ID)

    private fun sharePermissions() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
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

    private fun showSettings() {

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
}
