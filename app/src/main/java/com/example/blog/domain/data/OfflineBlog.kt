package com.example.blog.domain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OfflineBlog")
data class OfflineBlog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val BlogId: Int,
    val title: String,
    val content: String,
    val image: String
)