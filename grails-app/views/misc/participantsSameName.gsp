<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <h3>${info}</h3>

        <div class="tbl_container">
            <table class="clear">
                <thead class="no-filters">
                    <tr>
                        <th class="counter"></th>
                        <th>Participant A</th>
                        <th>Participant B</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${data}" var="row" status="i">
                        <tr>
                            <td class="counter">${i+1}</td>
                            <td>
                                <eca:link controller="${controller}" action="${action}" id="${row[0]}">${row[1]}, ${row[2]}</eca:link>
                            </td>
                            <td>
                                <eca:link controller="${controller}" action="${action}" id="${row[3]}">${row[4]}, ${row[5]}</eca:link>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
             </table>
        </div>

        <div class="buttons">
            <eca:link previous="true">
                <g:message code="default.button.back.label" />
            </eca:link>
        </div>
    </body>
</html>