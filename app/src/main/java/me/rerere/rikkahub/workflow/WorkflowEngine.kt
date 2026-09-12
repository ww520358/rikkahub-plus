package me.rerere.rikkahub.workflow

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.rerere.rikkahub.data.db.AppDatabase
import me.rerere.rikkahub.data.db.entity.workflow.WorkflowEntity

data class WorkflowNode(
    val id: String,
    val type: String, // ai, server, condition
    val roleId: String? = null,
    val prompt: String? = null,
    val serverId: Int? = null,
    val command: String? = null,
    val expression: String? = null
)

data class NodeRunResult(val output: String, val success: Boolean)

class WorkflowEngine(
    private val database: AppDatabase,
    private val sshManager: me.rerere.rikkahub.ssh.SshManager
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun run(entity: WorkflowEntity, input: String): List<NodeRunResult> {
        val nodes = json.decodeFromString<List<WorkflowNode>>(entity.nodesJson)
        val results = mutableMapOf<String, NodeRunResult>()
        var currentInput = input

        for (node in nodes) {
            val resolved = resolveTemplate(node.prompt ?: "", currentInput, results)
            val result = when (node.type) {
                "ai" -> NodeRunResult("[AI result for: $resolved]", true)
                "server" -> {
                    val server = node.serverId?.let { database.serverDao().getById(it) }
                    if (server != null) {
                        sshManager.connect(server.host, server.port, server.username, server.password, server.privateKey)
                        val (out, err) = sshManager.execCommand(node.command ?: "echo 'no command'")
                            .getOrDefault("" to "")
                        sshManager.disconnect()
                        NodeRunResult(out + err, err.isBlank())
                    } else {
                        NodeRunResult("Server not found", false)
                    }
                }
                "condition" -> {
                    val expr = node.expression ?: ""
                    val passes = when {
                        expr.startsWith("contains:") -> resolved.contains(expr.removePrefix("contains:"))
                        expr.startsWith("regexp:") -> Regex(expr.removePrefix("regexp:")).containsMatchIn(resolved)
                        else -> resolved.isNotBlank()
                    }
                    NodeRunResult(passes.toString(), passes)
                }
                else -> NodeRunResult("Unknown type", false)
            }
            results[node.id] = result
            currentInput = result.output
        }
        return nodes.map { results[it.id] ?: NodeRunResult("", false) }
    }

    private fun resolveTemplate(template: String, input: String, results: Map<String, NodeRunResult>): String {
        return template
            .replace("{input}", input)
            .replace(Regex("\\{output:([^}]+)\\}")) { match ->
                results[match.groupValues[1]]?.output ?: ""
            }
    }
}
