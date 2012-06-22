<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <div id="filter-participant">
            <form name="filter-participant" action="#" method="get">
                <div id="filter">
                    <fieldset class="form">
                        <div>
                            <label class="property-label" for="filter-type">
                                <g:message code="default.button.searchin.label" />
                            </label>
                            <g:select class="property-value" id="filter-type" name="filter-type" valueMessagePrefix="search.for" from="['name', 'organisation', 'address']" value="${params['filter-type']}" />
                        </div>
                        <div>
                            <label class="property-label" for="filter-text">
                                <g:message code="default.button.search.label" />
                            </label>
                            <input class="property-value" type="text" id="filter-text" name="filter-text" value="${params['filter-text']}" />
                        </div>
                    </fieldset>

                    <fieldset class="buttons">
                        <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                            <g:message code="default.button.cancel.label" />
                        </eca:link>
                        <input type="submit" value="${message(code: 'default.button.search.label')}" />
                    </fieldset>
                </div>

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
                <ol>
                  <g:if test="${!params['filter-type']}">
                      <li><g:message code="default.search.help.message" /></li>
                  </g:if>
                  <g:elseif test="${participants.isEmpty()}">
                      <li><g:message code="default.search.nothing.found.message" /></li>
                  </g:elseif>
                </ol>   
              
                <g:each in="${alfabet}" var="character">
                    <ol>
                        <li class="char"><a name="${character.toLowerCase()}">${character}</a></li>
                        <g:each in="${participants.get(character)}" var="user">
                            <li>
                                <eca:ifUserHasAccess controller="${params.controller}" action="show">
                                    <eca:linkAllParams controller="${params.controller}" action="show" id="${user[0]}">${user[1]}</eca:linkAllParams>
                                    ${user[2]}
                                </eca:ifUserHasAccess>
                                <eca:ifUserHasNoAccess controller="${params.controller}" action="show">
                                    ${user[1]} ${user[2]}
                                </eca:ifUserHasNoAccess>
                            </li>
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
