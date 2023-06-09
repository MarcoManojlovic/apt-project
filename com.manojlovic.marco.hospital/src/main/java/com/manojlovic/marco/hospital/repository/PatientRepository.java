package com.manojlovic.marco.hospital.repository;

import java.util.List;

import com.manojlovic.marco.hospital.model.Patient;

public interface PatientRepository {
	
	public List<Patient> findAll();
	
	public List<Patient> findByName(String name);

	public Patient findById(String id);

	public void save(Patient patient);

	public void delete(String id);
}
