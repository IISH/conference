package org.iisg.eca.controller

import org.apache.commons.io.IOUtils
import org.iisg.eca.domain.Setting

class CssController {
    def servletContext

    def css() {
        InputStream is = servletContext.getResourceAsStream("/css/default.css")
        if (is == null) {
            response.sendError(404)
            return
        }

        String css = IOUtils.toString(is, 'UTF-8')

        String bannerImg = Setting.getSetting(Setting.BANNER_IMG).value
        if (!bannerImg.startsWith("http")) {
            bannerImg = g.resource(dir: 'images', file: bannerImg)
        }
        css = css.replace("[${Setting.BANNER_IMG}]", bannerImg)

        String bannerBgImg = Setting.getSetting(Setting.BANNER_BG_IMG).value
        if (!bannerImg.startsWith("http")) {
            bannerBgImg = g.resource(dir: 'images', file: bannerBgImg)
        }
        css = css.replace("[${Setting.BANNER_BG_IMG}]", bannerBgImg)

        String labelColor = Setting.getSetting(Setting.LABEL_COLOR).value
        css = css.replace("[${Setting.LABEL_COLOR}]", labelColor)

        String mainColorLight = Setting.getSetting(Setting.MAIN_COLOR_LIGHT).value
        css = css.replace("[${Setting.MAIN_COLOR_LIGHT}]", mainColorLight)

        String mainColorDark = Setting.getSetting(Setting.MAIN_COLOR_DARK).value
        css = css.replace("[${Setting.MAIN_COLOR_DARK}]", mainColorDark)

        String mainColorBg = Setting.getSetting(Setting.MAIN_COLOR_BG).value
        css = css.replace("[${Setting.MAIN_COLOR_BG}]", mainColorBg)

        css = css.replace("[resource_dir]", g.resource(dir: 'assets'))

        render(text: css, contentType: "text/css")
    }
}
