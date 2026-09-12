package me.rerere.rikkahub.data.db.entity.workflow

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workflows")
data class WorkflowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val nodesJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis()
)
