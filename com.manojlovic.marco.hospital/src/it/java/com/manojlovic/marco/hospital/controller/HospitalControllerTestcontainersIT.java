package com.manojlovic.marco.hospital.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.manojlovic.marco.hospital.model.Patient;
import com.manojlovic.marco.hospital.repository.mongo.PatientMongoRepository;
import com.manojlovic.marco.hospital.view.swing.PatientSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

@RunWith(GUITestRunner.class)

public class HospitalControllerTestcontainersIT extends AssertJSwingJUnitTestCase {

	@ClassRule
	public static final MongoDBContainer mongo =
		new MongoDBContainer("mongo:4.4.3");

	private MongoClient client;

	private FrameFixture window;
	private PatientSwingView patientSwingView;
	private HospitalController hospitalController;
	private PatientMongoRepository patientRepository;
	
	private static final String HOSPITAL_DB_NAME = "hospital";
	private static final String PATIENT_COLLECTION_NAME = "patient";

	@Override
	protected void onSetUp() {
		client = new MongoClient(
				new ServerAddress(
					mongo.getContainerIpAddress(),
					mongo.getMappedPort(27017)));
			patientRepository = new PatientMongoRepository(client, HOSPITAL_DB_NAME, PATIENT_COLLECTION_NAME);
			
			MongoDatabase database = client.getDatabase(HOSPITAL_DB_NAME);
			database.drop();
			
			
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
		client.close();
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
	public void testNewPatient() {
		Patient patient = new Patient("1", "test", "10/02/2023");
		GuiActionRunner.execute(
				() -> hospitalController.newPatient(patient));
		assertThat(window.list("patientList").contents())
			.containsExactly("1 - test - 10/02/2023");
	}

	@Test @GUITest
	public void testDeletePatient() {
		Patient patientToDelete = new Patient("1", "test", "10/02/2023");
		patientRepository.save(patientToDelete);
		GuiActionRunner.execute(
				() -> hospitalController.deletePatient(patientToDelete));
		assertThat(window.list("patientList").contents())
			.doesNotContain(patientToDelete.toString());
	}

	@Test @GUITest
	public void testSearchPatient() {
		Patient patient1 = new Patient("1", "test1", "10/02/2023");
		Patient patient2 = new Patient("2", "test2", "11/02/2023");
		patientRepository.save(patient1);
		patientRepository.save(patient2);
		GuiActionRunner.execute(
				() -> hospitalController.searchPatient("test"));
		assertThat(window.list("searchedPatientsList").contents())
			.containsExactly("1 - test1 - 10/02/2023", "2 - test2 - 11/02/2023");
	}
}
