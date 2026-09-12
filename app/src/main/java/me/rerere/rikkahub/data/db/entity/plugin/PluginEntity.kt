package me.rerere.rikkahub.data.db.entity.plugin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plugins")
data class PluginEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val className: String = "",
    val name: String = "",
    val version: String = "",
    val enabled: Boolean = true,
    val apkPath: String? = null,
    val isBuiltin: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
