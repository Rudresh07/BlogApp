package com.example.blog.domain.data

data class Links(
    val about: List<About>,
    val author: List<Author>,
    val collection: List<Collection>,
    val curies: List<Cury>,
    val predecessor_version: List<PredecessorVersion>,
    val replies: List<Reply>,
    val self: List<Self>,
    val version_history: List<VersionHistory>,
    val wpattachment: List<WpAttachment>,
    val wpfeaturedmedia: List<WpFeaturedmedia>,
    val wpterm: List<WpTerm>
)