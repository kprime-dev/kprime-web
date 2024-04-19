package it.unibz.krdb.kprime.support

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfWriter
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MdPdfPublisher {

    enum class Metadata { Author, Version }

    fun translateFolder(
        sourceFolderName: String, targetFolderName: String,
        contextPath: String,
        contextName: String,
        metadata: Map<Metadata, String>,
        createToc: Boolean = false
    ): String {

        val targetDirectory = File(targetFolderName)
        val sourceDirectory = File(sourceFolderName)
        val index = File(sourceFolderName+"index.md")
        println(index.absolutePath)
        val files =
        if (index.exists()) {
            index.readText().split(System.lineSeparator())
                .filter { it.isNotEmpty() && !it.startsWith("---") && !it.startsWith("#")}
                .map { it.substringAfter("](").substringBeforeLast(")") }
                .map { File(contextPath+it) }
                .filter { it.exists() }
        } else {
            sourceDirectory.walkTopDown()
                .filter { it.isFile }
                .filter { it.name.endsWith(".md") }
                .toList()
        }
        return translateMultiFile(targetDirectory,files, contextPath, contextName, metadata, createToc)
    }


    private fun translateMultiFile(targetDirectory: File, files: List<File>,
                                       contextPath: String,
                                       contextName:String,
                                       metadata:Map<Metadata,String>,
                                       createToc: Boolean): String {

        val font24 = FontFactory.getFont(FontFactory.HELVETICA, 24.0f)
        val font12 = FontFactory.getFont(FontFactory.HELVETICA, 12.0f)
        val metadataAuthor = metadata.get(Metadata.Author)?:""
        val metadataVersion = metadata.get(Metadata.Version)?:""
        val document = Document(PageSize.A4)
        File(targetDirectory.absolutePath).mkdirs()
        val outPdfFilename = targetDirectory.absolutePath + "/$contextName.pdf"
        val fileOutputStream = FileOutputStream(outPdfFilename)
        val  writer = PdfWriter.getInstance(document, fileOutputStream)
        document.open()
//        document.add(Paragraph(contextName, font24))

//        val c = Chunk("Multiple lines", FontFactory.getFont(FontFactory.HELVETICA, 24f))
//        c.setUnderline(Color(0xFF, 0x00, 0x00), 0.0f, 0.3f, 0.0f, 0.4f, PdfContentByte.LINE_CAP_ROUND)
//        c.setUnderline(
//            Color(0x00, 0xFF, 0x00), 5.0f, 0.0f, 0.0f, -0.5f,
//            PdfContentByte.LINE_CAP_PROJECTING_SQUARE
//        )
        val c = Chunk(contextName, font24)
        c.setUnderline(Color(0xFF, 0x00, 0x00 ), 0.0f, 0.2f, 0.0f, 0.0f, PdfContentByte.LINE_CAP_BUTT)
        document.add(c)

        if (metadataAuthor.isNotEmpty()) document.add(Paragraph(metadataAuthor, font12))
        if (metadataVersion.isNotEmpty()) document.add(Paragraph(metadataVersion, font12))
        document.newPage()
        for (file in files) {
            val mdFileText = file.readText()
            mdToDocument(mdFileText, document, contextPath)
            document.newPage()
        }
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val timestamp = formatter.format(Date())
        addFooter(document,"$contextName $timestamp")
        document.close()
        return outPdfFilename
    }

    private fun addFooter(document: Document, phraseText:String) {
        val footer = HeaderFooter(true, Phrase(phraseText))
        footer.setAlignment(Element.ALIGN_CENTER)
        footer.borderWidthBottom = 0f
        document.setFooter(footer)
    }

    fun textToPdf(mdFileText: String, outPdfFilename:String, contextPath:String) {
        val document = Document(PageSize.A4)
        val  writer = PdfWriter.getInstance(document, FileOutputStream(outPdfFilename))
        document.open()
        mdToDocument(mdFileText, document, contextPath)
        document.close()
    }

    private fun mdToDocument(mdFileText: String, document: Document, contextPath: String) {
        var outOfQuote = true
        val quoteLines = mutableListOf<String>()
        val noMetadataText = if (mdFileText.contains("~~~~~~")) mdFileText.substringAfter("~~~~~~")
        else mdFileText
        val lines = noMetadataText.split(System.lineSeparator())
        val fontQuote = FontFactory.getFont(FontFactory.COURIER, 8.0f)
        val font8 = FontFactory.getFont(FontFactory.HELVETICA, 8.0f)
        val font10 = FontFactory.getFont(FontFactory.HELVETICA, 10.0f)
        val font12 = FontFactory.getFont(FontFactory.HELVETICA, 12.0f)
        val font16 = FontFactory.getFont(FontFactory.HELVETICA, 16.0f)
        val font20 = FontFactory.getFont(FontFactory.HELVETICA, 20.0f, Font.NORMAL, Color(0x00, 0x00, 0xFF))
        val font24 = FontFactory.getFont(FontFactory.HELVETICA, 24.0f, Font.NORMAL, Color(0xFF, 0x00, 0x00 ))
        for (mdLine in lines) {
            if (mdLine.startsWith("---")) {
                val c = Chunk("                         ", font24)
                c.setUnderline(Color(0xFF, 0x00, 0x00 ), 0.0f, 0.1f, 0.0f, 0.0f, PdfContentByte.LINE_CAP_BUTT)
                document.add(c)
            } else
            if (mdLine.startsWith("#####"))
                document.add(Paragraph(mdLine.substring(6), font10))
            else if (mdLine.startsWith("####"))
                document.add(Paragraph(mdLine.substring(5), font12))
            else if (mdLine.startsWith("###"))
                document.add(Paragraph(mdLine.substring(4), font16))
            else if (mdLine.startsWith("##"))
                document.add(Paragraph(mdLine.substring(3), font20))
            else if (mdLine.startsWith("#"))
                document.add(Paragraph(mdLine.substring(2), font24))
            else if (mdLine.startsWith("```") && outOfQuote) {
                outOfQuote = false;
            } else if (mdLine.startsWith("```") && !outOfQuote) {
                for (quoteLine in quoteLines) {
                    val c = Chunk(quoteLine, fontQuote)
                    c.setBackground(Color(0xFF, 0xFF, 0xDD ))
                    //val p = Paragraph(quoteLine, fontQuote)
                    val p = Paragraph(c)
                    document.add(p)
                }
                outOfQuote = true;
            } else if (!outOfQuote)
                quoteLines.add(mdLine)
            else if (mdLine.startsWith("![")) {
                val before = mdLine.substringBefore("![")
                val alt = mdLine.substringAfter("![").substringBefore("]")
                val src = mdLine.substringAfter("](").substringBefore(")")
                val after = mdLine.substringAfter("![").substringAfter(")")
                val urlStringImage = if (src.startsWith("http")) src else contextPath + src
                val jpg: Image = Image.getInstance(urlStringImage)
                jpg.scalePercent(50.0f)
                document.add(jpg)
            } else
                document.add(Paragraph(mdLine, font8))
        }
    }
}