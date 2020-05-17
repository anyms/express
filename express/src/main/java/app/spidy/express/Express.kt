package app.spidy.express

import app.spidy.express.data.Request
import app.spidy.express.data.Response
import app.spidy.express.data.Route
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import kotlin.concurrent.thread

class Express(
    private val port: Int = 5000,
    private val host: String = "127.0.0.1",
    private val debug: Boolean = false
) {
    private var serverSocket: ServerSocket? = null

    init {
        serverSocket = ServerSocket(port, 50, InetAddress.getByName(host))
        serverSocket!!.reuseAddress = true
    }


    private val routes = ArrayList<Route>()
    private var keepRunning = false

    fun get(path: String, callback: (req: Request, res: Response) -> Unit) {
        var p = path
        if (p.endsWith("/")) p = p.dropLast(1)
        routes.add(Route(path = p, callback = callback, method = "GET"))
    }

    fun post(path: String, callback: (req: Request, res: Response) -> Unit) {
        var p = path
        if (p.endsWith("/")) p = p.dropLast(1)
        routes.add(Route(path = p, callback = callback, method = "POST"))
    }

    fun terminate() {
        serverSocket?.close()
        routes.clear()
        keepRunning = false
    }

    private fun handleRequest(client: Socket) {
        val inputStreamReader = InputStreamReader(client.getInputStream())
        val bufferedReader = BufferedReader(inputStreamReader)

        val request = Request()
        val response = Response(client)

        var line = bufferedReader.readLine()

        while (line != "") {
            if (line == null) break
            if (line.contains("HTTP/1.")) {
                val nodes = line.split(" ")
                request.method = nodes[0]
                request.path = nodes[1]
                request.protocol = nodes[2]
            } else {
                val nodes = line.split(": ").toMutableList()
                request.headers.add(nodes.removeAt(0), nodes.joinToString(": "))
            }
            line = bufferedReader.readLine()
        }
        val payload = StringBuilder()
        while (bufferedReader.ready()) {
            payload.append(bufferedReader.read().toChar())
        }

        if (payload.trim() != "") {
            val nodes = payload.split("&")
            for (node in nodes) {
                val prop = node.split("=")
                request.form[prop[0]] = prop[1]
            }
        }

        var isRouteSuccess = false
        for (route in routes) {
            if (request.path.endsWith("/")) request.path = request.path.dropLast(1)
            if (request.path == route.path) {
                if (request.method == route.method) {
                    route.callback.invoke(request, response)
                } else {
                    response.statusCode = 405
                    response.send("<h1>405 ${StatusCode.codes[405]}</h1>")
                }
                isRouteSuccess = true
            }
        }

        if (!isRouteSuccess) {
            response.statusCode = 404
            response.send("<h1>404 Not Found</h1>")
        }

        inputStreamReader.close()
        bufferedReader.close()
        client.close()
    }

    fun run() {
        keepRunning = true

        while (keepRunning) {
            try {
                val client = serverSocket!!.accept()
                handleRequest(client)
            } catch (e: SocketException) {
                break
            }
        }
        serverSocket?.close()
    }

    fun runAsync() {
        thread {
            run()
        }
    }
}
