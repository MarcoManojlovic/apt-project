package com.manojlovic.marco.hospital.bdd.steps;

import static com.manojlovic.marco.hospital.bdd.steps.DatabaseSteps.COLLECTION_NAME;
import static com.manojlovic.marco.hospital.bdd.steps.DatabaseSteps.DB_NAME;
import com.manojlovic.marco.hospital.bdd.HospitalSwingAppBDD;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PatientSwingViewSteps {

	private FrameFixture window;

	@After
	public void tearDown() {
		if (window != null)
			window.cleanUp();
	}

	@When("The Patient View is shown")
	public void the_Patient_View_is_shown() {
		application("com.manojlovic.marco.hospital.app.swing.HospitalSwingApp")
			.withArgs(
				"--mongo-port=" + HospitalSwingAppBDD.mongoPort,
				"--db-name=" + DB_NAME,
				"--db-collection=" + COLLECTION_NAME
			)
			.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Patient View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonText) {
		window.button(JButtonMatcher.withText(buttonText)).click();
	}

	@Given("The user provides patient data in the text fields")
	public void the_user_provides_patient_data_in_the_text_fields() {
		window.textBox("idTextBox").enterText("10");
		window.textBox("nameTextBox").enterText("new patient");
		window.textBox("recoveryDateTextBox").enterText("10/02/2023");
	}

	@Then("The list contains the new patient")
	public void the_list_contains_the_new_patient() {
		assertThat(window.list("patientList").contents())
			.anySatisfy(e -> assertThat(e).contains("10", "new patient", "10/02/2023"));
	}

	@Given("The user provides patient data in the text fields, specifying an existing id")
	public void the_user_provides_patient_data_in_the_text_fields_specifying_an_existing_id() {
		window.textBox("idTextBox").enterText(DatabaseSteps.PATIENT_FIXTURE_1_ID);
		window.textBox("nameTextBox").enterText("new patient");
		window.textBox("recoveryDateTextBox").enterText("10/02/2023");
	}

	@Then("An error is shown containing the name of the existing patient")
	public void an_error_is_shown_containing_the_name_of_the_existing_patient() {
		assertThat(window.label("errorMessageLabel").text())
			.contains(DatabaseSteps.PATIENT_FIXTURE_1_NAME);
	}

	@Given("The user selects a patient from the list")
	public void the_user_selects_a_patient_from_the_list() {
		window.list("patientList")
			.selectItem(Pattern.compile(".*" + DatabaseSteps.PATIENT_FIXTURE_1_ID + ".*"));
	}

	@Then("The patient is removed from the list")
	public void the_patient_is_removed_from_the_list() {
		assertThat(window.list("patientList").contents())
			.noneMatch(e -> e.contains(DatabaseSteps.PATIENT_FIXTURE_1_NAME));
	}

	@Then("An error is shown containing the name of the selected patient")
	public void an_error_is_shown_containing_the_name_of_the_selected_patient() {
		assertThat(window.label("errorMessageLabel").text())
			.contains(DatabaseSteps.PATIENT_FIXTURE_1_NAME);
	}

	@Given("The user provides a name in the search name field, specifying an existing name")
	public void the_user_provides_a_name_in_the_search_name_field_specifying_an_existing_name() {
		window.textBox("searchNameTextBox").enterText(DatabaseSteps.PATIENT_FIXTURE_1_NAME);
	}

	@Then("The search list contains the searched patient")
	public void the_single_list_contains_the_searched_patient() {
		assertThat(window.list("searchedPatientsList").contents())
			.anySatisfy(e -> assertThat(e).contains(DatabaseSteps.PATIENT_FIXTURE_1_ID, DatabaseSteps.PATIENT_FIXTURE_1_NAME, DatabaseSteps.PATIENT_FIXTURE_1_DATE));
	}

	@Given("The user provides a name in the search name field, specifying a not existing name")
	public void the_user_provides_a_name_in_the_search_name_field_specifying_a_not_existing_name() {
		window.textBox("searchNameTextBox").enterText("not existing");
	}

	@Then("An error is shown containing the searched name")
	public void an_error_is_shown_containing_the_searched_name() {
	assertThat(window.label("errorMessageLabel").text())
		.contains("not existing");
	}
}
