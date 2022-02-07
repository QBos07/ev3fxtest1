@file:OptIn(ExperimentalCli::class)

package qbos.ev3fx.test1.cmdinterface

import com.example.MyApp
import kotlinx.cli.*
import lejos.hardware.Brick
import lejos.remote.ev3.RMIMenu
import lejos.remote.ev3.RMIEV3
import tornadofx.launch
import tornadofx.runAsync
import tornadofx.urlEncoded
import java.io.File
import java.io.IOException
import java.rmi.ConnectIOException
import java.rmi.Naming
import java.util.*

fun formatName(ev3: RMIEV3, menu: RMIMenu, ip: String): String {
    if (ip == ev3.name && ev3.name == menu.name) return ip
    if (ip != ev3.name && ev3.name == menu.name) return "$ip (${ev3.name} [E,M])"
    if (ip == ev3.name && ev3.name != menu.name) return "$ip (${menu.name} [M])"
    if (ip != ev3.name && ev3.name != menu.name && ip == menu.name) return "$ip (${ev3.name} [E])"
    if (ip != ev3.name && ev3.name != menu.name && ip != menu.name) return "$ip (${ev3.name} - ${menu.name})"
    throw Exception("unreachable")
}

fun logfor(pair: Pair<Pair<RMIEV3, RMIMenu>, String>, message: String) =
    print("${formatName(pair.first.first, pair.first.second, pair.second)}: $message")

fun main(args: Array<String>) {
    @Suppress("NAME_SHADOWING")
    var args = args.toMutableList()
    run {
        val debugArgsFiles: MutableList<String> = mutableListOf()
        var skip = 0u
        args.forEachIndexed { argc, argv ->
            if (skip > 0u) {
                skip--
                return@forEachIndexed
            }
            when (argv) {
                "-dAf", "--debugArgsFile" -> {
                    if (args.size - 1 < argc + 1) throw IndexOutOfBoundsException("nicht genügent angaben für \"-dAf\", bzw. \"--debugArgsFile\" (${args.size - 1} < ${argc + 1})")
                    debugArgsFiles += args[argc + 1]
                    skip++
                }
            }
        }
        run {
            var found = false
            args = args.filter {
                if (found) {
                    found = false
                    return@filter false
                }
                when (it) {
                    "-dAf", "--debugArgsFile" -> {
                        found = true; false
                    }
                    else -> true
                }
            }.toMutableList()
        }
        debugArgsFiles.forEach { fileName ->
            val file = File(fileName)
            println("${file.absolutePath}: F: ${file.isFile} R: ${file.canRead()}")
            if (!file.canRead()) return@forEach
            args += file.readText().split(" ", "\n", "\r")
        }
        if (debugArgsFiles.isNotEmpty()) println(args.joinToString(" ", "args = \"", "\";"))
    }
    run {
        val newargs = mutableListOf<String>()
        args.forEach { newargs += it.split(",", ";", "=", "(", ")", "{", "}", "[", "]") }
        args = newargs
    }
    args = args.filter { it != "" }.toMutableList()

    val praser = ArgParser("ev3fxtest1", strictSubcommandOptionsOrder = true)

    class Cmd(name: String, actionDescription: String) : Subcommand(name, actionDescription) {
        val debug by option(ArgType.Boolean).default(false)
        val dontmove by option(ArgType.Boolean, shortName = "dm", description = "don't move somthing").default(false)
        val dfcon by option(ArgType.Boolean, "dontfailconnect", "dfc", "don't fail on connect").default(false)
        val hosts by argument(ArgType.String, description = "Hostnames of the EV3s").vararg()
        val files by option(ArgFile, shortName = "f").multiple()
        override fun execute() {
            fun debugfor(pair: Pair<Pair<RMIEV3, RMIMenu>, String>, message: String) =
                if (debug) logfor(pair, "DEBUG $message") else Unit

            var ev3s = hosts.map { ip ->
                try {
                    Pair(
                        Pair(Naming.lookup("//$ip/RemoteEV3") as RMIEV3, Naming.lookup("//$ip/RemoteMenu") as RMIMenu),
                        ip
                    )
                } catch (e: IOException) {
                    e
                }
            }.run {
                filterIsInstance<IOException>().run {
                    forEach { e -> e.printStackTrace() }
                    if (isNotEmpty()) {
                        println("${if (dfcon) "Ignoring " else ""}$size connection issue${if (size > 1) "s" else ""}.${if (!dfcon) " Aborting!" else ""}")
                        if (!dfcon) kotlin.system.exitProcess(1)
                    }
                }
                filterIsInstance<Pair<Pair<RMIEV3, RMIMenu>, String>>()
            }/*.run { Pair(                         // remap the outer list to multiple inner lists
                    Pair(
                        map { it.first.first },
                        map { it.first.second }
                    ),
                    map { it.second }
                ) }*/
            var ev3ns = ev3s.mapNotNull {
                if (it.first.second.executingProgramName != null) {
                    do {
                        logfor(it, "Programm \"${it.first.second.executingProgramName}\" is still running!")
                        logfor(it, "[A]bort, [I]gnore, [R]retry, [S]top, [G]o on ?: ")
                        when (Scanner(System.`in`).next(".")) {
                            "a" -> throw Exception("Programm \"${it.first.second.executingProgramName}\" is running! Aborting")
                            "i" -> {
                                return@mapNotNull null
                            }
                            "s" -> {
                                it.first.second.stopProgram(); return@mapNotNull it
                            }
                            "g" -> return@mapNotNull it
                        }
                    } while (true)
                } else {
                    debugfor(it, "OK!\n")
                    return@mapNotNull it
                }
            }
            ev3s.mapNotNull {
                if (it.first.second.executingProgramName != null) {
                    while (true) {
                        return@mapNotNull null
                    }
                } else {
                    debugfor(it, "hi\n")
                    return@mapNotNull it
                }
            }.forEach { println(it::class) }
            ev3s.forEach { it.first.second.suspend() }
            ev3s.forEach {
                logfor(
                    it,
                    "syss3 wfap\n"
                ); it.first.first.audio.systemSound(3); it.first.first.keys.waitForAnyPress()
            }
            ev3s.forEach { it.first.second.resume() }
            kotlin.system.exitProcess(0)
        }
    }
    praser.subcommands(Cmd("cli", "commandline"))
    val gargs by praser.argument(ArgType.String, "arguments", "Argumente for the graphical application").vararg()
        .optional()
    praser.parse(args.toTypedArray())
    launch<MyApp>(gargs.toTypedArray())

    /*printAll(
        address.let { a ->
            val bricks = mutableListOf<Brick>()
            a.forEach { bricks += RemoteEV3(it) }
            bricks
        },
        files.let { f ->
            val realfiles = mutableListOf<File>()
            f.forEach { realfiles += File(it) }
            realfiles
        },
        rows, dryrun)*/
    //printAll(address,files,rows,dryrun)


}

fun printAll(bricksS: List<String>, filesS: List<String>, rows: UInt, dryrun: Boolean = false) {
    val bricks = bricksS.forEach { b ->
        try {
        } catch (e: ConnectIOException) {
        }
    }
}

fun printOne(brick: Brick, data: UByteArray, rows: UInt, dryrun: Boolean = false) = runAsync {

}

object ArgFile : ArgType<File>(true) {
    override val description get() = "Path"
    override fun convert(value: kotlin.String, name: kotlin.String) = File(value)
}

