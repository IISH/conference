package org.iisg.eca.export

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
class XlsExport extends AbstractExport {
    private static final String CONTENT_TYPE = 'application/ms-excel'

    /**
     * Creates a new xls export for the specified element
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     */
    XlsExport(List columns, List results, String title) {
        super(columns, results, title)
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
     * Export the results to an xls file
     */
    @Override
    parse() {
        File file = File.createTempFile('excel', '.xls')
        file.deleteOnExit()

        WritableWorkbook workbook = Workbook.createWorkbook(file, new WorkbookSettings())

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableFont fontBold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD)

        WritableCellFormat format = new WritableCellFormat(font)
        WritableCellFormat formatBold = new WritableCellFormat(fontBold)

        format.setShrinkToFit(true)
        formatBold.setShrinkToFit(true)

        WritableSheet sheet = workbook.createSheet(title, 0)

        columnNames.eachWithIndex { column, j ->
            sheet.addCell(new Label(j, 0, column, formatBold))
        }

        results.eachWithIndex { result, i ->
            if (result.class.isArray()) {
                columns.eachWithIndex { c, j ->
                    sheet.addCell(new Label(j, i+1, result.find { it.class.simpleName == c.domainClass.name }[c.name], format))
                }
            }
            else {
                columns.eachWithIndex { c, j ->
                    sheet.addCell(new Label(j, i+1, result[c.name], format))
                }
            }
        }

        workbook.write()
        workbook.close()
        file.bytes
    }
}
