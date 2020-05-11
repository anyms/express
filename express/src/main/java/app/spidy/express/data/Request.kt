package app.spidy.express.data

import java.lang.StringBuilder

data class Request(
    val headers: Headers = Headers(),
    var protocol: String = "HTTP/1.1",
    var path: String = "/",
    var method: String = "GET",
    val form: HashMap<String, String> = hashMapOf()
)