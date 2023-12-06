package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class DrawingObject(
    val id: Int,
    val creatorId: String,
    val title: String,
    val lastModifiedDate: Long,
    val createdDate: Long,
    val imagePath: String,
    val thumbnail: String
)

object Drawing : IntIdTable() {
    val creatorId = varchar("creatorId", 255)
    val title = varchar("title", 255)
    val lastModifiedDate = long("lastModifiedDate")
    val createdDate = long("createdDate")
    val imagePath = varchar("imagePath", 255)
    val thumbnail = text("thumbnail")
}