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

package com.adriangl.devquicktiles.base

import com.adriangl.devquicktiles.tiles.DevelopmentSettingDelegate
import com.adriangl.devquicktiles.tiles.DevelopmentTileService
import dagger.Component

/**
 * Dagger main component.
 */
@AppScope
@Component(modules = arrayOf(
        App.AppModule::class,
        DevelopmentSettingDelegate.DevelopmentSettingDelegateModule::class))
interface AppComponent {
    fun settingsDelegateMap(): Map<Class<*>, DevelopmentSettingDelegate>

    fun inject(developmentTileService: DevelopmentTileService)
}