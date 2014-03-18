package org.iisg.eca.export

import jxl.CellView
import jxl.Workbook
import jxl.WorkbookSettings

import jxl.write.Label
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.WritableCellFormat

/**
 * Export xls (Excel) files
 */
class XlsMapExport extends MapExport {
    private static final String CONTENT_TYPE = 'application/vnd.ms-excel'
    private static final String EXTENSION = 'xls'

    /**
     * Creates a new xls export for the specified element
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     * @param columnNames The names of the columns
     */
    XlsMapExport(List<String> columns, List<Map> results, String title, List<String> columnNames) {
        super(columns, results, title, columnNames)
    }

    /**
     * Returns the content type of xls files
     * @return The content type
     */
    @Override
    String getContentType() {
        CONTENT_TYPE
    }

    /**
     * Returns the extension of csv files
     * @return The extension
     */
    @Override
    String getExtension() {
        EXTENSION
    }

    /**
     * Export the results to an xls file
     */
    @Override
    parse() {
        File file = File.createTempFile('excel', '.' + EXTENSION)
        file.deleteOnExit()

        WritableWorkbook workbook = Workbook.createWorkbook(file, new WorkbookSettings())

        WritableFont font = new WritableFont(WritableFont.TAHOMA, 12)
        WritableFont fontBold = new WritableFont(WritableFont.TAHOMA, 12, WritableFont.BOLD)

        WritableCellFormat format = new WritableCellFormat(font)
        WritableCellFormat formatBold = new WritableCellFormat(fontBold)

        WritableSheet sheet = workbook.createSheet(title, 0)

        columnNames.eachWithIndex { name, j ->
            sheet.addCell(new Label(j, 0, name.toString(), formatBold))
        }

        results.eachWithIndex { result, i ->
            columns.eachWithIndex { c, j ->
                if (result[c] != null) {
                    sheet.addCell(new Label(j, i+1, result[c].toString(), format))
                }
            }
        }

        for (int i=0; i<columns.size(); i++) {
            CellView cell = sheet.getColumnView(i)
            cell.autosize = true
            sheet.setColumnView(i, cell)
        }

        workbook.write()
        workbook.close()
        file.bytes
    }
}
