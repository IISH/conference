<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>${page.toString()}</title>
    </head>
    <body>
        <div id="filter-participant">
            <form name="filter-participant" action="#" method="get">
                <fieldset id="filter">
                    <div>
                        <label for="filter-date">
                            <g:message code="eventdate.label" />
                        </label>
                        <g:select from="${dates}" id="filter-date" name="filter-date" optionKey="${-2}" optionValue="${1}" value="${params['filter-date']}" />
                    </div>
                    <div>
                        <label for="filter-type">
                            <g:message code="default.button.searchin.label" />
                        </label>
                        <g:select id="filter-type" name="filter-type" valueMessagePrefix="search.for" from="['organisation', 'name', 'address']" value="${params['filter-type']}" />
                    </div>
                    <div>
                        <label for="filter-text">
                            <g:message code="default.button.search.label" />
                        </label>
                        <input type="text" id="filter-text" name="filter-text" value="${params['filter-text']}" />
                    </div>

                    <div class="buttons">
                        <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                            <g:message code="default.button.cancel.label" />
                        </eca:link>
                        <input type="submit" value="${message(code: 'default.button.search.label')}" />
                    </div>
                </fieldset>

                <fieldset id="status">
                    <table>
                        <g:each in="${states}" var="state" status="i">
                            <tr>
                                <td>
                                    <g:if test="${(params.int('filter-state') == state[0]) || (params['filter-state'] == null && i == 0)}">
                                        <input type="radio" name="filter-state" value="${state[0]}" checked="checked" />
                                    </g:if>
                                    <g:else>
                                        <input type="radio" name="filter-state" value="${state[0]}" />
                                    </g:else>
                                </td>
                                <td>
                                    ${state[1]}
                                </td>
                                <td>
                                    ${state[2]}
                                </td>
                            </tr>
                        </g:each>
                    </table>
                </fieldset>

                <div class="empty clear"></div>
            </form>
        </div>
        <div id="participants">
            <div class="alfabet">
                <g:each in="${alfabet}" var="character">
                    <a href="#${character.toLowerCase()}">${character}</a>
                </g:each>
            </div>

            <div id="list">
                <g:each in="${alfabet}" var="character">
                    <ol name="${character.toLowerCase()}">
                        <li class="char">${character}</li>
                        <g:each in="${participants.get(character)}" var="user">
                            <li><eca:link action="show" id="${user[0]}">${user[1]}</eca:link></li>
                        </g:each>
                    </ol>
                </g:each>
            </div>

            <div class="alfabet">
                <g:each in="${alfabet}" var="character">
                    <a href="#${character.toLowerCase()}">${character}</a>
                </g:each>
            </div>
        </div>
    </body>
</html>
