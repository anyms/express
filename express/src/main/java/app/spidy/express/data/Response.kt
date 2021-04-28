package app.spidy.express.data

import app.spidy.express.StatusCode
import java.io.InputStream
import java.io.PrintStream
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

    fun stream(stream: InputStream, contentLength: Long, contentType: String) {
        addHeader("Content-Type", contentType)
        val resString = "HTTP/1.1 $statusCode ${StatusCode.codes[statusCode]}\r\nConnection: close\r\nServer: Express/1.0\r\n${headerString}Content-Length: ${contentLength}\r\n\r\n"
        val printWriter = PrintStream(client.getOutputStream())
        printWriter.write(resString.toByteArray())
        printWriter.flush()
        val buf = ByteArray(8192)
        var length: Int
        while (stream.read(buf).also { length = it } > 0) {
            printWriter.write(buf, 0, length)
            printWriter.flush()
        }
    }

//    fun streamTest(s: String) {
//        val resString = "HTTP/1.1 $statusCode ${StatusCode.codes[statusCode]}\r\nConnection: close\r\nServer: Express/1.0\r\n${headerString}Content-Length: ${s.length}\r\n\r\n$s"
//        val printWriter = PrintStream(client.getOutputStream())
//        printWriter.write(resString.toByteArray())
//        printWriter.flush()
//    }

    fun json(s: String) {
        addHeader("Content-Type", "application/json")
        send(s)
    }

    fun html(s: String) {
        addHeader("Content-Type", "text/html")
        send(s)
    }
}