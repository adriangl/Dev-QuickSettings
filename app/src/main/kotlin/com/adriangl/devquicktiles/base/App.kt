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

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.adriangl.devquicktiles.BuildConfig
import com.adriangl.devquicktiles.tiles.TileStatusController
import com.adriangl.devquicktiles.tiles.TileStatusControllerImpl
import dagger.Module
import dagger.Provides
import timber.log.Timber

class App : Application() {
    companion object {
        lateinit var component: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        // Init Timber in debug builds
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    /**
     * Global app dependencies
     */
    @Module
    class AppModule(val app: App) {
        @Provides @AppScope
        fun provideApplicationContext(): Context {
            return app
        }

        @Provides @AppScope
        fun provideApplication(): Application {
            return app
        }

        @Provides @AppScope
        fun provideContentResolver(): ContentResolver {
            return app.contentResolver
        }

        @Provides @AppScope
        fun provideTileStatusController(impl: TileStatusControllerImpl): TileStatusController {
            return impl
        }
    }
}