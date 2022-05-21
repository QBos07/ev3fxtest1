package fake.lejos.remote.ev3

import lejos.remote.ev3.RMIMenu

class FakeRMIMenu(private val reportedName: String) : RMIMenu {

    override fun toString(): String {
        return "${this::class.simpleName}(reportedName = $reportedName)"
    }

    override fun runProgram(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun debugProgram(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun runSample(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun stopProgram() {
        TODO("Not yet implemented")
    }

    override fun deleteFile(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFileSize(p0: String?): Long {
        TODO("Not yet implemented")
    }

    override fun getProgramNames(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun getSampleNames(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun uploadFile(p0: String?, p1: ByteArray?): Boolean {
        TODO("Not yet implemented")
    }

    override fun fetchFile(p0: String?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getSetting(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun setSetting(p0: String?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteAllPrograms() {
        TODO("Not yet implemented")
    }

    override fun getVersion(): String {
        TODO("Not yet implemented")
    }

    override fun getMenuVersion(): String {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        return reportedName
    }

    override fun setName(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun configureWifi(p0: String?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun getExecutingProgramName(): String {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun suspend() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }
}