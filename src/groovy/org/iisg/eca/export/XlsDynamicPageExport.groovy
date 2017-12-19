package org.iisg.eca.export

import groovy.transform.CompileStatic
import jxl.CellView
import jxl.Workbook
import jxl.WorkbookSettings

import jxl.write.Label
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.WritableCellFormat
import org.iisg.eca.dynamic.Column

/**
 * Export xls (Excel) files
 */
@CompileStatic
class XlsDynamicPageExport extends DynamicPageExport {
    private static final String CONTENT_TYPE = 'application/vnd.ms-excel'
    private static final String EXTENSION = 'xls'

    /**
     * Creates a new xls export for the specified element
     * @param columns An array of columns to export
     * @param results The results, a list with arrays of domain classes, to export
     * @param title The title of the resulting file
     */
    XlsDynamicPageExport(List columns, List results, String title) {
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
        File file = File.createTempFile('excel', '.xls')
        file.deleteOnExit()

        WritableWorkbook workbook = Workbook.createWorkbook(file, new WorkbookSettings())

        WritableFont font = new WritableFont(WritableFont.TAHOMA, 12)
        WritableFont fontBold = new WritableFont(WritableFont.TAHOMA, 12, WritableFont.BOLD)

        WritableCellFormat format = new WritableCellFormat(font)
        WritableCellFormat formatBold = new WritableCellFormat(fontBold)

        WritableSheet sheet = workbook.createSheet(title, 0)

        columnNames.eachWithIndex { column, j ->
            sheet.addCell(new Label(j, 0, columnNames[j].toString(), formatBold))
        }

        results.eachWithIndex { result, i ->
            columns.grep { Column it -> it.canBeShown() && !it.isHidden() }.eachWithIndex { c, j ->
                def value = result
                c.columnPath.each { value = value[it.toString()] }
                sheet.addCell(new Label(j, i+1, value.toString(), format))
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
