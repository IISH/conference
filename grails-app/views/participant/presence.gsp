<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
<div class="tbl_container">
    <input type="hidden" name="url" value="${eca.createLink(controller: 'participant', action: 'show', id: 0)}" />

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
            <th class="id hidden"></th>

            <th><g:message code="user.lastName.label" /></th>
            <th><g:message code="user.firstName.label" /></th>

            <g:each in="${days}" var="day">
                <th>${day.toString()}</th>
            </g:each>

            <g:each in="${extras}" var="extra">
                <th>${extra.toString()}</th>
            </g:each>
        </tr>
        </thead>
        <tbody>
            <g:each in="${overview}" var="row" status="i">
                <tr>
                    <td class="counter">${i+1}</td>
                    <td class="id hidden">${row.get('user_id')}</td>

                    <td>${row.get('lastname')}</td>
                    <td>${row.get('firstname')}</td>

                    <g:set var="dayIds" value="${row.get('days')?.split(',')}" />
                    <g:each in="${days}" var="day">
                        <g:if test="${dayIds != null && dayIds.contains(day.id.toString())}">
                            <th><span class="ui-icon ui-icon-check" /></th>
                        </g:if>
                        <g:else>
                            <th>&nbsp;</th>
                        </g:else>
                    </g:each>

                    <g:set var="extraIds" value="${row.get('extras')?.split(',')}" />
                    <g:each in="${extras}" var="extra">
                        <g:if test="${extraIds != null && extraIds.contains(extra.id.toString())}">
                            <th><span class="ui-icon ui-icon-check" /></th>
                        </g:if>
                        <g:else>
                            <th>&nbsp;</th>
                        </g:else>
                    </g:each>
                </tr>
            </g:each>

            <tr class="tbl_totals">
                <td>&nbsp;</td>
                <td class="hidden">&nbsp;</td>

                <td>&nbsp;</td>
                <td>&nbsp;</td>

                <g:each in="${days}" var="day">
                    <g:if test="${daysCount.containsKey(day.id)}">
                        <th>${daysCount.get(day.id)}</th>
                    </g:if>
                    <g:else>
                        <th>0</th>
                    </g:else>
                </g:each>

                <g:each in="${extras}" var="extra">
                    <g:if test="${extrasCount.containsKey(extra.id)}">
                        <th>${extrasCount.get(extra.id)}</th>
                    </g:if>
                    <g:else>
                        <th>0</th>
                    </g:else>
                </g:each>
            </tr>
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