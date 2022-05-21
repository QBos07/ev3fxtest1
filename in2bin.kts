import java.io.File

var bits = File(args[0]).readLines().joinToString("").filter {
    when (it) {
        '1' -> true
        '0' -> true
        else -> false
    }
}
var normalized = bits.padEnd((Byte.SIZE_BITS - (bits.length % Byte.SIZE_BITS)) + bits.length, '0').chunked(Byte.SIZE_BITS).map { it.reversed() }
println(normalized.joinToString(" "))
var chars = normalized.map{ it.toUByte(2).toByte() }.toByteArray()
File(args[1]).writeBytes(chars)