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

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Icon
import android.net.Uri
import com.adriangl.devquicktiles.base.AppScope
import com.adriangl.devquicktiles.tiles.animatorduration.AnimatorDurationSettingDelegate
import com.adriangl.devquicktiles.tiles.animatorduration.AnimatorDurationTileService
import com.adriangl.devquicktiles.tiles.demomode.DemoModeSettingDelegate
import com.adriangl.devquicktiles.tiles.demomode.DemoModeTileService
import com.adriangl.devquicktiles.tiles.finishactivities.FinishActivitiesSettingDelegate
import com.adriangl.devquicktiles.tiles.finishactivities.FinishActivitiesTileService
import com.adriangl.devquicktiles.tiles.screenon.KeepScreenOnSettingDelegate
import com.adriangl.devquicktiles.tiles.screenon.KeepScreenOnTileService
import com.adriangl.devquicktiles.tiles.show_taps.ShowTapsSettingDelegate
import com.adriangl.devquicktiles.tiles.show_taps.ShowTapsTileService
import com.adriangl.devquicktiles.tiles.usbdebug.UsbDebuggingSettingDelegate
import com.adriangl.devquicktiles.tiles.usbdebug.UsbDebuggingTileService
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

/**
 * Base class to implement settings delegate.
 */
abstract class DevelopmentSettingDelegate constructor(val context: Context,
                                                      val contentResolver: ContentResolver) {
    /**
     * Returns if the tile is in an "active" state.
     */
    abstract fun isActive(value: String): Boolean

    /**
     * Returns the possible values that the tile can have.
     */
    abstract fun getValueList(): List<String>

    /**
     * Queries the value that the tile service holds.
     */
    abstract fun queryValue(): String

    /**
     * Saves the given tile value.
     */
    abstract fun saveValue(value: String): Boolean

    /**
     * Returns an [Icon] for a given tile value. May be null.
     */
    abstract fun getIcon(value: String): Icon?

    /**
     * Returns a [CharSequence] for a given tile value. May be null.
     */
    abstract fun getLabel(value: String): CharSequence?

    /**
     * Returns a list of [Uri]s that the tile uses to save values.
     */
    abstract fun getSettingsUri(): List<Uri>

    @Module
    @Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction", "unused")
    abstract class DevelopmentSettingDelegateModule {
        @AppScope
        @Binds @ClassKey(AnimatorDurationTileService::class) @IntoMap
        abstract fun provideAnimatorDurationSettingDelegate(delegate: AnimatorDurationSettingDelegate): DevelopmentSettingDelegate

        @AppScope
        @Binds @ClassKey(DemoModeTileService::class) @IntoMap
        abstract fun provideDemoModeSettingDelegate(delegate: DemoModeSettingDelegate): DevelopmentSettingDelegate

        @AppScope
        @Binds @ClassKey(FinishActivitiesTileService::class) @IntoMap
        abstract fun provideFinishActivitiesSettingDelegate(delegate: FinishActivitiesSettingDelegate): DevelopmentSettingDelegate

        @AppScope
        @Binds @ClassKey(KeepScreenOnTileService::class) @IntoMap
        abstract fun provideKeepScreenOnSettingDelegate(delegate: KeepScreenOnSettingDelegate): DevelopmentSettingDelegate

        @AppScope
        @Binds @ClassKey(ShowTapsTileService::class) @IntoMap
        abstract fun provideShowTapsSettingDelegate(delegate: ShowTapsSettingDelegate): DevelopmentSettingDelegate

        @AppScope
        @Binds @ClassKey(UsbDebuggingTileService::class) @IntoMap
        abstract fun provideUsbDebuggingSettingDelegate(delegate: UsbDebuggingSettingDelegate): DevelopmentSettingDelegate
    }
}