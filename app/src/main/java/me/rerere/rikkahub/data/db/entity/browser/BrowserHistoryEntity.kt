package me.rerere.rikkahub.data.db.entity.browser

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "browser_history")
data class BrowserHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val url: String = "",
    val visitedAt: Long = System.currentTimeMillis()
)
