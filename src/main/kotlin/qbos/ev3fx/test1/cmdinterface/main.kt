package qbos.ev3fx.test1.cmdinterface

import com.example.MyApp
import tornadofx.launch

fun main(args: Array<String>) {
    if (args.isEmpty() || (args[1] != "--console") || (args[1] != "-c")) launch<MyApp>(args)
}