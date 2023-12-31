package dev.tandosid.cleancode.kotlin

import fitnesse.responders.run.SuiteResponder
import fitnesse.wiki.PageCrawlerImpl
import fitnesse.wiki.PageData
import fitnesse.wiki.PathParser
import fitnesse.wiki.WikiPage


class TestableHtml {
    fun testableHtml(pageData: PageData, includeSuiteSetup: Boolean): String {
        return SetupTeardownSurrounder(pageData, includeSuiteSetup).surround()
    }
}

private class SetupTeardownSurrounder(private val pageData: PageData, private val includeSuiteSetup: Boolean) {

    val wikiPage: WikiPage = pageData.wikiPage
    var content = ""

    fun surround(): String {
        if (pageData.isTestPage()) {
            surroundPageWithSetupsAndTearDowns()
        }

        return pageData.html
    }

    private fun surroundPageWithSetupsAndTearDowns() {
        content += includeSetups()
        content += pageData.content
        content += includeTearDowns()
        pageData.content = content
    }

    private fun PageData.isTestPage() = hasAttribute("Test")

    private fun includeSetups(): String {
        var content = ""
        if (includeSuiteSetup) {
            content += includeIfInherited(SuiteResponder.SUITE_SETUP_NAME, "setup")
        }
        content += includeIfInherited("SetUp", "setup")
        return content
    }

    private fun includeTearDowns(): String {
        var content = includeIfInherited("TearDown", "teardown")
        if (includeSuiteSetup) {
            content += includeIfInherited(SuiteResponder.SUITE_TEARDOWN_NAME, "teardown")
        }
        return content
    }

    private fun includeIfInherited(pageName: String, mode: String): String {
        val page: WikiPage? = PageCrawlerImpl.getInheritedPage(pageName, wikiPage)
        if (page != null) {
            return includePage(page, mode)
        }
        return ""
    }

    private fun includePage(page: WikiPage?, mode: String): String {
        val path = wikiPage.pageCrawler.getFullPath(page)
        val pathName = PathParser.render(path)
        return "!include -$mode .$pathName\n"
    }

}
