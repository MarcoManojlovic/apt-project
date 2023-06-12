package com.manojlovic.marco.hospital.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.manojlovic.marco.hospital.model.Patient;
import com.manojlovic.marco.hospital.repository.PatientRepository;
import com.manojlovic.marco.hospital.view.PatientView;

public class HospitalControllerTest {

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private PatientView patientView;

	@InjectMocks
	private HospitalController HospitalController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllPatients() {
		List<Patient> patients = asList(new Patient());
		when(patientRepository.findAll())
			.thenReturn(patients);
		HospitalController.allPatients();
		verify(patientView)
			.showAllPatients(patients);
	}

	@Test
	public void testNewPatientWhenPatientDoesNotAlreadyExist() {
		Patient patient = new Patient("1", "test", "10/02/2023");
		when(patientRepository.findById("1")).
			thenReturn(null);
		HospitalController.newPatient(patient);
		InOrder inOrder = inOrder(patientRepository, patientView);
		inOrder.verify(patientRepository).save(patient);
		inOrder.verify(patientView).patientAdded(patient);
	}

	@Test
	public void testNewPatientWhenPatientAlreadyExist() {
		Patient existingPatient = new Patient("1", "test", "10/02/2023");
		Patient patientToAdd = new Patient("1","name","20/04/2023");
		when(patientRepository.findById("1")).
			thenReturn(existingPatient);
		HospitalController.newPatient(patientToAdd);
		verify(patientView)
			.showError("Already existing patient with id 1", existingPatient);
		verifyNoMoreInteractions(ignoreStubs(patientRepository));
	}

	@Test
	public void testDeletePatientWhenPatientExists() {
		Patient patientToDelete = new Patient("1", "test", "10/02/2023");
		when(patientRepository.findById("1")).
			thenReturn(patientToDelete);
		HospitalController.deletePatient(patientToDelete);
		InOrder inOrder = inOrder(patientRepository, patientView);
		inOrder.verify(patientRepository).delete("1");
		inOrder.verify(patientView).patientRemoved(patientToDelete);
	}

	@Test
	public void testDeletePatientWhenPatientDoesNotExist() {
		Patient patient = new Patient("1", "test", "10/02/2023");
		when(patientRepository.findById("1")).
			thenReturn(null);
		HospitalController.deletePatient(patient);
		verify(patientView)
			.showErrorPatientNotFound("No existing patient with id 1",
					patient);
		verifyNoMoreInteractions(ignoreStubs(patientRepository));
	}

	@Test
	public void testSearchPatientWhenPatientsExists() {
		Patient patient1 = new Patient("1", "Marco", "10/02/2023");
		Patient patient2 = new Patient("2", "Marco", "15/03/2023");
		List <Patient> patients = asList(patient1, patient2);
		when(patientRepository.findByName("Marco")).
			thenReturn(patients);
		HospitalController.searchPatient("Marco");
		InOrder inOrder = inOrder(patientRepository, patientView);
		inOrder.verify(patientRepository).findByName("Marco");
		inOrder.verify(patientView).showSearchedPatients(patients);
	}

	@Test
	public void testSearchPatientsWhenPatientDoesNotExists() {
		String nameToSearch = "Marco";
		when(patientRepository.findByName(nameToSearch)).
			thenReturn(Collections.emptyList());
		HospitalController.searchPatient(nameToSearch);
		verify(patientView)
			.showErrorPatientNotFound("No existing patient with name " + nameToSearch);
		verifyNoMoreInteractions(ignoreStubs(patientRepository));
	}
}
