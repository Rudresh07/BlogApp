package com.example.blog.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.blog.domain.data.OfflineBlog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [OfflineBlog::class], version = 1)
abstract class BlogDatabase : RoomDatabase() {
    abstract fun blogDao(): BlogDao

    companion object {
        const val DATABASE_NAME = "blog_database"

        @JvmStatic
        fun getDatabase(context: Context): BlogDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BlogDatabase::class.java,
                DATABASE_NAME
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    InitBasicData(context)
                }

                fun InitBasicData(context: Context) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val blogDao = getDatabase(context).blogDao()
                    }
                }
            }
            ).build()
        }
    }
}

