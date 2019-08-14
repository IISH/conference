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
					Omschrijving: <xsl:value-of select="description" /> (<xsl:value-of select="exportDate" />)
				<br />
				</span>
				<span>
					<xsl:for-each select="sessions/session">
						<hr />
						<br />

							<span class="sessionDiv">
								<span class="sessionName">
									Naam: <xsl:value-of select="sessionname" />
								<br />
								</span>
								<span class="sessionCode">
                                    Sessie code: <xsl:value-of select="sessioncode" />
								<br />
								</span>
                                <span class="sessionType">
                                    Type: <xsl:value-of select="sessiontype" />
                                    <br />
                                </span>
								<span class="sessionAbstract">
                                    Sessie abstract: <xsl:value-of select="sessionabstract" />
								<br />
								</span>

								<br />

								<span class="sessionDate">
									Datum: <xsl:value-of select="../time/weekday" /> <xsl:text> </xsl:text> <xsl:value-of select="../time/day" /> <xsl:text> </xsl:text> <xsl:value-of select="../time/month" />
								<br />
								</span>
								<span class="sessionTime">
									Tijd: <xsl:value-of select="../time/starttime" /> - <xsl:value-of select="../time/endtime" />
								<br />
								</span>
								<span class="roomName">
									Zaal: <xsl:value-of select="location/locationname" />
								<br />
								</span>

								<br />

								<span class="sessionOrganizers">
									<xsl:if test="count(organizers/organizer) = 1">
										Organisator:
										<xsl:for-each select="organizers/organizer">
											<xsl:value-of select="name" />
											<xsl:if test="(organisation != '') and (organisation != 'null')">
												<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
											</xsl:if>
										</xsl:for-each>
										<br />
									</xsl:if>
									<xsl:if test="count(organizers/organizer) > 1">
										Organisatoren:
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
													<xsl:text>Auteur: </xsl:text><xsl:value-of select="presenter" />
													<xsl:if test="(organisation != '') and (organisation != 'null')">
														<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
													</xsl:if>
												</xsl:if>
												<xsl:if test="string-length(normalize-space(copresenters)) > 0">
													<xsl:text>Auteurs: </xsl:text><xsl:value-of select="presenter" />
													<xsl:if test="(organisation != '') and (organisation != 'null')">
														<xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
													</xsl:if>
													<xsl:text>, </xsl:text><xsl:value-of select="copresenters" />
												</xsl:if>
											<br />
											</span>
											<span class="paperSubject">
												Paper titel: <xsl:value-of select="subject" />
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
