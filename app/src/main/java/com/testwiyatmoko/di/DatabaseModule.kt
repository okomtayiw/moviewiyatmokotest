package com.testwiyatmoko.di

import android.content.Context
import androidx.room.Room
import com.testwiyatmoko.data.database.MoviesDao
import com.testwiyatmoko.data.database.MoviesDatabase
import com.testwiyatmoko.until.Constans.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        MoviesDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: MoviesDatabase) = database.moviesDao()

}