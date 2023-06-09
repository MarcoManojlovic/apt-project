package com.manojlovic.marco.hospital.view;

import java.util.List;

import com.manojlovic.marco.hospital.model.Patient;

public interface PatientView {

	void showAllPatients(List<Patient> patients);

	void showSearchedPatients(List<Patient> patients);

	void showError(String message, Patient patient);

	void patientAdded(Patient patient);

	void patientRemoved(Patient patient);

	void showErrorPatientNotFound(String message);
}
