package me.rerere.rikkahub.data.db.entity.cluster

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cluster_definitions")
data class ClusterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val mode: String = "parallel",
    val memberJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis()
)
