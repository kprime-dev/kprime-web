package it.unibz.krdb.kprime.domain.setting

import it.unibz.krdb.kprime.domain.trace.TraceName
import java.io.File

class SettingService(val settingRepo: SettingRepository) {

    //private val settingRepo = settingRepoBuilder.build(getInstanceDir())
    private var setting : Setting = Setting() // cache

    companion object {
        private const val TEMPLATES_DIR = "templates/"
        const val TRACES_DIR = ".kprime/traces/"
        const val SOURCES_DIR = "sources/"

        // Settings per INSTANCE

        fun getClassInstanceDir(): String {
            val instanceDir = System.getenv("KPRIME_HOME")?:""
            if (!instanceDir.endsWith("/"))
                return "$instanceDir/"
            return instanceDir
        }
    }

    fun settingRead(currentUser: String) = settingRepo.read()

    fun settingWrite(currentUser: String, newSetting: Setting) {
        this.setting = newSetting
        settingRepo.write(newSetting)
    }

    // Settings Per USER

    fun newWorkingDir(newWorkingDir:String) {
        if (!File(newWorkingDir).mkdir()) {
            setting = setting.copy(workingDir = newWorkingDir)
            println("SettingService.newWorkingDir: $newWorkingDir")
            setWorkingDir(newWorkingDir)
        }
    }

    private fun setWorkingDir(dir: String) {
        println("SettingFileRepository.setWorkingDir:$dir")
        val setting = settingRepo.read()
        val newsetting = Setting(dir,
            setting.projectName,
            setting.traceName,
            setting.databaseName,
            setting.changesetName,
            setting.storyName)
        settingRepo.write(newsetting)
    }

    fun getWorkingDir(): String{
        if (setting.workingDir.isEmpty()) setting = settingRepo.read()
        println(" ---- SettingService.getWorkingDir: ${setting.workingDir}")
        return setting.workingDir
    }

    fun getTracesDir() : String{
        return getWorkingDir()+ TRACES_DIR
    }

    fun getTraceDir(traceName: TraceName): String {
        return getWorkingDir()+ TRACES_DIR +traceName.value+"/"
    }

    fun getTraceName(): String {
        setting = settingRepo.read()
        return setting.traceName
    }

    fun getSourcesDir(): String {
        return getWorkingDir()+ SOURCES_DIR
    }

    fun getProjectName(): String {
        if (setting.projectName.isEmpty()) setting = settingRepo.read()
        println(" ---- SettingService.getProjectName: ${setting.projectName}")
        return setting.projectName
    }

    fun getInstanceDir(): String {
        return getClassInstanceDir()
    }

    fun getTemplatesDir() : String{
        return getClassInstanceDir() + TEMPLATES_DIR
    }

}
