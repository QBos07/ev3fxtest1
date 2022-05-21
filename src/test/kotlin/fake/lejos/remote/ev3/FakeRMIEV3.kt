package fake.lejos.remote.ev3

import lejos.hardware.lcd.Font
import lejos.remote.ev3.*

class FakeRMIEV3(private val reportedName: String): RMIEV3 {

    override fun toString(): String {
        return "${this::class.simpleName}(reportedName = $reportedName)"
    }

    override fun openAnalogPort(p0: String?): RMIAnalogPort {
        TODO("Not yet implemented")
    }

    override fun openI2CPort(p0: String?): RMII2CPort {
        TODO("Not yet implemented")
    }

    override fun getBattery(): RMIBattery {
        TODO("Not yet implemented")
    }

    override fun openUARTPort(p0: String?): RMIUARTPort {
        TODO("Not yet implemented")
    }

    override fun openMotorPort(p0: String?): RMIMotorPort {
        TODO("Not yet implemented")
    }

    override fun createSampleProvider(p0: String?, p1: String?, p2: String?): RMISampleProvider {
        TODO("Not yet implemented")
    }

    override fun createRegulatedMotor(p0: String?, p1: Char): RMIRegulatedMotor {
        TODO("Not yet implemented")
    }

    override fun getAudio(): RMIAudio {
        TODO("Not yet implemented")
    }

    override fun getTextLCD(): RMITextLCD {
        TODO("Not yet implemented")
    }

    override fun getTextLCD(p0: Font?): RMITextLCD {
        TODO("Not yet implemented")
    }

    override fun getGraphicsLCD(): RMIGraphicsLCD {
        TODO("Not yet implemented")
    }

    override fun getWifi(): RMIWifi {
        TODO("Not yet implemented")
    }

    override fun getBluetooth(): RMIBluetooth {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        return reportedName
    }

    override fun getKey(p0: String?): RMIKey {
        TODO("Not yet implemented")
    }

    override fun getLED(): RMILED {
        TODO("Not yet implemented")
    }

    override fun getKeys(): RMIKeys {
        TODO("Not yet implemented")
    }

}