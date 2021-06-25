package ru.kvait.somegraph.data.app.di

import android.app.Application
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.kvait.somegraph.viewmodel.PointViewModel
import ru.kvait.somegraph.data.AppDatabase
import ru.kvait.somegraph.data.AppDatabase.Companion.getInstance
import ru.kvait.somegraph.data.dao.PointDao
import ru.kvait.somegraph.data.repository.PointRepository


val databaseModule = module {

    fun provideDatabase(application: Application): AppDatabase{
        return getInstance(application)
    }

    fun provideDao(database: AppDatabase): PointDao{
        return database.pointDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}

val repositoryModule = module {
    fun providePointRepository(pointDao: PointDao): PointRepository{
        return PointRepository(pointDao)
    }

    single { providePointRepository(get()) }
}

val viewModelModule = module {
    single { PointViewModel(get()) }
}



