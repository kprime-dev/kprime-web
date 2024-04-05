package it.unibz.krdb.kprime.support

import java.io.File

class MdHtmlPublisher {

    fun translate(mdFileText: String): String {
        val lines = mdFileText.split(System.lineSeparator())
        val htmlLines = mutableListOf<String>()
        htmlLines.add("<html>")
        htmlLines.add("<body>")
        var startQuote=true
        for (mdLine in lines) {
            var skipLine = false
            //headers
            if (mdLine.startsWith("#####")) {
                val mdContent = mdLine.substringAfter("#####")
                htmlLines.add("<h5>$mdContent</h5>")
                skipLine=true
            } else if (mdLine.startsWith("####")) {
                val mdContent = mdLine.substringAfter("####")
                htmlLines.add("<h4>$mdContent</h4>")
                skipLine=true
            } else if (mdLine.startsWith("###")) {
                val mdContent = mdLine.substringAfter("###")
                htmlLines.add("<h3>$mdContent</h3>")
                skipLine=true
            } else if (mdLine.startsWith("##")) {
                val mdContent = mdLine.substringAfter("##")
                htmlLines.add("<h2>$mdContent</h2>")
                skipLine=true
            } else if (mdLine.startsWith("#")) {
                val mdContent = mdLine.substringAfter("#")
                htmlLines.add("<h1>$mdContent</h1>")
                skipLine=true
            } else if (mdLine.startsWith("---")) {
                htmlLines.add("<hr/>")
                skipLine=true
            }
            // quotations
            if (mdLine.startsWith("```") && startQuote==false) {
                htmlLines.add("</pre>")
                htmlLines.add("</blockquote>")
                startQuote=true
                skipLine=true
            } else if (mdLine.startsWith("```") && startQuote==true) {
                htmlLines.add("<blockquote>")
                htmlLines.add("<pre>")
                skipLine=true
                startQuote=false
                skipLine=true
            }
            //removes all previous metadata lines if present
            if (mdLine.startsWith("~~~~~~")) {
                htmlLines.clear()
                skipLine=true
            }
            // img
            if (mdLine.contains("![")) {
                val before = mdLine.substringBefore("![")
                val alt = mdLine.substringAfter("![").substringBefore("]")
                val src = mdLine.substringAfter("](").substringBefore(")")
                val after = mdLine.substringAfter("![").substringAfter(")")
                htmlLines.add("$before<img alt=\"$alt\" src=\"$src\" />$after")
                skipLine=true
            } else
            // anchor
            if (mdLine.contains("[")) {
                val before = mdLine.substringBefore("[")
                val alt = mdLine.substringAfter("[").substringBefore("]")
                val src = mdLine.substringAfter("](").substringBefore(")")
                val after = mdLine.substringAfter("![").substringAfter(")")
                htmlLines.add("$before<a href=\"$src\" />$alt</a>$after")
                skipLine=true
            }
            // normal line
            if (!skipLine) {
                htmlLines.add(mdLine)
                skipLine = false
            }
        }
        htmlLines.add("</body>")
        htmlLines.add("</html>")
        return htmlLines.joinToString(System.lineSeparator())
    }

    fun translateFile(fileToTranslate:File) {
        val textMd = fileToTranslate.readText()
        val textHtml = translate(textMd)
        val fileTranslated = File(fileToTranslate.absolutePath.substringBeforeLast(".md")+".html")
        println("to translate ${fileToTranslate.absolutePath}")
        fileTranslated.writeText(textHtml)
        println("translated ${fileTranslated.absolutePath}")
        fileToTranslate.delete()
    }

    fun translateFolder(sourceFolderName:String,targetFolderName:String) {
        val sourceDirectory = File(sourceFolderName)
        val targetDirectory = File(targetFolderName)
        targetDirectory.deleteRecursively()
        sourceDirectory.copyRecursively(targetDirectory)
        val files = targetDirectory.walkTopDown()
            .filter { it.isFile }
            .filter { it.name.endsWith(".md") }
            .toList()
        println(files.size)
        for (file in files) translateFile(file)
        //files.apply { ::translateFile }
    }

    fun assetFolder(sourceFolderName:String,targetFolderName:String) {
        val sourceDirectory = File(sourceFolderName)
        val targetDirectory = File(targetFolderName)
        sourceDirectory.copyRecursively(targetDirectory)
    }
}