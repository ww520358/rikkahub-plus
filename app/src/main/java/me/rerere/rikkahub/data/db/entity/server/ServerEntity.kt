package me.rerere.rikkahub.data.db.entity.server

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val host: String = "",
    val port: Int = 22,
    val username: String = "",
    val password: String = "",
    val useKeyAuth: Boolean = false,
    val privateKey: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
