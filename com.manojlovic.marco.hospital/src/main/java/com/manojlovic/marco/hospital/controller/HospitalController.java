package com.manojlovic.marco.hospital.controller;

import java.util.List;

import com.manojlovic.marco.hospital.model.Patient;
import com.manojlovic.marco.hospital.repository.PatientRepository;
import com.manojlovic.marco.hospital.view.PatientView;

public class HospitalController {

	private PatientView patientView;
	private PatientRepository patientRepository;

	public HospitalController(PatientView patientView, PatientRepository patientRepository) {
		this.patientView = patientView;
		this.patientRepository = patientRepository;
	}

	public void allPatients() {
		patientView.showAllPatients(patientRepository.findAll());
	}

	public void newPatient(Patient patient) {
		Patient existingPatient = patientRepository.findById(patient.getId());
		if (existingPatient != null) {
			patientView.showError("Already existing patient with id " + patient.getId(),
					existingPatient);
			return;
		}

		patientRepository.save(patient);
		patientView.patientAdded(patient);
	}

	public void deletePatient(Patient patient) {
		Patient existingPatient = patientRepository.findById(patient.getId());
		if (existingPatient == null) {
			patientView.showErrorPatientNotFound("No existing patient with id " + patient.getId());
			return;
		}

		patientRepository.delete(patient.getId());
		patientView.patientRemoved(patient);
	}

	public void searchPatient(String name) {
		List<Patient> patients = patientRepository.findByName(name);
		if (patients.isEmpty()) {
			patientView.showErrorPatientNotFound("No existing patient with name " + name);
			return;
		}

		patientView.showSearchedPatients(patients);
	}
	
}
