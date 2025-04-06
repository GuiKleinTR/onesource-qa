Feature: Onesource QA

  Scenario Outline: Create User

    Given the user navigates to onesource login page
    And user types username & password and click login button
    Then Onesource homepage is displayed
    When the user navigates to the user creation page
    And enters "<id>", "<name>", "<email>", "<password>", and selects "<usertype>"
 
Examples:
    | id               | name       | email                                | password   | usertype      |
    | klein_test_123   | John Doe   | guilherme.klein@thomsonreuters.com   | Pass@1234  | Regular User  |