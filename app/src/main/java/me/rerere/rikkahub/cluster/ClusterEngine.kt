package me.rerere.rikkahub.cluster

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.rerere.ai.provider.ProviderManager
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.model.Assistant

data class MemberAnswer(val role: String, val answer: String)
data class ClusterResult(val answers: List<MemberAnswer>, val verdict: String? = null)

class ClusterEngine(
    private val settingsStore: SettingsStore,
    private val providerManager: ProviderManager
) {
    suspend fun run(task: String, members: List<Assistant>, mode: String): ClusterResult {
        return when (mode) {
            "parallel" -> runParallel(task, members)
            "serial" -> runSerial(task, members)
            "vote" -> runVote(task, members)
            else -> runParallel(task, members)
        }
    }

    private suspend fun runParallel(task: String, members: List<Assistant>): ClusterResult = coroutineScope {
        val deferreds = members.map { member ->
            async {
                MemberAnswer(member.name, askModel(task, member))
            }
        }
        ClusterResult(deferreds.awaitAll())
    }

    private suspend fun runSerial(task: String, members: List<Assistant>): ClusterResult {
        val answers = mutableListOf<MemberAnswer>()
        var context = task
        for (member in members) {
            val answer = askModel(context, member)
            answers.add(MemberAnswer(member.name, answer))
            context = "$context\n\n${member.name} answered:\n$answer"
        }
        return ClusterResult(answers)
    }

    private suspend fun runVote(task: String, members: List<Assistant>): ClusterResult {
        val parallel = runParallel(task, members)
        val first = members.firstOrNull() ?: return parallel
        val reviewPrompt = "请评审以下回答并给出最终结论：\n" +
            parallel.answers.joinToString("\n---\n") { "${it.role}: ${it.answer}" }
        val verdict = askModel(reviewPrompt, first)
        return ClusterResult(parallel.answers, verdict)
    }

    private suspend fun askModel(prompt: String, assistant: Assistant): String {
        // Simplified: in real implementation, call providerManager with assistant's model
        return "[Simulated response from ${assistant.name}]"
    }
}
