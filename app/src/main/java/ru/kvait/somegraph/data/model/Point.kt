package ru.kvait.somegraph.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.Entry
import ru.kvait.somegraph.MainActivity

@Entity(tableName = "points")
data class Point(
    @PrimaryKey val id: Long,
    var point: Float,
    val serialNumber: Int
) {
    companion object {
        fun Point.toEntry() = Entry(
            this.point,
            this.serialNumber
        )
    }
}