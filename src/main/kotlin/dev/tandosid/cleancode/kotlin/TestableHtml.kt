package dev.tandosid.cleancode.kotlin

import fitnesse.responders.run.SuiteResponder
import fitnesse.wiki.PageCrawlerImpl
import fitnesse.wiki.PageData
import fitnesse.wiki.PathParser
import fitnesse.wiki.WikiPage


class TestableHtml {
    fun testableHtml(pageData: PageData, includeSuiteSetup: Boolean): String {
        return TestableHtmlMaker(pageData, includeSuiteSetup).make()
    }
}

private class TestableHtmlMaker(private val pageData: PageData, private val includeSuiteSetup: Boolean) {

    val wikiPage: WikiPage = pageData.wikiPage
    val buffer = StringBuffer()

    fun make(): String {
        if (pageData.hasAttribute("Test")) {
            val mode = "setup"
            if (includeSuiteSetup) {
                includeIfInherited(SuiteResponder.SUITE_SETUP_NAME, mode)
            }
            includeIfInherited("SetUp", mode)
        }
        buffer.append(pageData.content)
        if (pageData.hasAttribute("Test")) {
            val mode = "teardown"
            includeIfInherited("TearDown", mode)
            if (includeSuiteSetup) {
                includeIfInherited(SuiteResponder.SUITE_TEARDOWN_NAME, mode)
            }
        }
        pageData.content = buffer.toString()
        return pageData.html
    }

    private fun includeIfInherited(pageName: String, mode: String) {
        val page: WikiPage? = PageCrawlerImpl.getInheritedPage(pageName, wikiPage)
        if (page != null) {
            includePage(page, mode)
        }
    }

    private fun includePage(page: WikiPage?, mode: String) {
        val path = wikiPage.pageCrawler.getFullPath(page)
        val pathName = PathParser.render(path)
        buffer.append("!include -$mode .$pathName\n")
    }

}
