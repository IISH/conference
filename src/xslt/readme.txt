1. download the program book export (xml file)

2. combine the xml and xsl file using xsltproc

example
#!/bin/bash
xsltproc XSLFILE.xslt SOURCEXML.xml > OUTPUT.html

3. open the output file in Word/Excel...
and save it as a original Word/Excel file

example:
xsltproc euroseas2022.xslt euroseas2022-sessions.xml > euroseas2022.html

You can now open the html file in Word and save as a Word document.
