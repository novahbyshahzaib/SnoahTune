package com.snoahtune.app.di

import android.content.Context
import androidx.room.Room
import com.snoahtune.app.data.local.MusicDatabase
import com.snoahtune.app.data.local.MediaStoreDataSource
import com.snoahtune.app.data.local.dao.*
import com.snoahtune.app.data.repository.MusicRepositoryImpl
import com.snoahtune.app.domain.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): MusicDatabase =
        Room.databaseBuilder(ctx, MusicDatabase::class.java, "snoahtune.db").build()

    @Provides @Singleton fun provideSongDao(db: MusicDatabase): SongDao = db.songDao()
    @Provides @Singleton fun providePlaylistDao(db: MusicDatabase): PlaylistDao = db.playlistDao()
    @Provides @Singleton fun provideFavoriteDao(db: MusicDatabase): FavoriteDao = db.favoriteDao()

    @Provides @Singleton
    fun provideMediaStore(@ApplicationContext ctx: Context): MediaStoreDataSource =
        MediaStoreDataSource(ctx)

    @Provides @Singleton
    fun provideRepository(
        mediaStore: MediaStoreDataSource,
        songDao: SongDao,
        favoriteDao: FavoriteDao,
        playlistDao: PlaylistDao
    ): MusicRepository = MusicRepositoryImpl(mediaStore, songDao, favoriteDao, playlistDao)
}
