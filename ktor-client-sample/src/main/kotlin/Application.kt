import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    val client = HttpClient(CIO)
    val file = File.createTempFile("files", "index")

    runBlocking {
        client.prepareGet("https://ktor.io/").execute { httpResponse ->
            val channel: ByteReadChannel = httpResponse.receive()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    file.appendBytes(bytes)
                    println("Received ${file.length()} bytes from ${httpResponse.contentLength()}")
                }
            }
            println("A file saved to ${file.path}")
        }
    }
}
