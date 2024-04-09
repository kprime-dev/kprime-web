package it.unibz.krdb.kprime.generic.pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.apache.commons.io.IOUtils.writer
import org.junit.Ignore
import java.io.FileOutputStream
import kotlin.test.Test


class PdfSaucerTest {

    @Test
    @Ignore
    fun test_pdf_hello() {
        val document = Document(PageSize.A4)
        val  writer = PdfWriter.getInstance(document, FileOutputStream("/home/nipe/Downloads/hello2.pdf"))

        document.open()
        document.add(Paragraph("A picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpg"))
        val jpg: Image = Image.getInstance("/home/nipe/Pictures/woodstock.jpg")
        document.add(jpg)
        document.newPage()
        document.add(Paragraph("A Second picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpgA picture of my dog: otsoe.jpg"))

        document.newPage()
        val width = document.pageSize.width
        val height = document.pageSize.height
        // step 3
        // step 3

        // step 4

        // step 4
        val columnDefinitionSize = floatArrayOf(33.33f, 33.33f, 33.33f)

        val pos = height / 2
        var table: PdfPTable? = null
        var cell: PdfPCell? = null

        table = PdfPTable(columnDefinitionSize)
        table.defaultCell.border = 1
        table.horizontalAlignment = 0
        table.totalWidth = width - 72
        table.isLockedWidth = true

        val font8 = FontFactory.getFont(FontFactory.HELVETICA, 8.0f)

        cell = PdfPCell(Phrase("Table added with document.add()"))
        cell.colspan = columnDefinitionSize.size
        table.addCell(cell)
        table.addCell(Phrase("Louis Pasteur", font8))
        table.addCell(Phrase("Albert Einstein", font8))
        table.addCell(Phrase("Isaac Newton", font8))

        table.addCell(Phrase("8, Rabic street", font8))
        table.addCell(Phrase("2 Photons Avenue", font8))
        table.addCell(Phrase("32 Gravitation Court", font8))

        table.addCell(Phrase("39100 Dole France", font8))
        table.addCell(Phrase("12345 Ulm Germany", font8))
        table.addCell(Phrase("45789 Cambridge  England", font8))

        table.addCell(Phrase("Louis Pasteur", font8))
        table.addCell(Phrase("Albert Einstein", font8))
        table.addCell(Phrase("Isaac Newton", font8))

        table.addCell(Phrase("8, Rabic street", font8))
        table.addCell(Phrase("2 Photons Avenue", font8))
        table.addCell(Phrase("32 Gravitation Court", font8))

        table.addCell(Phrase("39100 Dole France", font8))
        table.addCell(Phrase("12345 Ulm Germany", font8))
        table.addCell(Phrase("45789 Cambridge  England", font8))

        document.add(table)

        table = PdfPTable(columnDefinitionSize)
        table.defaultCell.border = 1
        table.horizontalAlignment = 0
        table.totalWidth = width - 72
        table.isLockedWidth = true

        cell = PdfPCell(Phrase("Table added with writeSelectedRows"))
        cell.colspan = columnDefinitionSize.size
        table.addCell(cell)
        table.addCell(Phrase("Louis Pasteur", font8))
        table.addCell(Phrase("Albert Einstein", font8))
        table.addCell(Phrase("Isaac Newton", font8))

        table.addCell(Phrase("8, Rabic street", font8))
        table.addCell(Phrase("2 Photons Avenue", font8))
        table.addCell(Phrase("32 Gravitation Court", font8))

        table.addCell(Phrase("39100 Dole France", font8))
        table.addCell(Phrase("12345 Ulm Germany", font8))
        table.addCell(Phrase("45789 Cambridge  England", font8))

        table.writeSelectedRows(0, -1, 50.0f, pos, writer.directContent)


        document.close()
    }
}