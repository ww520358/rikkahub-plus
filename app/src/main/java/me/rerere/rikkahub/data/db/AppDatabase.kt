package me.rerere.rikkahub.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import me.rerere.ai.core.TokenUsage
import me.rerere.rikkahub.data.db.dao.ConversationDAO
import me.rerere.rikkahub.data.db.dao.FavoriteDAO
import me.rerere.rikkahub.data.db.dao.FolderDAO
import me.rerere.rikkahub.data.db.dao.GenMediaDAO
import me.rerere.rikkahub.data.db.dao.ManagedFileDAO
import me.rerere.rikkahub.data.db.dao.MemoryDAO
import me.rerere.rikkahub.data.db.dao.MessageNodeDAO
import me.rerere.rikkahub.data.db.dao.WorkspaceDAO
import me.rerere.rikkahub.data.db.entity.ConversationEntity
import me.rerere.rikkahub.data.db.entity.FavoriteEntity
import me.rerere.rikkahub.data.db.entity.FolderEntity
import me.rerere.rikkahub.data.db.entity.GenMediaEntity
import me.rerere.rikkahub.data.db.entity.ManagedFileEntity
import me.rerere.rikkahub.data.db.entity.MemoryEntity
import me.rerere.rikkahub.data.db.entity.MessageNodeEntity
import me.rerere.rikkahub.data.db.entity.WorkspaceEntity
import me.rerere.rikkahub.data.db.dao.browser.BookmarkDao
import me.rerere.rikkahub.data.db.dao.browser.BrowserHistoryDao
import me.rerere.rikkahub.data.db.dao.cluster.ClusterDao
import me.rerere.rikkahub.data.db.dao.plugin.PluginDao
import me.rerere.rikkahub.data.db.dao.server.ServerDao
import me.rerere.rikkahub.data.db.dao.workflow.WorkflowDao
import me.rerere.rikkahub.data.db.entity.browser.BookmarkEntity
import me.rerere.rikkahub.data.db.entity.browser.BrowserHistoryEntity
import me.rerere.rikkahub.data.db.entity.cluster.ClusterEntity
import me.rerere.rikkahub.data.db.entity.plugin.PluginEntity
import me.rerere.rikkahub.data.db.entity.server.ServerEntity
import me.rerere.rikkahub.data.db.entity.workflow.WorkflowEntity
import me.rerere.rikkahub.data.db.migrations.Migration_16_17
import me.rerere.rikkahub.data.db.migrations.Migration_22_23
import me.rerere.rikkahub.data.db.migrations.Migration_25_26
import me.rerere.rikkahub.data.db.migrations.Migration_8_9
import me.rerere.rikkahub.utils.JsonInstant

@Database(
    entities = [
        ConversationEntity::class,
        MemoryEntity::class,
        GenMediaEntity::class,
        MessageNodeEntity::class,
        ManagedFileEntity::class,
        FavoriteEntity::class,
        WorkspaceEntity::class,
        FolderEntity::class,
        BookmarkEntity::class,
        BrowserHistoryEntity::class,
        ServerEntity::class,
        ClusterEntity::class,
        WorkflowEntity::class,
        PluginEntity::class,
    ],
    version = 26,
    autoMigrations = [
        AutoMigration(from = 25, to = 26, spec = Migration_25_26::class),
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9, spec = Migration_8_9::class),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 16, to = 17, spec = Migration_16_17::class),
        AutoMigration(from = 17, to = 18),
        AutoMigration(from = 18, to = 19),
        AutoMigration(from = 19, to = 20),
        AutoMigration(from = 20, to = 21),
        AutoMigration(from = 21, to = 22),
        AutoMigration(from = 22, to = 23, spec = Migration_22_23::class),
        AutoMigration(from = 23, to = 24),
        AutoMigration(from = 24, to = 25),
    ]
)
@TypeConverters(TokenUsageConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDAO

    abstract fun memoryDao(): MemoryDAO

    abstract fun genMediaDao(): GenMediaDAO

    abstract fun messageNodeDao(): MessageNodeDAO

    abstract fun managedFileDao(): ManagedFileDAO

    abstract fun favoriteDao(): FavoriteDAO

    abstract fun workspaceDao(): WorkspaceDAO

    abstract fun folderDao(): FolderDAO

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun browserHistoryDao(): BrowserHistoryDao

    abstract fun serverDao(): ServerDao

    abstract fun clusterDao(): ClusterDao

    abstract fun workflowDao(): WorkflowDao

    abstract fun pluginDao(): PluginDao
}

object TokenUsageConverter {
    @TypeConverter
    fun fromTokenUsage(usage: TokenUsage?): String {
        return JsonInstant.encodeToString(usage)
    }

    @TypeConverter
    fun toTokenUsage(usage: String): TokenUsage? {
        return JsonInstant.decodeFromString(usage)
    }
}
