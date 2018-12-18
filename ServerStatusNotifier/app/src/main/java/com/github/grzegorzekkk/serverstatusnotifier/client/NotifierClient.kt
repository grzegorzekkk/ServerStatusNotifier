package com.github.grzegorzekkk.serverstatusnotifier.client

import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SsnJsonMessage
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import javax.net.SocketFactory

class NotifierClient(private val address: InetAddress, private val port: Int) {
    var isAuthorized = false
        private set
    var isGettingConsoleUpdates = false
    val clientIdentifier = UUID.randomUUID()!!

    private val socket: Socket
    private val writer: BufferedWriter
    private val reader: BufferedReader

    init {
        socket = SocketFactory.getDefault().createSocket()
        socket.connect(InetSocketAddress(address, port), TIMEOUT)
        socket.soTimeout = TIMEOUT
        writer = socket.getOutputStream().bufferedWriter()
        reader = socket.getInputStream().bufferedReader()
    }

    fun authorize(password: String) {
        val ssnAuthRequest = SsnJsonMessage<String>().apply {
            status = SsnJsonMessage.MessageType.AUTH_REQUEST
            clientId = clientIdentifier
            data = password
        }

        writer.apply {
            write(ssnAuthRequest.toJsonString())
            newLine()
            flush()
        }

        val ssnResponse = SsnJsonMessage.fromJsonString(reader.readLine(), Unit::class.java)
        if (SsnJsonMessage.MessageType.AUTHORIZED_RESPONSE == ssnResponse.status) isAuthorized = true
    }

    fun fetchServerDetails(): ServerDetails? {
        if (isAuthorized) {
            val ssnDataRequest = SsnJsonMessage<Unit>().apply {
                status = SsnJsonMessage.MessageType.DATA_REQUEST
                clientId = clientIdentifier
            }

            writer.apply {
                write(ssnDataRequest.toJsonString())
                newLine()
                flush()
            }

            val ssnDataResponse = SsnJsonMessage.fromJsonString(reader.readLine(), ServerDetails::class.java)
            return if (SsnJsonMessage.MessageType.DATA_RESPONSE == ssnDataResponse.status) {
                ssnDataResponse.data as ServerDetails
            } else {
                null
            }
        } else {
            return null
        }
    }

    fun openConsoleStream() {
        if (isAuthorized) {
            val ssnConsoleRequest = SsnJsonMessage<Unit>().apply {
                status = SsnJsonMessage.MessageType.CONSOLE_REQUEST
                clientId = clientIdentifier
            }

            writer.apply {
                write(ssnConsoleRequest.toJsonString())
                newLine()
                flush()
            }

            isGettingConsoleUpdates = true
        }
    }

    fun fetchConsoleLine() : String? {
        return if (isGettingConsoleUpdates) {
            val ssnConsoleResponse = SsnJsonMessage.fromJsonString(reader.readLine(), String::class.java)
            ssnConsoleResponse.data as String
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