package org.iisg.eca.controller

import grails.validation.ValidationException
import org.codehaus.groovy.grails.web.servlet.FlashScope
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.PaperReview
import org.iisg.eca.domain.User
import org.springframework.validation.FieldError
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * Controller responsible for importing data
 */
class ImportController {
	def pageInformation

	def index() { }

	def users() {
		onCsvImport(params, flash) { String[] tokens ->
			if (tokens.size() != 3)
				throw new Exception("Expecting three columns!")

			new User(lastName: tokens[0], firstName: tokens[1], email: tokens[2]).save(failOnError: true)
		}
	}

	def reviewers() {
		onCsvImport(params, flash) { String[] tokens ->
			if (tokens.size() != 2)
				throw new Exception("Expecting two columns!")

			if (!tokens[0].isLong())
				throw new Exception("Expecting paper id, got ${tokens[0]} instead!")

			Paper paper = Paper.findById(tokens[0].toLong())
			if (!paper)
				throw new Exception("No paper found with the id ${tokens[0]}!")

			User user = User.findByEmail(tokens[1])
			if (!user)
				throw new Exception("No user found with the email address ${tokens[1]}!")

			if (!user.reviewers.find { it.confirmed && it.date.id == pageInformation.date.id })
				throw new Exception("The user with email address ${tokens[1]} is not a reviewer!")

			new PaperReview(reviewer: user, paper: paper).save(failOnError: true)
		}
	}

	private void onCsvImport(GrailsParameterMap params, FlashScope flash, Closure forEachCsvLine) {
		CommonsMultipartFile file = (CommonsMultipartFile) params['csv']
		if (!file || file.size == 0) {
			flash.error = true
			flash.message = g.message(code: 'default.null.message', args: [g.message(code: 'default.file')])
			redirect(uri: eca.createLink(action: 'index', noBase: true, noPreviousInfo: true))
			return
		}

		String separator = ','
		if (params.sep == ';') separator = ';'
		else if (params.sep == 'tab') separator = '\t'

		User.withTransaction { status ->
			try {
				file.inputStream.toCsvReader(['charset': 'UTF-8', 'separatorChar': separator, ]).eachLine { String[] tokens ->
					forEachCsvLine(tokens)
				}
				flash.message = g.message(code: 'default.import.success.message')
			}
			catch (Exception e) {
				flash.error = true
				flash.message = e.message
				if (e instanceof ValidationException) {
					FieldError fieldError = e.errors.getFieldError()
					flash.message = g.message(code: fieldError.defaultMessage, args: fieldError.arguments)
				}
				status.setRollbackOnly()
			}
			finally {
				redirect(uri: eca.createLink(action: 'index', noBase: true, noPreviousInfo: true))
			}
		}
	}
}
