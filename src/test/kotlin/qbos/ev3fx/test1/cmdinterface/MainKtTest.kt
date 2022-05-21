package qbos.ev3fx.test1.cmdinterface

import fake.lejos.remote.ev3.FakeRMIEV3
import fake.lejos.remote.ev3.FakeRMIMenu
import lejos.remote.ev3.RMIEV3
import lejos.remote.ev3.RMIMenu
import org.testng.annotations.Test

import org.testng.Assert.*
import org.testng.annotations.DataProvider
import java.io.ByteArrayOutputStream
import java.io.PrintStream

import org.mockito.Mockito.*

class MainKtTest {

    @DataProvider(name = "formatName")
    fun dataFormatName(): Array<Array<Any>> {
        /*
        E,M,I "Exp"
        1,1,1 "1"
        2,1,1 "1 (2 [E])"
        1,2,1 "1 (2 [M])"
        2,2,1 "1 (2 [E&M])"
        2,3,1 "1 (2 - 3 [E-M])"
        */
        return arrayOf(
            arrayOf(
                FakeRMIEV3("1"),
                FakeRMIMenu("1"),
                "1",
                "1"
            ),
            arrayOf(
                FakeRMIEV3("2"),
                FakeRMIMenu("1"),
                "1",
                "1 (2 [E])"
            ),
            arrayOf(
                FakeRMIEV3("1"),
                FakeRMIMenu("2"),
                "1",
                "1 (2 [M])"
            ),
            arrayOf(
                FakeRMIEV3("2"),
                FakeRMIMenu("2"),
                "1",
                "1 (2 [E&M])"
            ),
            arrayOf(
                FakeRMIEV3("2"),
                FakeRMIMenu("3"),
                "1",
                "1 (2 - 3 [E-M])"
            )
        )
    }

    @Test(groups = ["logger", "multi"], dataProvider = "formatName")
    fun testFormatName(ev3: RMIEV3, menu: RMIMenu, ip: String, expected: String) {
        assertEquals(formatName(ev3, menu, ip), expected)
    }

    @Test(groups = ["logger", "stdout"])
    fun testLogfor() {
        val oldout = System.out
        val newout = ByteArrayOutputStream()
        System.setOut(PrintStream(newout))
        logfor(Pair(Pair(FakeRMIEV3("1"), FakeRMIMenu("2")), "3"), "4\n5\n", "6 ", { e, m, i ->
            assertEquals(e.name, "1")
            assertEquals(m.name, "2")
            assertEquals(i, "3")
            "7"}) {
            assertEquals(this, "7: 6 4\n7: 6 5\n")
            "8"
        }
        System.setOut(oldout)
        assertEquals(newout.toString(), "8")
    }
}