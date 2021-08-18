<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/data">
		<html>
			<head>
			<style>
				th {
					font-weight: bold;
					text-align: left;
				}
			</style>
			</head>
			<body>
			<table>
				<tr>
					<th colspan="7">Program at a glance (<xsl:value-of select="exportDate" />)</th>
				</tr>
				<tr>
					<th>Day</th>
					<th>Period</th>
					<th>Room name</th>
					<th>Room number</th>
					<th>Session code</th>
					<th>Session name</th>
					<th>Paper</th>
				</tr>

				<xsl:for-each select="sessions/session">

				<tr>
					<td><xsl:value-of select="../time/weekday"/> <xsl:text> </xsl:text> <xsl:value-of select="../time/day"/> <xsl:text> </xsl:text> <xsl:value-of select="../time/month"/></td>
					<td><xsl:value-of select="../time/starttime"/> - <xsl:value-of select="../time/endtime"/></td>
					<td><xsl:value-of select="location/locationname"/></td>
					<td><xsl:value-of select="location/code"/></td>
					<td><xsl:value-of select="sessioncode"/></td>
					<td><xsl:value-of select="sessionname"/></td>
					<td></td>
				</tr>

					<xsl:for-each select="papers/paper">
					<tr>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td><xsl:value-of select="subject"/></td>
					</tr>
					</xsl:for-each>
				</xsl:for-each>
			</table>

			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
