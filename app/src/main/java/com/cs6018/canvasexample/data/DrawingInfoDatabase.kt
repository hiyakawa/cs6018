package com.cs6018.canvasexample.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DrawingInfo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DrawingInfoDatabase : RoomDatabase() {
    abstract fun drawingInfoDao(): DrawingInfoDAO

    companion object {
        @Volatile
        private var INSTANCE: DrawingInfoDatabase? = null

        fun getDatabase(context: Context): DrawingInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrawingInfoDatabase::class.java,
                    "drawing_info_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
