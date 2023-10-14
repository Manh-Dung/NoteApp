package com.example.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Khi thay đổi cấu trúc của bảng ví dụ thêm 1 cột thì cập nhật version
// Nếu exportSchema = true thì hệ thống sẽ tự tạo ra 1 file .json
@Database(entities = [Notes::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        // Giá trị của biến volatile sẽ không bao giờ được lưu vào bộ nhớ đệm
        // và tất cả lượt ghi và đọc sẽ xuất phát và trở về từ bộ nhớ chính.
        // Điều này giúp đảm bảo giá trị của INSTANCE luôn cập nhật và giống nhau cho tất cả các luồng thực thi.
        // Tức là khi một luồng thực hiện thay đổi đối với INSTANCE, tất cả luồng khác sẽ thấy thay đổi đó ngay lập tức.
        @Volatile
        var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            // Chỉ cho sử dụng 1 luồng tại 1 thời điểm
            // Và nếu INSTANCE null thì thực hiện đoạn sau
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    "notes.db"
                )
                    // Nếu bảng bị thay đổi thì sẽ không mất dữ liệu cũ mà dùng nó cho bảng mới
                    .fallbackToDestructiveMigration()
                    .build()
                // Gán như thế này để luôn cập nhật INSTANCE mới nhất
                INSTANCE = instance
                return instance
            }
        }

    }

}