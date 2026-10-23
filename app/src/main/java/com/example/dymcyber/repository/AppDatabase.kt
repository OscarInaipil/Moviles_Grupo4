package com.example.dymcyber.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dymcyber.model.Candidata
import com.example.dymcyber.model.Evaluacion

@Database(entities = [Candidata::class, Evaluacion::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun candidataDao(): CandidataDao
    abstract fun evaluacionDao(): EvaluacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE candidatas ADD COLUMN cvNombre TEXT")
                db.execSQL("ALTER TABLE candidatas ADD COLUMN cvUri TEXT")
            }
        }

        val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS evaluaciones (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        candidataId INTEGER NOT NULL,
                        evaluadoraId TEXT NOT NULL,
                        notaLiderazgo INTEGER NOT NULL,
                        notaTrayectoria INTEGER NOT NULL,
                        notaImpacto INTEGER NOT NULL,
                        notaInnovacion INTEGER NOT NULL,
                        puntajePromedio REAL NOT NULL,
                        observaciones TEXT NOT NULL,
                        fechaEvaluacion INTEGER NOT NULL,
                        FOREIGN KEY(candidataId) REFERENCES candidatas(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_evaluaciones_candidataId ON evaluaciones(candidataId)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dymcyber_db"
                ).addMigrations(MIGRACION_1_2, MIGRACION_2_3).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
