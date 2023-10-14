package com.example.notesapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val getAllNotes: LiveData<List<Notes>>
    private val noteDao: NoteDao

    init {
        noteDao = NoteDatabase.getDatabase(application).noteDao()
        getAllNotes = noteDao.getAllNotes()
    }

    fun insertNote(notes: Notes) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.insertNote(notes)
        }
    }

    fun updateNote(notes: Notes?) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.updateNote(notes)
        }
    }

    fun deleteNote(notes: Notes?) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNote(notes)
        }
    }

    fun searchDatabase(searchKey: String?): LiveData<List<Notes>> {
        return noteDao.searchDatabase(searchKey)
    }
}

