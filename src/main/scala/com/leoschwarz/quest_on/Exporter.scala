// Copyright 2014-2016 Leonardo Schwarz (leoschwarz.com)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
    if (results.nonEmpty) {
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
