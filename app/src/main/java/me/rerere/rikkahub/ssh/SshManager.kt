package me.rerere.rikkahub.ssh

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

data class RemoteFile(val name: String, val longName: String, val attrs: ChannelSftp.ATTRS)

class SshManager {
    private val jsch = JSch()
    private var session: Session? = null

    suspend fun connect(host: String, port: Int, username: String, password: String, privateKey: String = ""): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            if (privateKey.isNotBlank()) {
                jsch.addIdentity("key", privateKey.toByteArray(), null, null)
            }
            val s = jsch.getSession(username, host, port)
            s.setPassword(password)
            s.setConfig("StrictHostKeyChecking", "no")
            s.connect(15000)
            session = s
            "Connected"
        }
    }

    fun disconnect() {
        session?.disconnect()
        session = null
    }

    suspend fun execCommand(command: String): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        runCatching {
            val s = session ?: error("Not connected")
            val channel = s.openChannel("exec") as ChannelExec
            channel.setCommand(command)
            val out = ByteArrayOutputStream()
            val err = ByteArrayOutputStream()
            channel.setOutputStream(out)
            channel.setErrStream(err)
            channel.connect()
            while (!channel.isClosed) { Thread.sleep(200) }
            channel.disconnect()
            out.toString() to err.toString()
        }
    }

    suspend fun listFiles(path: String): Result<List<RemoteFile>> = withContext(Dispatchers.IO) {
        runCatching {
            val s = session ?: error("Not connected")
            val channel = s.openChannel("sftp") as ChannelSftp
            channel.connect()
            val entries = mutableListOf<RemoteFile>()
            @Suppress("UNCHECKED_CAST")
            val vec = channel.ls(path) as java.util.Vector<*>
            for (item in vec) {
                val entry = item as? ChannelSftp.LsEntry ?: continue
                entries.add(RemoteFile(entry.filename, entry.longname, entry.attrs))
            }
            channel.disconnect()
            entries
        }
    }
}
