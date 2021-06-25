package ru.kvait.somegraph.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.pbreakers.mobile.androidtest.udacity.utils.LoadingState
import ru.kvait.somegraph.data.model.Point
import ru.kvait.somegraph.data.repository.PointRepository

class PointViewModel(private val pointRepository: PointRepository) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    val data = pointRepository.data

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                _loadingState.value = LoadingState.LOADING
                pointRepository.refresh()
                _loadingState.value = LoadingState.LOADED
            } catch (e: Exception) {
                _loadingState.value = LoadingState.error(e.message)
            }
        }
    }

    fun updateData(points: MutableList<Point>){
        viewModelScope.launch {
            try {
                pointRepository.update(points)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}