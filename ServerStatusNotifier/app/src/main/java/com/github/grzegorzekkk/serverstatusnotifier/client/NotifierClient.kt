package com.github.grzegorzekkk.serverstatusnotifier.client

import com.github.grzegorzekkk.serverstatusnotifier.serverconsole.model.ConsoleLine
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.ServerDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SsnJsonMessage
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.SocketFactory

class NotifierClient(address: InetAddress, port: Int) {
    var isAuthorized = false
        private set
    var isGettingConsoleUpdates = false

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
            clientId = AppSettings.uuid
            data = password
        }

        writeSsnMessage(ssnAuthRequest)

        val ssnResponse = SsnJsonMessage.fromJsonString(reader.readLine(), Unit::class.java)
        if (SsnJsonMessage.MessageType.AUTHORIZED_RESPONSE == ssnResponse.status) isAuthorized = true
    }

    fun fetchServerDetails(): ServerDetails? {
        if (isAuthorized) {
            val ssnDataRequest = SsnJsonMessage<Unit>().apply {
                status = SsnJsonMessage.MessageType.DATA_REQUEST
                clientId = AppSettings.uuid
            }

            writeSsnMessage(ssnDataRequest)

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
                clientId = AppSettings.uuid
            }

            writeSsnMessage(ssnConsoleRequest)

            isGettingConsoleUpdates = true
        }
    }

    fun fetchConsoleLines() : List<ConsoleLine> {
        val lines = mutableListOf<ConsoleLine>()

        if (isGettingConsoleUpdates) {
            while (isDataToReadAvailable()) {
                val ssnConsoleResponse = SsnJsonMessage.fromJsonString(reader.readLine(), String::class.java)
                lines.add(ConsoleLine(ssnConsoleResponse.data as String))
            }
        }
        return lines
    }

    fun sendConsoleCommand(command: String) {
        val ssnConsoleCommand = SsnJsonMessage<String>().apply {
            data = command
            status = SsnJsonMessage.MessageType.CONSOLE_COMMAND
            clientId = AppSettings.uuid
        }

        writeSsnMessage(ssnConsoleCommand)
    }

    fun isDataToReadAvailable(): Boolean {
        return reader.ready()
    }

    fun shutdown() {
        reader.close()
        writer.close()
        socket.close()
    }

    private fun writeSsnMessage(message: SsnJsonMessage<out Any>) {
        writer.apply {
            write(message.toJsonString())
            newLine()
            flush()
        }
    }

    companion object {
        const val TIMEOUT = 2000
    }
}