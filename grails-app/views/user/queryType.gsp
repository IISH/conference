<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
<div class="tbl_container">
    <input type="hidden" name="url" value="${eca.createLink(controller: 'participant', action: 'show', id: 0)}" />

    <ol class="property-list margin">
        <li>
            <span id="queryType-label" class="property-label">
                <g:message code="default.queryType" />
            </span>
            <span class="property-value" arial-labelledby="queryType-label">
                <form name="queryType-form" action="#" method="get">
                    <g:select id="query-type-select" name="q" from="${queryTypes}" value="${params.q}" />
                </form>
            </span>
        </li>
    </ol>

    <div class="menu">
        <ul>
            <li><a href="">Open link</a></li>
            <li><a href="" target="_blank">Open link in new tab</a></li>
        </ul>
    </div>

    <table class="clear">
        <thead class="no-filters">
        <tr>
            <th class="counter"></th>
            <th class="id">#</th>
            <th><g:message code="user.lastName.label" /></th>
            <th><g:message code="user.firstName.label" /></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${users}" var="row" status="i">
            <tr>
                <td class="counter">${i+1}</td>
                <td class="id">${row[0]}</td>
                <td>${row[1]}</td>
                <td>${row[2]}</td>
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