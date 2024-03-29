<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/data">
		<html>
			<head>
				<style>
					.mainDescription{
						font-size: 24pt;
					}
					.paper {
						margin-bottom: 30px;
					}
					hr {
						margin-top: 30px;
						margin-bottom: 30px;
					}
				</style>
			</head>
			<body>
				<span class="mainDescription">
					Description: <xsl:value-of select="description" /> (<xsl:value-of select="exportDate" />)
				<br />
				</span>
				<span>
					<xsl:for-each select="sessions/session">
						<hr />

							<span class="sessionDiv">
								<span class="sessionCode">
									Panel code: <xsl:value-of select="sessioncode" />
									<br />
								</span>
								<span class="sessionName">
									Panel name: <xsl:value-of select="sessionname" />
								<br />
								</span>
								<span class="sessionAbstract">
									Panel abstract: <xsl:value-of select="sessionabstract" />
								<br />
								</span>
								<br />

								<span class="sessionOrganizers">
									<xsl:if test="count(organizers/organizer) = 1">
										Organizer: 
										<xsl:for-each select="organizers/organizer">
											<xsl:value-of select="name" />
											<xsl:if test="(organisation != '') and (organisation != 'null')">
												<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
											</xsl:if>
										</xsl:for-each>
										<br />
									</xsl:if>
									<xsl:if test="count(organizers/organizer) > 1">
										Organizers: 
										<xsl:for-each select="organizers/organizer">
											<xsl:value-of select="name" />
											<xsl:if test="(organisation != '') and (organisation != 'null')">
												<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
											</xsl:if>
											<xsl:if test="position() != last()">
												<xsl:text>, </xsl:text>
											</xsl:if>
										</xsl:for-each>
										<br />
									</xsl:if>
								</span>

								<span class="sessionDiscussants">
									<xsl:if test="count(discussants/discussant) = 1">
										Discussant: 
										<xsl:for-each select="discussants/discussant">
											<xsl:value-of select="name" />
											<xsl:if test="(organisation != '') and (organisation != 'null')">
												<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
											</xsl:if>
										</xsl:for-each>
										<br />
									</xsl:if>
									<xsl:if test="count(discussants/discussant) > 1">
										Discussants: 
										<xsl:for-each select="discussants/discussant">
											<xsl:value-of select="name" />
											<xsl:if test="(organisation != '') and (organisation != 'null')">
												<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
											</xsl:if>
											<xsl:if test="position() != last()">
												<xsl:text>, </xsl:text>
											</xsl:if>
										</xsl:for-each>
										<br />
									</xsl:if>
								</span>

								<br />

								<span class="papers">
									<xsl:for-each select="papers/paper">
										<span class="paper">
											<span class="paperAuthors">
												<xsl:if test="string-length(normalize-space(copresenters)) = 0">
													<xsl:text>Author: </xsl:text><xsl:value-of select="presenter" />
													<xsl:if test="(organisation != '') and (organisation != 'null')">
														<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
													</xsl:if>
												</xsl:if>
												<xsl:if test="string-length(normalize-space(copresenters)) > 0">
													<xsl:text>Authors: </xsl:text><xsl:value-of select="presenter" />
													<xsl:if test="(organisation != '') and (organisation != 'null')">
														<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
													</xsl:if>
													<xsl:text>, </xsl:text><xsl:value-of select="copresenters" />
												</xsl:if>
											<br />
											</span>
											<span class="paperSubject">
												Paper title: <xsl:value-of select="subject" />
											<br />
											</span>
											<span class="paperAbstract">
												Paper abstract: <xsl:value-of select="abstract" />
											<br />
											</span>
										<br />
									</span>
									</xsl:for-each>
								</span>
						</span>

					</xsl:for-each>
				<br />
				</span>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
