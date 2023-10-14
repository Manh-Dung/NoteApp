package com.example.notesapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface NoteDao {
    // Nếu insert note trùng primary key thì sẽ bỏ qua
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(notes: Notes)

    @Update
    suspend fun updateNote(notes: Notes?)

    @Delete
    suspend fun deleteNote(notes: Notes?)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Notes>>

    @Query("SELECT * FROM notes where title like :searchKey or subTitle like :searchKey or text like :searchKey")
    fun searchDatabase(searchKey: String?): LiveData<List<Notes>>
}