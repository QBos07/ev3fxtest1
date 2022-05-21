package qbos.ev3fx.test1.cmdinterface

import lejos.remote.ev3.RMIEV3
import lejos.remote.ev3.RMIMenu
import java.io.File
import kotlin.math.abs

fun print(
    e: Pair<Pair<RMIEV3, RMIMenu>, String>,
    logger: (Pair<Pair<RMIEV3, RMIMenu>, String>, String) -> Unit,
    files: List<File>,
    xsize: Int,
    ysize: Int,
    zservo: Boolean,
    xgap: Int,
    ygap: Int,
    zrot: Int,
    xmotorport: String,
    ymotorport: String,
    zmotorport: String,
    xtype: Char,
    ytype: Char,
    ztype: Char,
    xspeed: Int,
    yspeed: Int,
    zspeed: Int,
    oldpath: Boolean
) {
    e.first.first.createRegulatedMotor(ymotorport, ytype).let { ym ->
        try {
            ym.speed = yspeed
            val yrot = ym.tachoCount
            e.first.first.createRegulatedMotor(xmotorport, xtype).let { xm ->
                try {
                    xm.speed = xspeed
                    val xrot = xm.tachoCount
                    e.first.first.createRegulatedMotor(zmotorport, ztype).let { zm ->
                        try {
                            zm.speed = zspeed
                            run {
                                /*files.forEach { f ->
                                    f.readBytes().forEach { byte ->
                                        byte.bits().forEach loop@{ bit ->
                                            if (bit) { zm.rotate(360); logger(e, "[R] $zmotorport<$zspeed>(360): $ztype") }
                                            if (yp == ysize) return@run
                                            xp++
                                            if (xp == xsize) {
                                                xp = 0
                                                yp++
                                                rev = !rev
                                                if (yp == ysize) return@loop
                                                ym.rotate(ygap)
                                                logger(e, "[M] $rev $yp\n[R] $ymotorport<$yspeed>($ygap): $ytype")
                                            }
                                            if (xp != 0) { xm.rotate(if (rev) -xgap else xgap)
                                                logger(e, "[M] $xp $yp\n[R] $xmotorport<$xspeed>(${if (rev) -xgap else xgap}): $xtype") }
                                        }
                                    }
                                }*/
                                if (!oldpath) {
                                    var xtime: (Int) -> Long
                                    run {
                                        xm.rotate(xgap)
                                        val time1 = System.currentTimeMillis()
                                        xm.rotate(xgap)
                                        val time2 = System.currentTimeMillis()
                                        xm.rotate(-xgap * 2)
                                        val time3 = System.currentTimeMillis()

                                        // mx+n (y1-y2)/x=m
                                        val m = ((time2 - time1) - (time3 - time2)) / (xgap - xgap * 2)
                                        val n = (time2 - time1) - m * xgap
                                        xtime = { abs(m * it + n) }
                                    }
                                    var ytime: (Int) -> Long
                                    run {
                                        ym.rotate(ygap)
                                        val time1 = System.currentTimeMillis()
                                        ym.rotate(ygap)
                                        val time2 = System.currentTimeMillis()
                                        ym.rotate(-ygap * 2)
                                        val time3 = System.currentTimeMillis()

                                        // mx+n (y1-y2)/x=m
                                        val m = ((time2 - time1) - (time3 - time2)) / (ygap - ygap * 2)
                                        val n = (time2 - time1) - m * ygap
                                        ytime = { abs(m * it + n) }
                                    }
                                    val sorted = mutableListOf<Pair<Int, Int>>()
                                    run {
                                        val flags = files.flatMap { it.readBytes().flatMap { b -> b.bits() } }.let { it.subList(0, ((xsize * ysize).let { s -> if (it.size < s) {
                                            it.size
                                        } else {
                                            s
                                        }
                                        })) }.mapIndexedNotNull { i, b -> if (b) Pair(i % xsize, i / xsize) else null }.toMutableList()/*.forEach {
                                            xm.rotateTo(xrot + it.first * xgap, true)
                                            ym.rotateTo(yrot + it.second * ygap)
                                            xm.waitComplete()
                                            zm.rotate(zrot)
                                            if (zservo) zm.rotate(-zrot)
                                            logger(e, "${it.first} | ${it.second}")
                                        }*/
                                        var xpos = 0
                                        var ypos = 0
                                        repeat(flags.size) {
                                            val nearesd = flags.minByOrNull { (x, y) -> xtime((xgap * x) - xpos).coerceAtLeast(ytime((ygap * y) - ypos)) }!!
                                            sorted += nearesd
                                            flags.remove(nearesd)
                                            xpos = nearesd.first * xgap
                                            ypos = nearesd.second * ygap
                                        }
                                    }
                                    sorted.forEach { (x, y) ->
                                        xm.rotateTo(xrot + x * xgap, true)
                                        ym.rotateTo(yrot + y * ygap)
                                        xm.waitComplete()
                                        zm.rotate(zrot)
                                        if (zservo) zm.rotate(-zrot)
                                        logger(e, "$x | $y")
                                    }
                                } else {
                                    files.flatMap { it.readBytes().flatMap { b -> b.bits() } }.let { it.subList(0, ((xsize * ysize).let { s -> if (it.size < s) {
                                        it.size
                                    } else {
                                        s
                                    }
                                    })) }.mapIndexedNotNull { i, b -> if (b) Pair(i % xsize, i / xsize) else null }.forEach {
                                        xm.rotateTo(xrot + it.first * xgap, true)
                                        ym.rotateTo(yrot + it.second * ygap)
                                        xm.waitComplete()
                                        zm.rotate(zrot)
                                        if (zservo) zm.rotate(-zrot)
                                        logger(e, "${it.first} | ${it.second}")
                                    }
                                }
                            }
                        }
                        finally {
                            try {
                                zm.flt(true)
                            }
                            finally {
                                zm.close()
                            }
                        }
                    }
                    ym.rotateTo(xrot, true)
                    xm.rotateTo(yrot)
                }
                finally {
                    try {
                        xm.flt(true)
                    }
                    finally {
                        xm.close()
                    }
                }
            }
            ym.waitComplete()
        }
        finally
        {
            try {
                ym.flt(true)
            }
            finally {
                ym.close()
            }
        }
    }
}
