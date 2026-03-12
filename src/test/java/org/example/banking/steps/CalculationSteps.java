package org.example.banking.steps;

import io.qameta.allure.Allure;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.*;
import org.apache.commons.io.IOUtils;
import org.example.banking.*;
import org.skyscreamer.jsonassert.*;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class CalculationSteps {

    private BankingService service;
    private Report lastReport;
    private String accountType;
    private double balance;


    @Before
    public void setUp() {
        // Reset our domain objects before every scenario
        this.lastReport = null;
        this.accountType = null;
        this.balance = 0.0;
        System.out.println("--- Starting fresh calculation scenario ---");
    }

    @After
    public void tearDown(Scenario scenario) {
        // This runs after every scenario
        if (scenario.isFailed()) {
            System.out.println("Scenario failed: " + scenario.getName());
            // In a real project, you might attach a screenshot or log file here
        }
        System.out.println("--- Calculation scenario complete ---");
    }

    @Given("the banking engine is initialized")
    public void the_banking_engine_is_initialized() {
        service = new BankingService();
    }

    @Given("a {string} account with {double}")
    public void setAccount(String type, double bal) {
        this.accountType = type;
        this.balance = bal;
    }

    @When("the calculation is performed")
    public void performCalc() {
        this.lastReport = service.calculateInterest(accountType, balance);
    }

    @Then("the JSON matches {string} ignoring dynamic fields")
    public void assertJson(String fileName) throws Exception {
        String expected = loadApprovedFile(fileName);

        // Convert POJO to JSON (In a real app, use Jackson ObjectMapper)
        String actualJson = String.format(Locale.ROOT,
                "{\"accountType\":\"%s\", \"balance\":%.2f, \"transactionId\":\"%s\", \"timestamp\":\"%s\"}",
                lastReport.accountType(), lastReport.balance(),
                lastReport.transactionId(), lastReport.timestamp()
        );

        Allure.addAttachment("Expected JSON (from " + fileName + ")", "application/json", expected);
        Allure.addAttachment("Actual JSON (Generated)", "application/json", actualJson);


        // Ignore transactionId and timestamp values
        CustomComparator customComparator = new CustomComparator(JSONCompareMode.STRICT,
                new Customization("transactionId", (a, e) -> true),
                new Customization("timestamp", (a, e) -> true)
        );

        JSONAssert.assertEquals(expected, actualJson, customComparator);
    }

    @Then("the XML matches {string} ignoring dynamic fields")
    public void assertXml(String fileName) throws Exception {
        String expected = loadApprovedFile(fileName);

        // Convert POJO to XML string
        String actualXml = String.format(Locale.ROOT,
                "<report><accountType>%s</accountType><balance>%.2f</balance>" +
                        "<transactionId>%s</transactionId><timestamp>%s</timestamp></report>",
                lastReport.accountType(), lastReport.balance(),
                lastReport.transactionId(), lastReport.timestamp()
        );

        DifferenceEvaluator evaluator = (comparison, outcome) -> {
            if (outcome == ComparisonResult.DIFFERENT) {
                String nodeName = comparison.getTestDetails().getTarget().getParentNode().getNodeName();
                if ("transactionId".equals(nodeName) || "timestamp".equals(nodeName)) {
                    return ComparisonResult.EQUAL;
                }
            }
            return outcome;
        };

        Diff diff = DiffBuilder.compare(expected).withTest(actualXml)
                .withDifferenceEvaluator(evaluator)
                .ignoreWhitespace().build();

        assertFalse(diff. hasDifferences(), diff.toString());
    }

    @Then("the internal report balance should be {double}")
    public void the_internal_report_balance_should_be(Double expectedBalance) {
        // Ensure the report actually exists before checking it
        assertNotNull(lastReport, "The report was not generated!");

        // Use a delta of 0.001 to handle minor floating point variations
        assertEquals(expectedBalance, lastReport.balance(), 0.001,
                String.format("Expected balance %s but got %s", expectedBalance, lastReport.balance()));
    }

    /**
     * Helper method to load files from src/test/resources/approved/
     */
    private String loadApprovedFile(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("approved/" + fileName)) {
            if (is == null) {
                fail("Could not find approved file: " + fileName);
            }
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (Exception e) {
            fail("Error reading approved file: " + e.getMessage());
            return null; // unreachable
        }
    }
}

