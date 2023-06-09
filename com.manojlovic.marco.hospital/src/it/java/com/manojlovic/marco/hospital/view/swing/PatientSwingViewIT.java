package com.manojlovic.marco.hospital.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.manojlovic.marco.hospital.controller.HospitalController;
import com.manojlovic.marco.hospital.model.Patient;
import com.manojlovic.marco.hospital.repository.mongo.PatientMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class PatientSwingViewIT extends AssertJSwingJUnitTestCase{

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient mongoClient;

	private FrameFixture window;
	private PatientSwingView patientSwingView;
	private HospitalController hospitalController;
	private PatientMongoRepository patientRepository;
	
	private static final String HOSPITAL_DB_NAME = "hospital";
	private static final String PATIENT_COLLECTION_NAME = "patient";
	
	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		patientRepository =
			new PatientMongoRepository(mongoClient, HOSPITAL_DB_NAME, PATIENT_COLLECTION_NAME);
		for (Patient patient : patientRepository.findAll()) {
			patientRepository.delete(patient.getId());
		}
		
		GuiActionRunner.execute(() -> {
			patientSwingView = new PatientSwingView();
			hospitalController = new HospitalController(patientSwingView, patientRepository);
			patientSwingView.setHospitalController(hospitalController);
			return patientSwingView;
		});
		window = new FrameFixture(robot(), patientSwingView);
		window.show();
	}
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testAllPatients() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "10/02/2023");
		patientRepository.save(patient1);
		patientRepository.save(patient2);

		GuiActionRunner.execute(
			() -> hospitalController.allPatients());

		assertThat(window.list("patientList").contents())
			.containsExactly("1 - test1 - 10/02/2023", "2 - test2 - 10/02/2023");
	}

	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.textBox("recoveryDateTextBox").enterText("10/02/2023");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("patientList").contents())
			.containsExactly("1 - test - 10/02/2023");
	}

	@Test @GUITest
	public void testAddButtonError() {
		patientRepository.save(new Patient("1", "existing", "10/02/2023"));
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.textBox("recoveryDateTextBox").enterText("10/02/2023");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("patientList").contents())
			.isEmpty();
		window.label("errorMessageLabel")
			.requireText("Already existing patient with id 1: "
					+ "1 - existing - 10/02/2023");
	}

	@Test @GUITest
	public void testDeleteButtonFromMainListSuccess() {
		GuiActionRunner.execute(
			() -> hospitalController.newPatient(new Patient("1", "toremove", "10/02/2023")));
		window.list("patientList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list("patientList").contents())
			.isEmpty();
	}

	@Test @GUITest
	public void testDeleteButtonFromSearchedPatientsListSuccess() {
		Patient patient = new Patient("1", "toremove", "10/02/2023");
		patientRepository.save(patient);
		GuiActionRunner.execute(
			() -> hospitalController.searchPatient("toremove"));
		window.list("searchedPatientsList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list("searchedPatientsList").contents())
			.isEmpty();
	}

	@Test @GUITest
	public void testDeleteButtonError() {
		Patient patient = new Patient("1", "non existent", "10/02/2023");
		GuiActionRunner.execute(
			() -> patientSwingView.getListPatientsModel().addElement(patient));
		window.list("patientList").selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list("patientList").contents())
			.isEmpty();
		window.label("errorMessageLabel")
			.requireText("No existing patient with id 1: 1 - non existent - 10/02/2023");
}

	@Test @GUITest
	public void testSearchButtonSuccess() {
		Patient patient = new Patient("1", "test", "10/02/2023");
		patientRepository.save(patient);
		window.textBox("searchNameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Search")).click();
		assertThat(window.list("searchedPatientsList").contents())
			.containsExactly("1 - test - 10/02/2023");
	}

	@Test @GUITest
	public void testSearchButtonError() {
		window.textBox("searchNameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Search")).click();
		assertThat(window.list("searchedPatientsList").contents())
			.isEmpty();
		window.label("errorMessageLabel")
			.requireText("No existing patient with name test");
	}	
}
