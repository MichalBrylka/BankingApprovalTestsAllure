Feature: End-of-Year Interest Calculations
  As a backend banking system
  I want to calculate annual interest rates based on account types
  So that I can generate compliant JSON and XML reports for auditing.

  Background:
    # This runs before every scenario to ensure the system is ready
    Given the banking engine is initialized

  @JSON @Premium
  Scenario: Generate approved JSON report for a Premium Account
    Given a "PREMIUM" account with 10000.00
    When the calculation is performed
    Then the JSON matches "expected_premium_interest.json" ignoring dynamic fields

  @XML @Standard
  Scenario: Generate approved XML report for a Standard Account
    Given a "STANDARD" account with 5000.00
    When the calculation is performed
    Then the XML matches "expected_standard_interest.xml" ignoring dynamic fields

  @BulkCheck
  Scenario Outline: Verify calculations for various balances and account types
    Given a "<type>" account with <initial_balance>
    When the calculation is performed
    Then the internal report balance should be <expected_total>

    Examples:
      | type     | initial_balance | expected_total |
      | PREMIUM  | 1000.00         | 1050.00        |
      | PREMIUM  | 20000.00        | 21000.00       |
      | STANDARD | 1000.00         | 1020.00        |
      | STANDARD | 0.00            | 0.00           |