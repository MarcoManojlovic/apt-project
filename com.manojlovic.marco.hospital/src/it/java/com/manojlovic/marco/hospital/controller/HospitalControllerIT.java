package com.manojlovic.marco.hospital.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testcontainers.containers.MongoDBContainer;

import com.manojlovic.marco.hospital.model.Patient;
import com.manojlovic.marco.hospital.repository.PatientRepository;
import com.manojlovic.marco.hospital.repository.mongo.PatientMongoRepository;
import com.manojlovic.marco.hospital.view.PatientView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

@RunWith(MockitoJUnitRunner.class)
public class HospitalControllerIT {

	@Mock
	private PatientView patientView;

	private PatientRepository patientRepository;

	private HospitalController hospitalController;

	private MongoClient client;

	private static final String HOSPITAL_DB_NAME = "hospital";
	private static final String PATIENT_COLLECTION_NAME = "patient";
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getMappedPort(27017)));
		patientRepository = new PatientMongoRepository(client, HOSPITAL_DB_NAME, PATIENT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(HOSPITAL_DB_NAME);
		database.drop();
		hospitalController = new HospitalController(patientView, patientRepository);
	}

	@After
	public void tearDown() {
		client.close();
	}


	@Test
	public void testAllPatients() {
		Patient patient = new Patient("1", "test", "10 02 2023");
		patientRepository.save(patient);
		hospitalController.allPatients();
		verify(patientView)
			.showAllPatients(asList(patient));
	}

	@Test
	public void testNewPatient() {
		Patient patient = new Patient("1", "test", "10 02 2023");
		hospitalController.newPatient(patient);
		verify(patientView).patientAdded(patient);
	}

	@Test
	public void testDeletePatient() {
		Patient patientToDelete = new Patient("1", "test", "10 02 2023");
		patientRepository.save(patientToDelete);
		hospitalController.deletePatient(patientToDelete);
		verify(patientView).patientRemoved(patientToDelete);
	}

	@Test
	public void testSearchPatient() {
		Patient patientToSearch = new Patient("1", "toSearch", "10 02 2023");
		Patient patientNotToSearch = new Patient("2", "notToSearch", "10 02 2023");
		patientRepository.save(patientToSearch);
		patientRepository.save(patientNotToSearch);
		hospitalController.searchPatient("toSearch");
		verify(patientView).showSearchedPatients(asList(patientToSearch));
	}

}
