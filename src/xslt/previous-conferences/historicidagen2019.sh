#!/bin/bash

xsltproc historicidagen2019.xslt historicidagen2019.xml > historicidagen2019.html

sed -i 's/Thursday 22 August/donderdag 22 augustus/g' historicidagen2019.html
sed -i 's/Friday 23 August/vrijdag 23 augustus/g' historicidagen2019.html
sed -i 's/Saturday 24 August/zaterdag 24 augustus/g' historicidagen2019.html
