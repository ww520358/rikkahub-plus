package me.rerere.rikkahub.data.db.dao.browser

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.entity.browser.BrowserHistoryEntity

@Dao
interface BrowserHistoryDao {
    @Query("SELECT * FROM browser_history ORDER BY visitedAt DESC LIMIT 200")
    fun getAll(): Flow<List<BrowserHistoryEntity>>

    @Insert
    suspend fun insert(history: BrowserHistoryEntity)

    @Delete
    suspend fun delete(history: BrowserHistoryEntity)

    @Query("DELETE FROM browser_history")
    suspend fun deleteAll()

    @Query("DELETE FROM browser_history WHERE url = :url")
    suspend fun deleteByUrl(url: String)
}
