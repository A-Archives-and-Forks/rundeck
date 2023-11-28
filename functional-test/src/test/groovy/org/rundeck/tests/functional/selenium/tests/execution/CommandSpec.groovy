package org.rundeck.tests.functional.selenium.tests.execution

import org.rundeck.tests.functional.selenium.pages.execution.CommandPage
import org.rundeck.tests.functional.selenium.pages.execution.ExecutionShowPage
import org.rundeck.tests.functional.selenium.pages.login.LoginPage
import org.rundeck.util.annotations.SeleniumCoreTest
import org.rundeck.util.container.SeleniumBase

@SeleniumCoreTest
class CommandSpec extends SeleniumBase {

    def setupSpec() {
        setupProject("SeleniumBasic", "/projects-import/SeleniumBasic.zip")
    }

    def setup() {
        def loginPage = go LoginPage
        loginPage.login(TEST_USER, TEST_PASS)
    }

    def "abort button in commands page"() {
        when:
            def commandPage = go CommandPage, "SeleniumBasic"
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            sleep 5000
            commandPage.filterNodeButton.click()
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test && sleep 45"
            commandPage.runButton.click()
            sleep 5000
            commandPage.runningExecutionStateButton.getAttribute("data-execstate") == 'RUNNING'
        expect:
            commandPage.abortButton.click()
            sleep 5000
            commandPage.runningExecutionStateButton.getAttribute("data-execstate") == 'ABORTED'
    }

    def "abort button in show page"() {
        when:
            def commandPage = go CommandPage, "SeleniumBasic"
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            sleep 5000
            commandPage.filterNodeButton.click()
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test && sleep 45"
            commandPage.runButton.click()
            sleep 2000
            commandPage.runningButtonLink().click()
        expect:
            def executionShowPage = page ExecutionShowPage
            executionShowPage.executionStateDisplayLabel.getAttribute('data-execstate') == 'RUNNING'

            executionShowPage.abortButton.click()
            sleep 5000
            executionShowPage.executionStateDisplayLabel.getAttribute('data-execstate') == 'ABORTED'
    }

    def "default page load shows nodes view"() {
        when:
            def commandPage = go CommandPage, "SeleniumBasic"
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            sleep 5000
            commandPage.filterNodeButton.click()
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            sleep 1000
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href
        expect:
            def executionShowPage = page ExecutionShowPage
            executionShowPage.validatePage()
            executionShowPage.executionStateDisplayLabel.getAttribute('data-execstate') != null
            executionShowPage.viewContentNodes.isDisplayed()
            !executionShowPage.viewButtonNodes.isDisplayed()
            executionShowPage.viewButtonOutput.isDisplayed()
    }

    def "fragment #output page load shows output view"() {
        when:
            def commandPage = go CommandPage, "SeleniumBasic"
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            sleep 5000
            commandPage.filterNodeButton.click()
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            sleep 1000
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href + "#output"
        expect:
            def executionShowPage = page ExecutionShowPage
            executionShowPage.validatePage()
            executionShowPage.executionStateDisplayLabel.getAttribute('data-execstate') != null
            executionShowPage.viewContentOutput.isDisplayed()
            executionShowPage.viewButtonNodes.isDisplayed()
            !executionShowPage.viewButtonOutput.isDisplayed()
    }

    def "output view toggle to nodes view with button"() {
        when:
            def commandPage = go CommandPage, "SeleniumBasic"
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            sleep 5000
            commandPage.filterNodeButton.click()
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            sleep 1000
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href + "#output"
        expect:
            def executionShowPage = page ExecutionShowPage
            executionShowPage.validatePage()
            executionShowPage.viewButtonNodes.isDisplayed()
            executionShowPage.viewButtonNodes.click()

            executionShowPage.viewContentNodes.isDisplayed()
            !executionShowPage.viewButtonNodes.isDisplayed()
            executionShowPage.viewButtonOutput.isDisplayed()
    }

    def "nodes view toggle to output view with button"() {
        when:
            def commandPage = go CommandPage, "SeleniumBasic"
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            sleep 5000
            commandPage.filterNodeButton.click()
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            sleep 5000
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href
        expect:
            def executionShowPage = page ExecutionShowPage
            executionShowPage.validatePage()
            executionShowPage.viewButtonOutput.isDisplayed()
            executionShowPage.viewButtonOutput.click()

            executionShowPage.viewContentOutput.isDisplayed()
            !executionShowPage.viewButtonOutput.isDisplayed()
            executionShowPage.viewButtonNodes.isDisplayed()
    }

}
