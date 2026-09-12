package me.rerere.rikkahub.plugin

import android.content.Context
import dalvik.system.DexClassLoader
import kotlinx.coroutines.flow.first
import me.rerere.rikkahub.data.db.AppDatabase
import me.rerere.rikkahub.data.db.entity.plugin.PluginEntity

interface AppPlugin {
    val name: String
    val version: String
    fun onInit(context: Context)
    fun toolDeclarations(): List<PluginTool> = emptyList()
}

data class PluginTool(val name: String, val description: String, val execute: suspend (String) -> String)

class PluginManager(private val context: Context, private val database: AppDatabase) {
    private val builtins = mutableListOf<AppPlugin>()
    private val loaded = mutableMapOf<String, AppPlugin>()

    init {
        ensureBuiltins()
    }

    private fun ensureBuiltins() {
        // Built-in plugins will be registered by the hosting app if needed
    }

    fun toolDeclarations(): List<PluginTool> {
        return builtins.flatMap { it.toolDeclarations() } +
               loaded.values.flatMap { it.toolDeclarations() }
    }

    suspend fun execute(name: String, args: String): String? {
        val tool = toolDeclarations().find { it.name == name } ?: return null
        return tool.execute(args)
    }

    suspend fun installApkPlugin(path: String, className: String, name: String, version: String): Result<Unit> = runCatching {
        val dir = context.cacheDir.resolve("plugins").apply { mkdirs() }
        val dest = dir.resolve("${className}_${System.currentTimeMillis()}.apk")
        java.io.File(path).copyTo(dest, true)
        val loader = DexClassLoader(dest.absolutePath, dir.resolve("opt").absolutePath, null, context.classLoader)
        val clazz = loader.loadClass(className)
        val plugin = clazz.getDeclaredConstructor().newInstance() as AppPlugin
        plugin.onInit(context)
        loaded[className] = plugin
        database.pluginDao().insert(PluginEntity(
            className = className,
            name = name,
            version = version,
            enabled = true,
            apkPath = dest.absolutePath,
            isBuiltin = false
        ))
    }
}
