package it.unibz.krdb.kprime.support

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FolderZipper {

    fun zip(folderName:String): File {

        val inputDirectory = File(folderName)
        val outputZipFile = File.createTempFile("out", ".zip")
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZipFile))).use { zos ->
            inputDirectory.walkTopDown().forEach { file ->
                val entryFileName = file.absolutePath.removePrefix(inputDirectory.absolutePath).removePrefix("/")
                val entry = ZipEntry( "$entryFileName${(if (file.isDirectory) "/" else "" )}")
                zos.putNextEntry(entry)
                if (file.isFile) {
                    file.inputStream().copyTo(zos)
                }
            }
        }
        return outputZipFile
    }
}