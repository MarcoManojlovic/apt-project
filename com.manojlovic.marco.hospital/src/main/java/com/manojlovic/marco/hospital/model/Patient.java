package com.manojlovic.marco.hospital.model;

import java.util.Objects;

public class Patient {
	private String id;
	private String name;
	private String recoveryDate;

	public Patient() {

	}

	public Patient(String id, String name, String recoveryDate) {
		this.id = id;
		this.name = name;
		this.recoveryDate = recoveryDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRecoveryDate() {
		return recoveryDate;
	}

	public void setRecoveryDate(String recoveryDate) {
		this.recoveryDate = recoveryDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, recoveryDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Patient other = (Patient) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name) && Objects.equals(recoveryDate, other.recoveryDate);
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", name=" + name + ", recoveryDate=" + recoveryDate + "]";
	}

}
