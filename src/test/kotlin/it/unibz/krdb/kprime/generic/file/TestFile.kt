package it.unibz.krdb.kprime.generic.file

import org.junit.Ignore
import org.junit.Test
import java.io.File

class TestFile {

    @Test
    @Ignore
    fun test_filenames() {
        // given
        val workingTraceDir = "/home/nipe/Temp/totrash2/trashyy/traces/root/"
        // when
        val names = File(workingTraceDir)
            .listFiles{ f -> f.isFile && f.name.endsWith("_db.xml") }.orEmpty()
            .sortedBy { it.lastModified() }
            .mapNotNull { f->  f.nameWithoutExtension.dropLast(3) }
        // then
        println(names)
    }
}