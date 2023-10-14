package com.example.notesapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentViewModel : ViewModel() {
    private var _sharedData = MutableLiveData<String>()
    private var _deleteConfirm = MutableLiveData<Boolean>()
    private var _color = MutableLiveData<String>()
    val sharedData: LiveData<String> get() = _sharedData
    val deleteConfirm: LiveData<Boolean> get() = _deleteConfirm
    val color: LiveData<String> get() = _color
    fun setSharedData(data: String) {
        _sharedData.value = data
    }

    fun setDeleteConfirm(data: Boolean) {
        _deleteConfirm.value = data
    }

    fun setColor(data: String) {
        _color.value = data
    }
}