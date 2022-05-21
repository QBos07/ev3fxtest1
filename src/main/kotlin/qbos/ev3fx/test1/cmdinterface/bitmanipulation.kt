package qbos.ev3fx.test1.cmdinterface

fun Byte.bits() = (0 until Byte.SIZE_BITS).map { this.toInt() and (1 shl it) != 0 }