package ru.kvait.somegraph.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.kvait.somegraph.data.dao.PointDao
import ru.kvait.somegraph.data.model.Point
import ru.kvait.somegraph.utils.ioThread

@Database(entities = [Point::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val pointDao: PointDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "Points.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        ioThread {
                            getInstance(context).pointDao.add(PREPOPULATE_DATA)
                        }
                    }
                })
                .build()

        val PREPOPULATE_DATA = mutableListOf<Point>(Point(0, 0f, 0))
    }
}