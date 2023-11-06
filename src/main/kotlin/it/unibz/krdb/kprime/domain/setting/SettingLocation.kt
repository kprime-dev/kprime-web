package it.unibz.krdb.kprime.domain.setting

@JvmInline
value class SettingLocation (val value:String) {
    fun isEmpty():Boolean = value.isEmpty()
    companion object {
        val NO_SETTING_LOCATION = SettingLocation("")
    }
}

