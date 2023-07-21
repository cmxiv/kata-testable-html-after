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
                    val pagePath = wikiPage.pageCrawler.getFullPath(suiteSetup)
                    val pagePathName = PathParser.render(pagePath)
                    buffer.append("!include -$mode .").append(pagePathName).append("\n")
                }
            }
            val setup: WikiPage? = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage)
            if (setup != null) {
                val setupPath = wikiPage.pageCrawler.getFullPath(setup)
                val setupPathName = PathParser.render(setupPath)
                buffer.append("!include -$mode .").append(setupPathName).append("\n")
            }
        }
        buffer.append(pageData.content)
        if (pageData.hasAttribute("Test")) {
            val mode = "teardown"
            val teardown: WikiPage? = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage)
            if (teardown != null) {
                val tearDownPath = wikiPage.pageCrawler.getFullPath(teardown)
                val tearDownPathName = PathParser.render(tearDownPath)
                buffer.append("!include -$mode .").append(tearDownPathName).append("\n")
            }
            if (includeSuiteSetup) {
                val suiteTeardown: WikiPage? =
                    PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage)
                if (suiteTeardown != null) {
                    val pagePath = wikiPage.pageCrawler.getFullPath(suiteTeardown)
                    val pagePathName = PathParser.render(pagePath)
                    buffer.append("!include -$mode .").append(pagePathName).append("\n")
                }
            }
        }
        pageData.content = buffer.toString()
        return pageData.html
    }

}
