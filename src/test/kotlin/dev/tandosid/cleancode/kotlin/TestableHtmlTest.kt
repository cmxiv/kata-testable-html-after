package dev.tandosid.cleancode.kotlin

import fitnesse.wiki.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class TestableHtmlTest {
    private var pageData: PageData? = null
    private var crawler: PageCrawler? = null
    private var root: WikiPage? = null
    private var testPage: WikiPage? = null

    @Language("html")
    private val expectedResultForTestCase =
        "<div class=\"setup\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Set Up: <a href=\"SuiteSetUp\">.SuiteSetUp</a> <a href=\"SuiteSetUp?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">suiteSetUp</div>\n</div>\n<div class=\"setup\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Set Up: <a href=\"SetUp\">.SetUp</a> <a href=\"SetUp?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">setup</div>\n</div>\n<span class=\"meta\">variable defined: TEST_SYSTEM=slim</span><br/>the content!include -teardown <a href=\"TearDown\">.TearDown</a><br/><div class=\"teardown\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Tear Down: <a href=\"SuiteTearDown\">.SuiteTearDown</a> <a href=\"SuiteTearDown?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">suiteTearDown</div>\n</div>\n"

    @Language("html")
    private val expectedResultForNonTestCase =
        "<div class=\"setup\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Set Up: <a href=\"SetUp\">.SetUp</a> <a href=\"SetUp?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">setup</div>\n</div>\n<div class=\"setup\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Set Up: <a href=\"SuiteSetUp\">.SuiteSetUp</a> <a href=\"SuiteSetUp?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">suiteSetUp</div>\n</div>\n<div class=\"setup\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Set Up: <a href=\"SetUp\">.SetUp</a> <a href=\"SetUp?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">setup</div>\n</div>\n<span class=\"meta\">variable defined: TEST_SYSTEM=slim</span><br/>the content!include -teardown <a href=\"TearDown\">.TearDown</a><br/><div class=\"teardown\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Tear Down: <a href=\"SuiteTearDown\">.SuiteTearDown</a> <a href=\"SuiteTearDown?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">suiteTearDown</div>\n</div>\n<div class=\"teardown\">\n\t<div style=\"float: right;\" class=\"meta\"><a href=\"javascript:expandAll();\">Expand All</a> | <a href=\"javascript:collapseAll();\">Collapse All</a></div>\n\t<a href=\"javascript:toggleCollapsable('');\">\n\t\t<img src=\"/files/images/collapsableOpen.gif\" class=\"left\" id=\"img\"/>\n\t</a>\n&nbsp;<span class=\"meta\">Tear Down: <a href=\"TearDown\">.TearDown</a> <a href=\"TearDown?edit&amp;redirectToReferer=true&amp;redirectAction=\">(edit)</a></span>\n\t<div class=\"collapsable\" id=\"\">teardown</div>\n</div>\n"

    @BeforeEach
    fun setUp() {
        root = InMemoryPage.makeRoot("RooT")
        crawler = root!!.pageCrawler
        testPage = addPage(
            "TestPage",
            """
            !define TEST_SYSTEM {slim}
            the content
            """.trimIndent()
        )
        addPage("SetUp", "setup")
        addPage("TearDown", "teardown")
        addPage("SuiteSetUp", "suiteSetUp")
        addPage("SuiteTearDown", "suiteTearDown")
        crawler!!.addPage(
            testPage, PathParser.parse("ScenarioLibrary"),
            "scenario library 2"
        )
        pageData = testPage!!.data
    }

    private fun addPage(pageName: String, content: String): WikiPage {
        return crawler!!.addPage(root, PathParser.parse(pageName), content)
    }

    private fun removeMagicNumber(expectedResult: String): String {
        return expectedResult.replace("[-]*\\d+".toRegex(), "")
    }

    @Test
    fun testableHtml() {
        generateHtmlAndAssert(true, expectedResultForTestCase)
        generateHtmlAndAssert(false, expectedResultForNonTestCase)
    }

    private fun generateHtmlAndAssert(
        includeSuiteSetup: Boolean,
        expectedResult: String
    ) {
        val testableHtml = TestableHtml().testableHtml(pageData!!, includeSuiteSetup)
        assertThat(removeMagicNumber(testableHtml), `is`(removeMagicNumber(expectedResult)))
    }
}