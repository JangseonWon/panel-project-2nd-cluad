package com.gcgenome.test

import com.gcgenome.lims.ssh.SshClient
import org.junit.jupiter.api.DisplayName

class SshClientTest {
    // @Test
    @DisplayName("Dion 서버 접속 테스트")
    internal fun test1() {
        val client = SshClient("172.19.206.4", 22, "root", "GCGenome12!", 1000, 1000)
        println(client.exec("java -version"))
    }
    // @Test
    @DisplayName("Dion 서버 파일 전송 테스트")
    internal fun test2() {
        val client = SshClient("172.19.206.4", 22, "root", "GCGenome12!", 1000, 1000)
        client.send("/storm/LIMS/hello.txt", "Hello".toByteArray(Charsets.UTF_8))
    }
}