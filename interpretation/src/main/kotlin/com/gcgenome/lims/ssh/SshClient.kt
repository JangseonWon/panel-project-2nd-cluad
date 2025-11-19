package com.gcgenome.lims.ssh

import com.jcraft.jsch.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.OutputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.pathString

class SshClient(
    private val host: String,
    private val port: Int = 22,
    private val username: String,
    private val password: String,
    private val gid: Int,
    private val uid: Int
) {
    private fun session(): Session = JSch().getSession(username, host, port).apply {
        setConfig(Properties().apply {
            this["StrictHostKeyChecking"] = "no"
        })
        setPassword(password)
    }
    fun exec(command: String, output: OutputStream) = exec(command, output, output)
    fun exec(command: String, output: OutputStream, error: OutputStream) {
        val session = session()
        session.toClosable().use {
            session.connect()
            val channel = session.openChannel("exec") as ChannelExec
            channel.toClosable().use {
                channel.setCommand(command)
                channel.outputStream = output
                channel.setErrStream(error)
                channel.connect()
                while (channel.isConnected) Thread.sleep(1000)
            }
        }
    }
    fun exec(command: String): String {
        val stream = ByteArrayOutputStream()
        exec(command, stream)
        return String(stream.toByteArray(), Charsets.UTF_8)
    }
    fun send(dest: String, data: ByteArray) {
        val session = session()
        session.toClosable().use {
            session.connect()
            val channel = session.openChannel("sftp") as ChannelSftp
            val path = Path.of(dest)
            channel.toClosable().use {
                channel.connect()
                channel.mkdirs(path.parent)
                channel.put(ByteArrayInputStream(data), dest)
                channel.chown(uid, dest.replace("\\", "/"))
                channel.chgrp(gid, dest.replace("\\", "/"))
            }
        }
    }
    private fun Session.toClosable(): Closeable = Closeable { this.disconnect() }
    private fun Channel.toClosable(): Closeable = Closeable { this.disconnect() }
    private fun ChannelSftp.mkdirs(path: Path?) {
        if(path==null) throw RuntimeException()
        if (!exists(path)) {
            if (!exists(path.parent)) mkdirs(path.parent)
            mkdir(path.pathString.replace("\\", "/"))
            chown(uid, path.pathString.replace("\\", "/"))
            chgrp(gid, path.pathString.replace("\\", "/"))
        }
    }
    private fun ChannelSftp.exists(path: Path): Boolean = try {
        stat(path.pathString.replace("\\", "/"))!=null
    } catch(e: SftpException) {
        false
    }
}