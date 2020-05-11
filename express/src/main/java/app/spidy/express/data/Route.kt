package app.spidy.express.data

data class Route(
    val path: String,
    val callback: (Request, Response) -> Unit,
    val method: String
)