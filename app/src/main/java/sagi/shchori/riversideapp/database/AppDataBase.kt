package sagi.shchori.riversideapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sagi.shchori.riversideapp.ui.models.Movie

@Database(entities = [Movie::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {

        private var INSTANCE: AppDataBase? = null

        fun getDB(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "appDataBase"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}