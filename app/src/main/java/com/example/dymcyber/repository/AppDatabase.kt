package com.example.dymcyber.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dymcyber.model.Candidata

/**
 * Base de datos Room principal de la aplicación siguiendo el patrón Singleton estándar.
 */
@Database(entities = [Candidata::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun candidataDao(): CandidataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE candidatas ADD COLUMN cvNombre TEXT")
                db.execSQL("ALTER TABLE candidatas ADD COLUMN cvUri TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dymcyber_db"
                ).addMigrations(MIGRACION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
