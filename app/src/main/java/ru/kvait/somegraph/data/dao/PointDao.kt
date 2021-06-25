package ru.kvait.somegraph.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kvait.somegraph.data.model.Point

@Dao
interface PointDao {
    @Query("SELECT * FROM points")
    fun getAll(): LiveData<MutableList<Point>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(points: MutableList<Point>)
}