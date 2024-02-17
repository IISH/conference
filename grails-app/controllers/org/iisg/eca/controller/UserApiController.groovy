package org.iisg.eca.controller

import org.iisg.eca.domain.PaperFile
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Setting

import org.apache.commons.io.FilenameUtils
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.multipart.commons.CommonsMultipartFile

class UserApiController {
    private static final int ERROR_NONE = 0
    private static final int ERROR_ID_NOT_FOUND = 1
    private static final int ERROR_USER_NOT_ALLOWED = 2
    private static final int ERROR_EMPTY_FILE = 3
    private static final int ERROR_LARGE_FILE = 4
    private static final int ERROR_EXT_NOT_ALLOWED = 5
    private static final int ERROR_OTHER = 6

    def exportService
    def pageInformation
    def springSecurityService

    def uploadPaper() {
        String[] allowedExtensions = Setting.getSetting(Setting.ALLOWED_PAPER_EXTENSIONS).value.split('\\s')
        long maxSize = Setting.getSetting(Setting.MAX_UPLOAD_SIZE_PAPER).value.toLong()
        int error = ERROR_NONE

        try {
            Paper paper = Paper.findById(params.long('paper-id'))
            User user = User.findByEmail(springSecurityService.principal.toString())

            if (paper) {
                if (user && (paper.user.id == user.id)) {
                    CommonsMultipartFile file = (CommonsMultipartFile) params['paper-file']
                    if (file?.size <= 0) {
                        error = ERROR_EMPTY_FILE
                    }
                    else if (file.size > maxSize) {
                        error = ERROR_LARGE_FILE
                    }
                    else if (!allowedExtensions.contains(FilenameUtils.getExtension(file.originalFilename))) {
                        error = ERROR_EXT_NOT_ALLOWED
                    }
                    else {
                        paper.fileSize = file.size
                        paper.contentType = file.contentType
                        paper.fileName = file.originalFilename

                        if (!paper.paperFile.isEmpty()) {
                            paper.paperFile[0].file = file.bytes
                        }
                        else {
                            paper.addToPaperFile(new PaperFile(file: file.bytes))
                        }

                        paper.save(flush: true, failOnError: true)
                    }
                }
                else {
                    error = ERROR_USER_NOT_ALLOWED
                }
            }
            else {
                error = ERROR_ID_NOT_FOUND
            }
        }
        catch (Exception e) {
            error = ERROR_OTHER
        }
        finally {
            if (params['back-url']) {
                String backUrl = params['back-url'].toString();

                // TODO: Old versions still send the absolute URL
                if (backUrl.startsWith('http')) {
                    redirect(url: UriComponentsBuilder
                            .fromHttpUrl(backUrl)
                            .replaceQueryParam('e', error)
                            .build()
                            .toString())
                }
                else {
                    redirect(url: UriComponentsBuilder
                            .fromHttpUrl(Setting.getSetting(Setting.WEB_ADDRESS).value)
                            .replacePath(backUrl)
                            .replaceQueryParam('e', error)
                            .build()
                            .toString())
                }
            }
            else if (error == ERROR_NONE) {
                render 'OK'
            }
            else {
                response.sendError(400, error.toString())
            }
        }
    }

    def downloadPaper() {
	    Date downloadPaperLastDate = Setting.getSetting(Setting.DOWNLOAD_PAPER_LASTDATE).getDateValue()
        /* WARNING: has download_paper_lastdate expired? is this the last/latest date_id for this event_id? */
	    if (pageInformation.date.isLastDate() && (downloadPaperLastDate.compareTo(new Date().clearTime()) >= 0)) {
		    Paper paper = Paper.findById(params.id)

		    // Let the export service deal with returning the uploaded paper
		    if (paper && (paper.fileSize > 0)) {
			    String prepend = "${pageInformation.date.event.getUrl()}-${pageInformation.date.getUrl()}-${paper.user.id}-"
			    exportService.getPaper(paper, response, prepend)
			    return
		    }
	    }

	    render 'Paper not found! You can only download papers from the last/latest/current conference. Papers from previous conferences are not accessible any more.'
    }
}
