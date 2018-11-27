package com.github.grzegorzekkk.serverstatusnotifier.client

import com.github.grzegorzekkk.serverstatusnotifier.client.model.SsnJsonMessage
import com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model.ServerDetails
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
        val ssnAuthRequest = SsnJsonMessage(SsnJsonMessage.MessageType.AUTH_REQUEST, password)
        writer.apply {
            write(ssnAuthRequest.toJsonString())
            newLine()
            flush()
        }

        val ssnResponse = SsnJsonMessage.fromJsonString(reader.readText(), ServerDetails::class.java)
        return if (SsnJsonMessage.MessageType.DATA_RESPONSE == ssnResponse.status) {
            ssnResponse.data
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