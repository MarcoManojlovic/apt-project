package com.manojlovic.marco.hospital.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static java.util.Arrays.asList;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.manojlovic.marco.hospital.controller.HospitalController;
import com.manojlovic.marco.hospital.model.Patient;

@RunWith(GUITestRunner.class)
public class PatientSwingViewTest extends AssertJSwingJUnitTestCase {


	private FrameFixture window;

	private PatientSwingView patientSwingView;	

	@Mock
	private HospitalController hospitalController;

	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);	
		GuiActionRunner.execute(() -> {
			patientSwingView = new PatientSwingView();
			patientSwingView.setHospitalController(hospitalController);
			return patientSwingView;
		});

		window = new FrameFixture(robot(), patientSwingView);
		window.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test @GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("Id"));
		window.textBox("idTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("nameTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("Recovery date"));
		window.textBox("recoveryDateTextBox").requireEnabled();		
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.list("patientList");
		window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");

		window.label(JLabelMatcher.withText("Search patient by name"));
		window.textBox("searchNameTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Search")).requireDisabled();
		window.list("searchedPatientsList");
	}

	@Test
	public void testWhenIdAndNameAndDateAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.textBox("recoveryDateTextBox").enterText("10/02/2023");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	public void testWhenIdOrNameOrDateAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture idTextBox = window.textBox("idTextBox");
		JTextComponentFixture nameTextBox = window.textBox("nameTextBox");
		JTextComponentFixture dateTextBox = window.textBox("recoveryDateTextBox");

		idTextBox.enterText("1");
		nameTextBox.enterText("test");
		dateTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetTextFields(idTextBox, nameTextBox, dateTextBox);

		idTextBox.enterText("1");
		nameTextBox.enterText(" ");
		dateTextBox.enterText("10/02/2023");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetTextFields(idTextBox, nameTextBox, dateTextBox);

		idTextBox.enterText("1");
		nameTextBox.enterText(" ");
		dateTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetTextFields(idTextBox, nameTextBox, dateTextBox);

		idTextBox.enterText(" ");
		nameTextBox.enterText("test");
		dateTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetTextFields(idTextBox, nameTextBox, dateTextBox);

		idTextBox.enterText(" ");
		nameTextBox.enterText(" ");
		dateTextBox.enterText("10/02/2023");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		resetTextFields(idTextBox, nameTextBox, dateTextBox);

		idTextBox.enterText(" ");
		nameTextBox.enterText("test");
		dateTextBox.enterText("10/02/2023 ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	public void testWhenSearchNameIsNonEmptyThenSearchButtonShouldBeEnabled() {
		window.textBox("searchNameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Search")).requireEnabled();
	}
	
	private void resetTextFields(JTextComponentFixture idTextBox, JTextComponentFixture nameTextBox, JTextComponentFixture recoveryDateTextBox) {
		idTextBox.setText("");
		nameTextBox.setText("");
		recoveryDateTextBox.setText("");
	}

	@Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenAPatientIsSelectedFromTheLists() {
		GuiActionRunner.execute(() -> patientSwingView.getListPatientsModel().addElement(new Patient("1", "test", "10/02/2023")));
		window.list("patientList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected"));
		deleteButton.requireEnabled();
		window.list("patientList").clearSelection();
		deleteButton.requireDisabled();
		
		GuiActionRunner.execute(() -> patientSwingView.getListSearchedPatientsModel().addElement(new Patient("1", "test", "10/02/2023")));
		window.list("searchedPatientsList").selectItem(0);
		deleteButton.requireEnabled();
		window.list("searchedPatientsList").clearSelection();
		deleteButton.requireDisabled();
	}

	@Test
	public void testsShowAllPatientsShouldAddPatientDescriptionsToTheList() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "10/02/2023");
		GuiActionRunner.execute(
			() -> patientSwingView.showAllPatients(
					Arrays.asList(patient1, patient2))
		);
		String[] listContents = window.list("patientList").contents();
		assertThat(listContents).containsExactly("1 - test1 - 10/02/2023",
				"2 - test2 - 10/02/2023");
	}

	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Patient patient = new Patient("1", "test", "10/02/2023");
		GuiActionRunner.execute(
			() -> patientSwingView.showError("error message", patient)
		);
		window.label("errorMessageLabel")
			.requireText("error message: 1 - test - 10/02/2023");
	}

	@Test
	public void testPatientAddedShouldAddThePatienToTheListAndResetTheErrorLabel() {
		GuiActionRunner.execute(
				() ->
				patientSwingView.patientAdded(new Patient("1", "test", "10/02/2023"))
				);
		String[] listContents = window.list("patientList").contents();
		assertThat(listContents).containsExactly("1 - test - 10/02/2023");
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testPatientRemovedShouldRemoveThePatientFromTheListsAndResetTheErrorLabel() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "10/02/2023");
		GuiActionRunner.execute(
			() -> {
				DefaultListModel<Patient> listPatientsModel = patientSwingView.getListPatientsModel();
				DefaultListModel<Patient> listsearchedPatientsModel = patientSwingView.getListSearchedPatientsModel();
				listPatientsModel.addElement(patient1);
				listPatientsModel.addElement(patient2);
				listsearchedPatientsModel.addElement(patient1);
				listsearchedPatientsModel.addElement(patient2);			
			}
		);

		GuiActionRunner.execute(
			() ->
			patientSwingView.patientRemoved(new Patient("1", "test1", "10/02/2023"))
		);

		String[] listPatientContents = window.list("patientList").contents();
		assertThat(listPatientContents).containsExactly("2 - test2 - 10/02/2023");
		String[] listSearchedPatientContents = window.list("searchedPatientsList").contents();
		assertThat(listSearchedPatientContents).containsExactly("2 - test2 - 10/02/2023");
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testAddButtonShouldDelegateToHospitalControllerNewPatient() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.textBox("recoveryDateTextBox").enterText("10/02/2023");
		window.button(JButtonMatcher.withText("Add")).click();
		verify(hospitalController).newPatient(new Patient("1", "test", "10/02/2023"));
	}

	@Test
	public void testDeleteButtonWhenSelectedPatientsAreDifferentShouldThrowError() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "10/02/2023");
		
		setLists(patient1, patient2);
		window.list("patientList").selectItem(0);
		window.list("searchedPatientsList").selectItem(1);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		window.label("errorMessageLabel").requireText("Selected patients are different");
	}

	@Test
	public void testDeleteButtonWhenSelectedPatientsAreEqualShouldDelegateToHospitalControllerDeletePatient() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "10/02/2023");
		
		setLists(patient1, patient2);
		window.list("patientList").selectItem(0);
		window.list("searchedPatientsList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		verify(hospitalController).deletePatient(patient1);
	}

	@Test
	public void testDeleteButtonWhenOnlyOnePatientIsSelectedShouldDelegateToHospitalControllerDeletePatient() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "10/02/2023");
		
		setLists(patient1, patient2);
		window.list("patientList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		verify(hospitalController).deletePatient(patient1);
		
		window.list("patientList").clearSelection();
		setLists(patient1, patient2);
		window.list("searchedPatientsList").selectItem(1);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		verify(hospitalController).deletePatient(patient2);
	}

	private void setLists(Patient patient1, Patient patient2) {
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Patient> listPatientsModel = patientSwingView.getListPatientsModel();
					DefaultListModel<Patient> listSearchedPatientsModel = patientSwingView.getListSearchedPatientsModel();
					listPatientsModel.removeAllElements();
					listSearchedPatientsModel.removeAllElements();
					
					listPatientsModel.addElement(patient1);
					listPatientsModel.addElement(patient2);
					listSearchedPatientsModel.addElement(patient1);
					listSearchedPatientsModel.addElement(patient2);			
				}
			);		
	}

	@Test
	public void testsShowSearchedPatientsShouldResetTheSearchedPatientListAndAddPatientsDescriptionToTheSearchedPatientListAndResetTheErrorLabel() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "10/02/2023");

		GuiActionRunner.execute(
			() -> patientSwingView.showSearchedPatients(asList(patient1,patient2)));
		String[] listContents = window.list("searchedPatientsList").contents();
		assertThat(listContents).containsExactly("1 - test1 - 10/02/2023", "2 - test2 - 10/02/2023");

		GuiActionRunner.execute(
				() -> patientSwingView.showSearchedPatients(asList(patient2)));
		listContents = window.list("searchedPatientsList").contents();
		assertThat(listContents).containsExactly("2 - test2 - 10/02/2023");
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testShowErrorPatientNotFoundShouldShowTheMessageInTheErrorLabel() {
		GuiActionRunner.execute(
			() -> patientSwingView.showErrorPatientNotFound("error message")
		);
		window.label("errorMessageLabel")
			.requireText("error message");
	}

}

