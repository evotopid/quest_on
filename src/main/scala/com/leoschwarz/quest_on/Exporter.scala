package com.leoschwarz.quest_on

import java.io.ByteArrayOutputStream

import com.leoschwarz.quest_on.data.Result
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.streaming.SXSSFWorkbook

class Exporter(private val results: IndexedSeq[Result]) {
  def export(): Array[Byte] = {
    // Create the workbook
    val wb = new SXSSFWorkbook
    val sheet = wb.createSheet()
    val boldFont = wb.createFont()
    boldFont.setBold(true)
    val boldStyle = wb.createCellStyle()
    boldStyle.setFont(boldFont)

    // Setup the header.
    implicit var row = sheet.createRow(0)
    setCell(0, "Submitted At")
    if (results.length > 1) {
      val data = results(0).parseData.get
      var i = 1
      for (key <- data.keys) {
        setCell(i, key)
        i += 1
      }
    }
    row.setRowStyle(boldStyle)

    // Fill in data rows.
    var rowIndex = 1
    for (result <- results) {
      row = sheet.createRow(rowIndex)
      setCell(0, result.submittedAt.toString)

      var colIndex = 1
      for (value <- result.parseData.get.values) {
        setCell(colIndex, value)
        colIndex += 1
      }

      rowIndex += 1
    }

    // Generate the output
    val output = new ByteArrayOutputStream
    wb.write(output)
    output.close()
    wb.dispose()
    output.toByteArray
  }

  private def setCell(col: Int, text: String)(implicit row: Row): Unit = {
    val cell = row.createCell(col)
    cell.setCellValue(text)
  }
}
