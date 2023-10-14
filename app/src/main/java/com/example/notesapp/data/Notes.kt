package com.example.notesapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Notes")
data class Notes(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var title: String,
    var subTitle: String,
    var dateTime: String,
    var text: String,
    var image: String,
    var link: String,
    var color: String
) : Parcelable