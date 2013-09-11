package org.iisg.eca.controller

import org.iisg.eca.domain.Setting

class CssController {
    def css() {
        String path = servletContext.getRealPath("css/default.css")
        File cssFile = new File(path)

        if (!cssFile.exists()) {
            response.sendError(404)
            return
        }

        String css = cssFile.text

        String bannerImg = Setting.getByEvent(Setting.findAllByProperty(Setting.BANNER_IMG, [cache: true])).value
        if (!bannerImg.startsWith("http")) {
            bannerImg = g.resource(dir: 'images', file: bannerImg)
        }
        css = css.replace("[${Setting.BANNER_IMG}]", bannerImg)

        String bannerBgImg = Setting.getByEvent(Setting.findAllByProperty(Setting.BANNER_BG_IMG, [cache: true])).value
        if (!bannerImg.startsWith("http")) {
            bannerBgImg = g.resource(dir: 'images', file: bannerBgImg)
        }
        css = css.replace("[${Setting.BANNER_BG_IMG}]", bannerBgImg)

        String labelColor = Setting.getByEvent(Setting.findAllByProperty(Setting.LABEL_COLOR, [cache: true])).value
        css = css.replace("[${Setting.LABEL_COLOR}]", labelColor)

        String mainColorLight = Setting.getByEvent(Setting.findAllByProperty(Setting.MAIN_COLOR_LIGHT, [cache: true])).value
        css = css.replace("[${Setting.MAIN_COLOR_LIGHT}]", mainColorLight)

        String mainColorDark = Setting.getByEvent(Setting.findAllByProperty(Setting.MAIN_COLOR_DARK, [cache: true])).value
        css = css.replace("[${Setting.MAIN_COLOR_DARK}]", mainColorDark)

        String mainColorBg = Setting.getByEvent(Setting.findAllByProperty(Setting.MAIN_COLOR_BG, [cache: true])).value
        css = css.replace("[${Setting.MAIN_COLOR_BG}]", mainColorBg)

        css = css.replace("[resource_dir]", g.resource(dir: 'images'))

        render(text: css, contentType: "text/css")
    }
}
