import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun main(args: Array<String>) {
    val server = HttpServer.create(InetSocketAddress(8000), 0)
    server.createContext("/", MyHandler())
    server.executor = null // creates a default executor
    server.start()
    println("Server listing on port 8000")
}

class MyHandler : HttpHandler {
    override fun handle(httpExchange: HttpExchange) {
        val city = getQueryByName(httpExchange.requestURI.query, "city")
        val degree = getDegreeByCity(city)
        val payload = "$degree Degree in $city"
        httpExchange.responseHeaders.add("Content-Type","text/html")
        httpExchange.sendResponseHeaders(200, payload.length.toLong())
        val os = httpExchange.responseBody
        os.write(payload.toByteArray())
        os.close()
    }

    private fun getDegreeByCity(city: String?): Int{
        // TODO Get degree from DB
        return 20
    }

    private fun getQueryByName(query: String, queryName: String): String? {
        return query.split('&').map {
            val parts = it.split('=')
            val name = parts.firstOrNull() ?: ""
            val value = parts.drop(1).firstOrNull() ?: ""
            Pair(name, value)
        }.firstOrNull{it.first == queryName}?.second
    }
}