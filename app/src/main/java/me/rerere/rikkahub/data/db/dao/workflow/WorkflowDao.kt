package me.rerere.rikkahub.data.db.dao.workflow

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.entity.workflow.WorkflowEntity

@Dao
interface WorkflowDao {
    @Query("SELECT * FROM workflows ORDER BY createdAt DESC")
    fun getAll(): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows WHERE id = :id")
    suspend fun getById(id: Int): WorkflowEntity?

    @Insert
    suspend fun insert(workflow: WorkflowEntity): Long

    @Update
    suspend fun update(workflow: WorkflowEntity)

    @Delete
    suspend fun delete(workflow: WorkflowEntity)
}
