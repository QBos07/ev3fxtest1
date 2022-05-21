package qbos.ev3fx.test1.cmdinterface

import lejos.remote.ev3.RMIEV3
import lejos.remote.ev3.RMIMenu

fun formatName(ev3: RMIEV3, menu: RMIMenu, ip: String): String {
    if (ip == ev3.name && ev3.name == menu.name) return ip
    if (ip != ev3.name && ev3.name == menu.name) return "$ip (${ev3.name} [E&M])"
    if (ip == ev3.name && ev3.name != menu.name) return "$ip (${menu.name} [M])"
    if (ip != ev3.name && ev3.name != menu.name && ip == menu.name) return "$ip (${ev3.name} [E])"
    if (ip != ev3.name && ev3.name != menu.name && ip != menu.name) return "$ip (${ev3.name} - ${menu.name} [E-M])"
    throw Exception("unreachable")
}

fun logfor(pair: Pair<Pair<RMIEV3, RMIMenu>, String>, message: String, prefixpostfix: String = "", formatter: (RMIEV3, RMIMenu, String) -> String = ::formatName, transform: String.() -> String = {this}) {
    print(message.split("\n").filter(String::isNotBlank)
        .joinToString("") { "${formatter(pair.first.first, pair.first.second, pair.second)}: $prefixpostfix$it\n" }.transform())
}