package qbos.ev3fx.test1.cmdinterface

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lejos.remote.ev3.RMIEV3
import lejos.remote.ev3.RMIMenu
import java.io.IOException
import java.io.Serializable
import java.rmi.Naming

@JvmInline
value class ConnectionOrError(private val state: Serializable) {
    fun get() = state
    override fun toString() = state.toString()
}

suspend fun connect(ip: String) = Pair(
    Pair(
        withContext(Dispatchers.IO) {
            Naming.lookup("//$ip/RemoteEV3")
        } as RMIEV3,
        withContext(Dispatchers.IO) {
            Naming.lookup("//$ip/RemoteMenu")
        } as RMIMenu
    ),
    ip
)

suspend fun connectCatching(ip: String) = ConnectionOrError(try {
    connect(ip)
} catch (e: IOException) {
    e
})

fun Iterable<ConnectionOrError>.handleConnectCatching(dfcon: Boolean):List<Pair<Pair<RMIEV3, RMIMenu>, String>> = map(ConnectionOrError::get).run {
    filterIsInstance<IOException>().run {
        forEach(IOException::printStackTrace)
        if (isNotEmpty()) {
            System.err.flush()
            println("${if (dfcon) "Ignoring " else ""}$size connection issue${if (size > 1) "s" else ""}.${if (!dfcon) " Aborting!" else ""}")
            if (!dfcon) kotlin.system.exitProcess(1)
        }
    }
    filterIsInstance<Pair<Pair<RMIEV3, RMIMenu>, String>>()
}