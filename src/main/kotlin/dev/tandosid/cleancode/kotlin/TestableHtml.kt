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
                val suiteSetup: WikiPage? = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage)
                if (suiteSetup != null) {
                    includePage(suiteSetup, mode)
                }
            }
            val setup: WikiPage? = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage)
            if (setup != null) {
                includePage(setup, mode)
            }
        }
        buffer.append(pageData.content)
        if (pageData.hasAttribute("Test")) {
            val mode = "teardown"
            val teardown: WikiPage? = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage)
            if (teardown != null) {
                includePage(teardown, mode)
            }
            if (includeSuiteSetup) {
                val suiteTeardown: WikiPage? =
                    PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage)
                if (suiteTeardown != null) {
                    includePage(suiteTeardown, mode)
                }
            }
        }
        pageData.content = buffer.toString()
        return pageData.html
    }

    private fun includePage(page: WikiPage?, mode: String) {
        val path = wikiPage.pageCrawler.getFullPath(page)
        val pathName = PathParser.render(path)
        buffer.append("!include -$mode .$pathName\n")
    }

}
