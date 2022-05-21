@file:OptIn(ExperimentalCli::class)

package qbos.ev3fx.test1.cmdinterface

import kotlinx.cli.*
import kotlinx.coroutines.*
import lejos.remote.ev3.RMIEV3
import lejos.remote.ev3.RMIMenu
import qbos.ev3fx.test1.gui.MainApp
import tornadofx.launch
import java.io.File
import java.io.PrintStream

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
            args += file.readText().split(" ", "\n", "\r", "\t")
        }
        if (debugArgsFiles.isNotEmpty()) println(args.joinToString(" ", "args = \"", "\";"))
    }
    args = args.flatMap { it.split(",", ";", "=", "(", ")", "{", "}", "[", "]") }.filter { it != "" }.toMutableList()

    val praser = ArgParser("ev3fxtest1", strictSubcommandOptionsOrder = true)

    class Cmd(name: String, actionDescription: String) : Subcommand(name, actionDescription) {
        val debug by option(ArgType.Boolean).default(false)
        val `in` by option(ArgType.String, description = "replaces stdin")
        val out by option(ArgType.String, description = "replaces stdout")
        val err by option(ArgType.String, description = "replaces stderr")
        val dontmove by option(ArgType.Boolean, shortName = "dm", description = "don't move somthing").default(false)
        val dfcon by option(ArgType.Boolean, "dontfailconnect", "dfc", "don't fail on connect").default(false)
        val hosts by argument(ArgType.String, description = "Hostnames of the EV3s").vararg()
        val files by option(ArgFile, shortName = "f").multiple().required()
        val xmotorport by option(ArgType.String, "x-motor-port", "xp", "The Port of the motor for the x-axis").default("B")
        val ymotorport by option(ArgType.String, "y-motor-port", "yp", "The Port of the motor for the y-axis").default("C")
        val zmotorport by option(ArgType.String, "z-motor-port", "zp", "The Port of the motor for the z-axis").default("A")
        val xsize by option(ArgType.Int, "x-size", "xsi", "the number of x's").required()
        val ysize by option(ArgType.Int, "y-size", "ysi", "the number of y's").required()
        val zservo by option(ArgType.Boolean, "z-servo", "zs", "if the z motor is a servo").default(false)
        val xgap by option(ArgType.Int, "x-gap", "xg", "the rotaion of the x motor for the gap").required()
        val ygap by option(ArgType.Int, "y-gap", "yg", "the rotaion of the y motor for the gap").required()
        val zrot by option(ArgType.Int, "z-rotation", "zrot", "the rotation of the z motor needed to \"paint\"").default(360)
        val xtype by option(ArgType.Choice("NMLG".toList(), {if (it.length != 1) throw Exception() else it[0]}), "x-type", "xt", "the type of the motor for x").default('M')
        val ytype by option(ArgType.Choice("NMLG".toList(), {if (it.length != 1) throw Exception() else it[0]}), "y-type", "zt", "the type of the motor for y").default('L')
        val ztype by option(ArgType.Choice("NMLG".toList(), {if (it.length != 1) throw Exception() else it[0]}), "z-type", "yt", "the type of the motor for z").default('M')
        val xspeed by option(ArgType.Int, "x-speed", "xsp", "the rotation speed of motor x").default(10)
        val yspeed by option(ArgType.Int, "y-speed", "ysp", "the rotation speed of motor y").default(10)
        val zspeed by option(ArgType.Int, "z-speed", "zsp", "the rotation speed of motor z").default(10)
        val oldpath by option(ArgType.Boolean, "no-path-finding", "nopath", "don't use a pathfinding like method").default(false)
        override fun execute() {
            fun debugfor(pair: Pair<Pair<RMIEV3, RMIMenu>, String>, message: String) =
                if (debug) logfor(pair, message, "{DEBUG} ") else Unit

            if (!`in`.isNullOrBlank()) System.setIn(File(`in`).inputStream())
            if (!out.isNullOrBlank()) System.setOut(PrintStream(File(out).outputStream()))
            if (!err.isNullOrBlank()) System.setErr(PrintStream(File(err).outputStream()))

            var ev3s = hosts.map { ip ->
                runBlocking(CoroutineName("NetworkConnect")) {
                    async { connectCatching(ip) }
                }
            }.run {
                runBlocking { awaitAll() }
            }.handleConnectCatching(dfcon)
            @Suppress("UNCHECKED_CAST")
            ev3s = ev3s.mapNotNull {
                if (it.first.second.executingProgramName != null) {
                    logfor(it, "Programm \"${it.first.second.executingProgramName}\" is still running!")
                    while (true) {
                        logfor(it, "[A]bort, [I]gnore, [R]retry, [S]top, [G]o on ?: ")
                        when (Char(System.`in`.read()).lowercase()) {
                            "a" -> throw Exception(
                                "Programm \"${it.first.second.executingProgramName}\" is running on ${
                                    formatName(
                                        it.first.first,
                                        it.first.second,
                                        it.second
                                    )
                                }! Aborting"
                            )
                            "i" -> {
                                return@mapNotNull null
                            }
                            "s" -> {
                                it.first.second.stopProgram(); return@mapNotNull it
                            }
                            "g" -> return@mapNotNull it
                        }
                    }
                } else {
                    debugfor(it, "OK!\n")
                    return@mapNotNull it
                }
            } as List<Pair<Pair<RMIEV3, RMIMenu>, String>>
            runBlocking(CoroutineName("Printing")) { ev3s.map { async(Dispatchers.IO) {
                //it.first.second.suspend()
                print(
                    it,
                    ::debugfor,
                    files,
                    xsize,
                    ysize,
                    zservo,
                    xgap,
                    ygap,
                    zrot,
                    xmotorport,
                    ymotorport,
                    zmotorport,
                    xtype,
                    ytype,
                    ztype,
                    xspeed,
                    yspeed,
                    zspeed,
                    oldpath
                )
                //it.first.second.resume()
            } }.awaitAll() }
            kotlin.system.exitProcess(0)
        }
    }
    praser.subcommands(Cmd("cli", "commandline"))
    val gargs by praser.argument(ArgType.String, "arguments", "Argumente for the graphical application").vararg().optional()
    praser.parse(args.toTypedArray())
    launch<MainApp>(gargs.toTypedArray())
}


object ArgFile : ArgType<File>(true) {
    override val description get() = "{ Path as String }"
    override fun convert(value: kotlin.String, name: kotlin.String) = File(value)
}