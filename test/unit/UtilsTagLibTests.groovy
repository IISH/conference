import grails.test.mixin.TestFor

import org.junit.Before
import org.iisg.eca.tags.UtilsTagLib
import org.codehaus.groovy.grails.commons.UrlMappingsArtefactHandler

/**
 * Tests some of the tags from the tag library with all custom made utility tags
 */
@TestFor(UtilsTagLib)
class UtilsTagLibTests {
    /**
     * Creates a default page information bean for testing and sets up the parameter map
     */
    @Before
    void setUp() {
        params.event = 'test'
        params.date = 'juli-3000'

        params.prevController = 'prevCo'
        params.prevAction = 'prevAc'
        params.prevId = 9L

        params.controller = 'co'
        params.action = 'ac'
        params.id = 10L

        // Make sure the URL mappings are also used for testing link tags
        grailsApplication.addArtefact(UrlMappingsArtefactHandler.TYPE,
                new GroovyClassLoader(getClass().classLoader).parseClass(new File("grails-app/conf/UrlMappings.groovy")))
    }

    /**
     * Tests the formatText tag
     */
    void testFormatTextTag() {
        String firstText = "Aaaaa \n Bbbbb \n"
        String secondText = "Aaaaa \n \t Bbbbb"

        assert applyTemplate('<eca:formatText text="${text}" />', [text: firstText]) == "Aaaaa <br /> Bbbbb <br />"
        assert applyTemplate('<eca:formatText text="${text}" />', [text: secondText]) == "Aaaaa <br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Bbbbb"
        assert applyTemplate('<eca:formatText text="${text}" />', [text: "     "]) == "-"
        assert applyTemplate('<eca:formatText text="${text}" />', [text: null]) == "-"
    }

    /**
     * Tests the link tag
     */
    /*void testLinkTag() {
        String firstTemplate = '<eca:link controller="${controller}" action="${action}" id="${id}">link</eca:link>'
        String secondTemplate = '<eca:link controller="${controller}" action="${action}" event="${event}" date="${date}">link</eca:link>'
        String thirdTemplate = '<eca:link previous="${previous}">link</eca:link>'
        String fourthTemplate = '<eca:link noBase="${noBase}" controller="${controller}" action="${action}" id="${id}">link</eca:link>'

        assert applyTemplate(firstTemplate, [controller: "co2", action: "ac2", id: 11L]) ==
                '<a href="/test/juli-3000/co2/ac2/11?prevController=co&amp;prevAction=ac&amp;prevId=10">link</a>'
        assert applyTemplate(secondTemplate, [controller: params.controller, action: params.action, event: "event", date: "date"]) ==
                '<a href="/event/date/co/ac?prevController=co&amp;prevAction=ac&amp;prevId=10">link</a>'
        assert applyTemplate(thirdTemplate, [previous: true]) ==
                '<a href="/test/juli-3000/prevCo/prevAc/9?prevController=co&amp;prevAction=ac&amp;prevId=10">link</a>'
        assert applyTemplate(fourthTemplate, [noBase: true, controller: "co2", action: "ac2", id: 11L]) ==
                '<a href="/./test/juli-3000/co2/ac2/11?prevController=co&amp;prevAction=ac&amp;prevId=10">link</a>'
    } */

    /**
     * Tests the createLink tag
     */
    /*void testCreateLinkTag() {
        String firstTemplate = '<eca:createLink controller="${controller}" action="${action}" id="${id}" />'
        String secondTemplate = '<eca:createLink controller="${controller}" action="${action}" event="${event}" date="${date}" />'
        String thirdTemplate = '<eca:createLink previous="${previous}" />'
        String fourthTemplate = '<eca:createLink noBase="${noBase}" controller="${controller}" action="${action}" id="${id}" />'

        assert applyTemplate(firstTemplate, [controller: "co2", action: "ac2", id: 11L]) ==
                '/test/juli-3000/co2/ac2/11?prevController=co&prevAction=ac&prevId=10'
        assert applyTemplate(secondTemplate, [controller: params.controller, action: params.action, event: "event", date: "date"]) ==
                '/event/date/co/ac?prevController=co&prevAction=ac&prevId=10'
        assert applyTemplate(thirdTemplate, [previous: true]) ==
                '/test/juli-3000/prevCo/prevAc/9?prevController=co&prevAction=ac&prevId=10'
        assert applyTemplate(fourthTemplate, [noBase: true, controller: "co2", action: "ac2", id: 11L]) ==
                '/./test/juli-3000/co2/ac2/11?prevController=co&prevAction=ac&prevId=10'
    }*/

    /**
     * Tests the fallbackMessage tag
     */
    void testFallbackMessageTag() {
        UtilsTagLib taglib = applicationContext.getBean(UtilsTagLib)

        // Simulate the standard message tag
        taglib.metaClass.message = { Map map -> return (map.code == 'default.home.label') ? 'Home' : map.code }

        assert applyTemplate('<eca:fallbackMessage code="default.home.label" fbCode="does.not.exist" />') == 'Home'
        assert applyTemplate('<eca:fallbackMessage code="does.not.exist" fbCode="default.home.label" />') == 'Home'
        assert applyTemplate('<eca:fallbackMessage code="default.welcome.label" fbCode="default.hello.label" />') == 'default.hello.label'
    }
}
