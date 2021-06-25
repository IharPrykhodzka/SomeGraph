package ru.kvait.somegraph.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kvait.somegraph.data.dao.PointDao
import ru.kvait.somegraph.data.model.Point

class PointRepository(private val pointDao: PointDao) {

    var data = pointDao.getAll()

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            data = pointDao.getAll()
        }
    }

    suspend fun update(points: MutableList<Point>){
        withContext(Dispatchers.IO) {
            pointDao.add(points)
        }
    }
}