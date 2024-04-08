package it.unibz.krdb.kprime.support

import java.io.File

class MdHtmlPublisher {

    fun translate(mdFileText: String): String {
        val lines = mdFileText.split(System.lineSeparator())
        val htmlLines = mutableListOf<String>()
        htmlLines.add("<html>")
        htmlLines.add("<body>")
        translateBody(lines,htmlLines)
        htmlLines.add("</body>")
        htmlLines.add("</html>")
        return htmlLines.joinToString(System.lineSeparator())
    }

    fun translateBody(lines:List<String>, htmlLines: MutableList<String>) {
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
    }

    fun translateFile(fileToTranslate: File, createToc: Boolean) {
        val textMd = fileToTranslate.readText()
        val textHtml = translate(textMd)
        val fileTranslated = File(fileToTranslate.absolutePath.substringBeforeLast(".md")+".html")
        println("to translate ${fileToTranslate.absolutePath}")
        fileTranslated.writeText(textHtml)
        println("translated ${fileTranslated.absolutePath}")
        fileToTranslate.delete()
    }

    fun translateSingleFile(targetDirectory: File, files: List<File>, createToc: Boolean) {
        val fileTranslated = File(targetDirectory.absolutePath+"/single.html")
        val htmlLines = mutableListOf<String>()
        val tocLines = mutableListOf<String>()
        htmlLines.add("<html>")
        htmlLines.add("<body>")
        val htmlLinesPage = mutableListOf<String>()
        var currentSection = ""
        for (file in files) {
            val fileRelativeUrl = file.absolutePath
                .substringAfter(targetDirectory.absolutePath)
                .substringBeforeLast("/")
            if (currentSection != fileRelativeUrl) {
                tocLines.add("<h2>${currentSection}</h2>")
                currentSection = fileRelativeUrl
            }
            tocLines.add("<a href=\"#${file.name}\" >${file.name}</a><br>")
            val mdFileText = file.readText()
            val lines = mdFileText.split(System.lineSeparator())
            translateBody(lines,htmlLinesPage)
            htmlLines.add("<div id=\"${file.name}\" />")
            htmlLines.addAll(htmlLinesPage)
            htmlLinesPage.clear()
        }
        if (createToc) {
            tocLines.add(0,"<hr>")
            tocLines.add("<hr>")
            htmlLines.addAll(2,tocLines)
        }
        htmlLines.add("</body>")
        htmlLines.add("</html>")
        val textHtml =  htmlLines.joinToString(System.lineSeparator())
        fileTranslated.writeText(textHtml)
    }

    fun translateFolder(
        sourceFolderName: String, targetFolderName: String,
        singleFile: Boolean = false,
        createToc: Boolean = false
    ) {
        val sourceDirectory = File(sourceFolderName)
        val targetDirectory = File(targetFolderName)
        targetDirectory.deleteRecursively()
        sourceDirectory.copyRecursively(targetDirectory)
        val files = targetDirectory.walkTopDown()
            .filter { it.isFile }
            .filter { it.name.endsWith(".md") }
            .toList()
        println(files.size)
        if (singleFile) translateSingleFile(targetDirectory,files,createToc)
        else translateMultiFile(targetDirectory,files,createToc)
    }

    private fun translateMultiFile(targetDirectory: File, files: List<File>, createToc: Boolean) {
        val tocLines = mutableListOf<String>()
        tocLines.add("<body>")
        tocLines.add("<html>")
        var chapName = ""
        for (file in files) {
            val currentChapName = file.absolutePath.substringBeforeLast("/")
            if (currentChapName!=chapName) {
                tocLines.add("<h2>$currentChapName</h2>")
                chapName = currentChapName
            }
            val urlHtmlFile = file.absolutePath.substringAfter(targetDirectory.absolutePath).dropLast(3)+".html"
            val relativeUrlHtmlFile = if (urlHtmlFile.startsWith("/")) urlHtmlFile.drop(1) else urlHtmlFile
            println(relativeUrlHtmlFile)
            tocLines.add("<a href=\"${relativeUrlHtmlFile}\" >${file.name}</a><br>")
            translateFile(file, createToc)
        }
        tocLines.add("</body>")
        tocLines.add("</html>")
        val tocHtml =  tocLines.joinToString(System.lineSeparator())
        val tocFile = File(targetDirectory.absolutePath+"/toc.html")
        tocFile.writeText(tocHtml)
    }

    fun assetFolder(sourceFolderName:String,targetFolderName:String) {
        val sourceDirectory = File(sourceFolderName)
        val targetDirectory = File(targetFolderName)
        sourceDirectory.copyRecursively(targetDirectory)
    }
}