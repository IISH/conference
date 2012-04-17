package org.iisg.eca

/*import jxl.Workbook
import jxl.WorkbookSettings

import jxl.write.Label
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.WritableCellFormat   */

import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

/**
 * A parser to create xls (Excel) files
 */
class XlsParser implements Parser {
    private static final String CONTENT_TYPE = 'application/ms-excel'
    private static final ValidationTagLib MESSAGES = new ValidationTagLib()

    private String title
    private DataContainer view

    /**
     * Creates a new xls parser for the specified element
     * @param pageElement The element to parse
     * @param title The title of the resulting file
     */
    XlsParser(DataContainer view, String title) {
        this.view = view
        this.title = title
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
     * Parses the results to an xls file
     */
    @Override
    parse() {
        File file = File.createTempFile('excel', '.xls')
        file.deleteOnExit()

       /* WritableWorkbook workbook = Workbook.createWorkbook(file, new WorkbookSettings())

        WritableFont font = new WritableFont(WritableFont.ARIAL, 12)
        WritableFont fontBold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD)

        WritableCellFormat format = new WritableCellFormat(font)
        WritableCellFormat formatBold = new WritableCellFormat(fontBold)

        format.setShrinkToFit(true)
        formatBold.setShrinkToFit(true)

        WritableSheet sheet = workbook.createSheet(title, 0)

        view.allColumns.eachWithIndex { column, j ->
            String name = view.getDomainClassByColumn(column)
            String columnName = MESSAGES.message(code: "${name.toLowerCase()}.${column.toLowerCase()}.label")?.toLowerCase()
            sheet.addCell(new Label(j, 0, columnName, formatBold))
        }

        view.result.eachWithIndex { row, i ->
            view.allColumns.eachWithIndex { column, j ->
                sheet.addCell(new Label(j, i+1, row[column], format))
            }
        }

        workbook.write()
        workbook.close() */
        file.bytes
    }
}
