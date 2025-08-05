package com.apero.minhnt1.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.apero.minhnt1.database.playlist.Playlist
import com.apero.minhnt1.database.playlist.PlaylistDao
//import com.apero.minhnt1.database.playlist.PlaylistSongCrossRef
import com.apero.minhnt1.database.song.Song
import com.apero.minhnt1.database.song.SongDao
import com.apero.minhnt1.database.user.User
import com.apero.minhnt1.database.user.UserDao

@Database(entities = [User::class, Song::class, Playlist::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { (instance ?: buildDatabase(context).also { instance = it }) }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                        context = context,
                        klass = AppDatabase::class.java,
                        name = "AppDatabase"
                    ).fallbackToDestructiveMigration(true)
                //.allowMainThreadQueries()
                .build()

//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//        fun getInstance(context: Context): AppDatabase{
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java, "app_database"
//                )
//                    .fallbackToDestructiveMigration()
//                    .build()
//                    .also { INSTANCE = it }
//            }
//
//        }

    }
}