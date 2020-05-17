package app.spidy.express.data

import app.spidy.express.StatusCode
import java.io.PrintWriter
import java.net.Socket

class Response(private val client: Socket) {
    var statusCode = 200

    private var headerString = ""

    fun addHeader(k: String, v: String) {
        headerString += "${k}: $v\r\n"
    }

    fun send(s: String) {
        val resString = "HTTP/1.1 $statusCode ${StatusCode.codes[statusCode]}\r\nConnection: close\r\nServer: Express/1.0\r\n${headerString}Content-Length: ${s.length}\r\n\r\n$s"
        val printWriter = PrintWriter(client.getOutputStream())
        printWriter.print(resString)
        printWriter.flush()
    }

    fun json(s: String) {
        addHeader("Content-Type", "application/json")
        send(s)
    }

    fun html(s: String) {
        addHeader("Content-Type", "text/html")
        send(s)
    }
}