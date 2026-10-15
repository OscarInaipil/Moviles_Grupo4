package com.example.dymcyber.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dymcyber.model.Candidata

/**
 * Base de datos Room principal de la aplicación siguiendo el patrón Singleton estándar.
 */
@Database(entities = [Candidata::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun candidataDao(): CandidataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dymcyber_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
