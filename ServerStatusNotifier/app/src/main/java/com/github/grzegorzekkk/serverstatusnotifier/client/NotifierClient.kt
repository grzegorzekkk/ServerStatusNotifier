package com.github.grzegorzekkk.serverstatusnotifier.client

import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SsnJsonMessage
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.SocketFactory

class NotifierClient(private val address: InetAddress, private val port: Int) {
    private val socket: Socket
    private val writer: BufferedWriter
    private val reader: InputStreamReader

    init {
        socket = SocketFactory.getDefault().createSocket()
        socket.connect(InetSocketAddress(address, port), TIMEOUT)
        socket.soTimeout = TIMEOUT
        writer = socket.getOutputStream().bufferedWriter()
        reader = socket.getInputStream().reader()
    }

    fun fetchServerDetails(password: String): ServerDetails? {
        val ssnAuthRequest = SsnJsonMessage<String>().apply {
            status = SsnJsonMessage.MessageType.AUTH_REQUEST
            data = password
        }

        writer.apply {
            write(ssnAuthRequest.toJsonString())
            newLine()
            flush()
        }

        val ssnResponse = SsnJsonMessage.fromJsonString(reader.readText(), ServerDetails::class.java)
        return if (SsnJsonMessage.MessageType.DATA_RESPONSE == ssnResponse.status) {
            ssnResponse.data as ServerDetails
        } else {
            null
        }
    }

    fun shutdown() {
        reader.close()
        writer.close()
        socket.close()
    }

    companion object {
        const val TIMEOUT = 2000
    }
}