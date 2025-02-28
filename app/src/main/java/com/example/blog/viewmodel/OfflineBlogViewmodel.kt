package com.example.blog.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.blog.domain.data.Blog
import com.example.blog.domain.data.OfflineBlog
import com.example.blog.room.BlogDao
import com.example.blog.room.BlogDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OfflineBlogViewmodel(private val dao: BlogDao) : ViewModel() {

    private val _selectedBlog = MutableStateFlow<Blog?>(null)
    val selectedBlog: StateFlow<Blog?> = _selectedBlog

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _offlineBlogs = MutableStateFlow<List<OfflineBlog>>(emptyList())
    val offlineBlogs: StateFlow<List<OfflineBlog>> = _offlineBlogs

    private val blogApi = BlogApiService.create()

    fun fetchAndSaveBlog(id: Int) {
        viewModelScope.launch {
            _errorMessage.emit(null) // Reset error message

            try {
                val response = blogApi.getBlogById(id)

                if (response.isSuccessful) {
                    response.body()?.let { blog ->

                        val offlineBlog = OfflineBlog(
                            BlogId = blog.id,
                            title = blog.title.rendered,
                            content = blog.content.rendered,
                            image =  blog.jetpack_featured_media_url
                        )

                        // 🔥 Run DB operations in Dispatchers.IO (background thread)
                        viewModelScope.launch(Dispatchers.IO) {
                            val existingBlog = dao.getBlogById(blog.id) // ✅ Now on background thread
                            if (existingBlog == null) {
                                dao.insertBlog(offlineBlog)
                                Log.d("LOCAL_DB", "Blog saved locally: ID=${blog.id}, Title=${blog.title.rendered}")
                            } else {
                                Log.d("LOCAL_DB", "Blog already exists: ID=${blog.id}")
                            }
                        }

                        _selectedBlog.emit(blog) // UI update (on main thread)
                    } ?: run {
                        _selectedBlog.emit(null)
                        _errorMessage.emit("Blog not found")
                    }
                } else {
                    _errorMessage.emit("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.emit("Failed to load blog: ${e.localizedMessage}")
                Log.e("API_ERROR", "Exception: ${e.localizedMessage}")
            }
        }
    }


    fun getAllBlogs() {
        viewModelScope.launch(Dispatchers.IO) { // ✅ Runs in background
            val blogs = dao.getAllBlogs()
            _offlineBlogs.emit(blogs) // ✅ UI update on main thread
            Log.d("LOCAL_DB", "Fetched ${blogs.size} blogs from local DB")
        }
    }


    fun removeBlog(blogId: Int) {
        viewModelScope.launch(Dispatchers.IO) { // ✅ Runs in background
            dao.deleteBlogById(blogId)
            Log.d("LOCAL_DB", "Deleted blog with ID=$blogId from local DB")
            getAllBlogs() // Refresh local list
        }
    }

    class OfflineBlogViewmodelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OfflineBlogViewmodel::class.java)) {
                val dao = BlogDatabase.getDatabase(context).blogDao()
                return OfflineBlogViewmodel(dao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
