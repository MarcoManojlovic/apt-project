Feature: Patient View High Level
  Specifications of the behavior of the Patient View

  Background: 
    Given The database contains a few patients
    And The Patient View is shown

  Scenario: Add a new patient
    Given The user provides patient data in the text fields
    When The user clicks the "Add" button
    Then The list contains the new patient

  Scenario: Add a new patient with an existing id
    Given The user provides patient data in the text fields, specifying an existing id
    When The user clicks the "Add" button
    Then An error is shown containing the name of the existing patient

  Scenario: Delete a patient
    Given The user selects a patient from the list
    When The user clicks the "Delete Selected" button
    Then The patient is removed from the list

  Scenario: Delete a not existing patient
    Given The user selects a patient from the list
    But The patient is in the meantime removed from the database
    When The user clicks the "Delete Selected" button
    Then An error is shown containing the name of the selected patient
    And The patient is removed from the list

  Scenario: Search a patient
    Given The user provides a name in the search name field, specifying an existing name
    When The user clicks the "Search" button
    Then The search list contains the searched patient

  Scenario: Search a not existing patient
    Given The user provides a name in the search name field, specifying a not existing name
    When The user clicks the "Search" button
    Then An error is shown containing the searched name
