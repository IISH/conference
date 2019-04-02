package org.iisg.eca.domain

/**
 * Domain class of table holding all paper files
 */
class PaperFile {
    Paper paper
    byte[] file

    static mapping = {
        table 'paper_files'
        version false

        id      column: 'paper_file_id'
        paper	column: 'paper_id'
        file	column: 'file',		    sqlType: 'mediumblob'
    }

    static constraints = {
        paper   unique: true
        file	nullable: true
    }
}
