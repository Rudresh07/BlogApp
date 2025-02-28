package com.example.blog.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.blog.domain.data.OfflineBlog

@Dao
interface BlogDao {
    @Query("SELECT * FROM OfflineBlog")
    suspend fun getAllBlogs(): List<OfflineBlog>

    @Query("SELECT * FROM OfflineBlog WHERE BlogId = :blogId")
    suspend fun getBlogById(blogId: Int): OfflineBlog?

    @Query("DELETE FROM OfflineBlog WHERE BlogId = :blogId")
    suspend fun deleteBlogById(blogId: Int)

    @Insert
    suspend fun insertBlog(blog: OfflineBlog)
}