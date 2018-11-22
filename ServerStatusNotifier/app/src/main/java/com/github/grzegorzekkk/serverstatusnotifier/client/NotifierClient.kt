package com.github.grzegorzekkk.serverstatusnotifier.client

import com.github.grzegorzekkk.serverstatusnotifier.client.model.SsnJsonMessage
import com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model.ServerDetails
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.SocketFactory

class NotifierClient(private val address: InetAddress, private val port: Int) {
    private val socket: Socket
    private val writer: PrintWriter
    private val reader: BufferedReader

    init {
        socket = SocketFactory.getDefault().createSocket()
        socket.connect(InetSocketAddress(address, port), 2000)
        socket.soTimeout = 2000
        writer = PrintWriter(socket.getOutputStream(), true)
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    fun fetchServerDetails(password: String): ServerDetails? {
        val ssnAuthRequest = SsnJsonMessage(SsnJsonMessage.MessageType.AUTH_REQUEST, password)
        writer.println(ssnAuthRequest.toJsonString())

        val ssnResponse = SsnJsonMessage.fromJsonString(reader.readLine(), ServerDetails::class.java)
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
}