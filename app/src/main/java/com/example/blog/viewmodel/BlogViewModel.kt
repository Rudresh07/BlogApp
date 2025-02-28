package com.example.blog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blog.domain.data.Blog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlogViewModel : ViewModel() {
    private val _blogs = MutableStateFlow<List<Blog>>(emptyList())
    val blogs: StateFlow<List<Blog>> = _blogs

    private val _selectedBlog = MutableStateFlow<Blog?>(null)
    val selectedBlog: StateFlow<Blog?> = _selectedBlog

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _page = MutableStateFlow(1) // Track the current page
    val page: StateFlow<Int> = _page

    private val blogApi = BlogApiService.create()

    fun fetchBlogs( page: Int = 1,perPage: Int = 10) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null // Reset error message

            try {
                val response = blogApi.getBlogs(perPage, page)
                if (response.isSuccessful) {
                    response.body()?.let { newBlogs ->
                        if (page == 1) {
                            // Replace the list for the first page
                            _blogs.value = newBlogs
                        } else {
                            // Append new blogs for subsequent pages
                            _blogs.update { it + newBlogs }
                        }
                    } ?: run {
                        _blogs.value = emptyList()
                        _errorMessage.value = "No blogs found"
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load blogs: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Fetch a single blog by ID
    fun getBlogById(id: Int) {
        viewModelScope.launch {
            _loading.emit(true)
            _errorMessage.emit(null) // Reset error message

            try {
                val response = blogApi.getBlogById(id)

                if (response.isSuccessful) {
                    response.body()?.let { blog ->
                        _selectedBlog.emit(blog)


                    } ?: run {
                        _selectedBlog.emit(null)
                        _errorMessage.emit("Blog not found")
                    }
                } else {
                    _errorMessage.emit("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.emit("Failed to load blog: ${e.localizedMessage}")
            } finally {
                _loading.emit(false)
            }
        }
    }

    fun loadNextPage() {
        _page.value += 1 // Increment the page number
        fetchBlogs(page = _page.value) // Fetch the next page
    }
}
