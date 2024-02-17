<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <ul id="import">
            <li>
                <h3>Users import</h3>
                <ol>
                    <li>
                        <a href="#" class="import-open" data-submit="users">Import users</a>
                    </li>
                    <li>
                        <a href="#" class="import-open" data-submit="reviewers">Import reviewers</a>
                    </li>
                </ol>
            </li>
        </ul>

        <div id="import-dialog">
            <form method="post" enctype="multipart/form-data" action="">
                <fieldset class="form dialog">
                    <div>
                        <label class="property-label" for="csv">
                            <g:message code="default.import.csv.data" />
                        </label>
                        <span class="property-value">
                            <input type="file" name="csv" id="csv" />
                        </span>
                    </div>
                    <div>
                        <label class="property-label" for="sep">
                            <g:message code="default.sep.csv.data" />
                        </label>
                        <span class="property-value">
                            <select name="sep" id="sep">
                                <option value=",">,</option>
                                <option value=";">;</option>
                                <option value="tab">tab</option>
                            </select>
                        </span>
                    </div>
                </fieldset>
                <fieldset class="buttons">
                    <input type="submit" name="import" value="${g.message(code: 'default.import.menu.label')}" />
                </fieldset>
            </form>
        </div>
    </body>
</html>
