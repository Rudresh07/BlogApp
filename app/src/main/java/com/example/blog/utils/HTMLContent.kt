package com.example.blog.utils

import org.jsoup.Jsoup

data class BlogContent(
    val text: String,
    val imageUrls: List<String>
)

fun parseBlogContent(htmlContent: String): BlogContent {
    val document = Jsoup.parse(htmlContent)
    val text = document.select("p, h1, h2, h3, h4, h5, h6").text() // Extract text from paragraphs and headings
    val imageUrls = document.select("img").map { it.attr("src") } // Extract image URLs
    return BlogContent(text, imageUrls)
}