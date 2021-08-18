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
					.sessionCode {
                        font-weight: bold;
					}
                    .sessionName {
                        font-weight: bold;
                    }
                    .moderatorName {
                        font-weight: bold;
                    }
                    .sprekerName {
                        font-weight: bold;
                    }
                    .coSprekerName {
                    }
				</style>
			</head>
			<body>
				<span class="mainDescription">
					Omschrijving: <xsl:value-of select="description" /> (<xsl:value-of select="exportDate" />)
				<br />
				</span>
				<span>

                    <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'" />
                    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

                    <xsl:for-each select="sessions/session">
                        <br />
                        <br />
						<br />

                        <span class="sessionDiv">
                            <span class="sessionCode">
                                <xsl:value-of select="sessioncode" />
                            </span>

                            <xsl:text> </xsl:text>

                            <span class="sessionName">
                                <xsl:value-of select="translate(sessionname, $lowercase, $uppercase)" />
                            </span>
                            <br />
                            <br />

                            <span class="sessionType">
                                Type: <xsl:value-of select="sessiontype" />
                                <br />
                            </span>

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
                            </span>
                            <br />

                            <span class="voertaal">
                                Voertaal: NL
                            </span>
                            <br />
                            <br />

                            <span class="sessionChairs">
                                <xsl:if test="count(chairs/chair) > 0">
                                    Moderator:
                                    <xsl:for-each select="chairs/chair">
                                        <span class="moderatorName">
                                            <xsl:value-of select="name" />
                                        </span>
                                        <xsl:if test="(organisation != '') and (organisation != 'null')">
                                            <xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
                                        </xsl:if>
                                        <xsl:if test="position() != last()">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                    </xsl:for-each>
                                    <br />
                                    <br />
                                </xsl:if>
                            </span>


                            <span class="papers">
                                <xsl:if test="count(papers/paper) > 0">
                                    <xsl:text>Sprekers: </xsl:text>
                                    <xsl:for-each select="papers/paper"><xsl:if test="position() &gt; 1">, </xsl:if>
                                        <span class="paper">
                                            <span class="paperAuthors">
                                                <span class="sprekerName">
                                                    <xsl:value-of select="presenter" />
                                                </span>

                                                <xsl:if test="(organisation != '') and (organisation != 'null')">
                                                    <xsl:text> (</xsl:text><xsl:value-of select="organisation" /><xsl:text>)</xsl:text>
                                                </xsl:if>
                                                <xsl:if test="(copresenters != '') and (copresenters != 'null')">
                                                    <xsl:text>, </xsl:text>
                                                    <span class="coSprekerName">
                                                        <xsl:value-of select="copresenters" />
                                                    </span>
                                                </xsl:if>
                                            </span>
                                        </span>
                                    </xsl:for-each>
                                </xsl:if>
                            </span>
                            <br />
                            <br />

                            <span class="sessionAbstract">
                                <xsl:value-of select="sessionabstract" />
                                <br />
                            </span>
						</span>
					</xsl:for-each>
				<br />
				</span>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
