package ru.kvait.somegraph.data.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.kvait.somegraph.data.app.di.databaseModule
import ru.kvait.somegraph.data.app.di.repositoryModule
import ru.kvait.somegraph.data.app.di.viewModelModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(listOf(viewModelModule, databaseModule, repositoryModule))
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}