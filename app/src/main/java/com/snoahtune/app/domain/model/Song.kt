package com.snoahtune.app.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,   // milliseconds
    val path: String,
    val dateAdded: Long,
    val size: Long
)
