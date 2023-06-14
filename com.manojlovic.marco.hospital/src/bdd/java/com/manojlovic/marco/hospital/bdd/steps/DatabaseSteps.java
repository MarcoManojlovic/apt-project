package com.manojlovic.marco.hospital.bdd.steps;

import org.bson.Document;

import com.manojlovic.marco.hospital.bdd.HospitalSwingAppBDD;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {

	static final String DB_NAME = "test-db";
	static final String COLLECTION_NAME = "test-collection";

	static final String PATIENT_FIXTURE_1_ID = "1";
	static final String PATIENT_FIXTURE_1_NAME = "patient1";
	static final String PATIENT_FIXTURE_1_DATE = "10 02 2023";
	static final String PATIENT_FIXTURE_2_ID = "2";
	static final String PATIENT_FIXTURE_2_NAME = "patient2";
	static final String PATIENT_FIXTURE_2_DATE = "11 02 2023";

	private MongoClient mongoClient;

	@Before
	public void setUp() {
		mongoClient = new MongoClient("localhost", HospitalSwingAppBDD.mongoPort);
		mongoClient.getDatabase(DB_NAME).drop();
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}

	@Given("The database contains a few patients")
	public void the_database_contains_a_few_patients() {
		addTestPatientToDatabase(PATIENT_FIXTURE_1_ID, PATIENT_FIXTURE_1_NAME, PATIENT_FIXTURE_1_DATE);
		addTestPatientToDatabase(PATIENT_FIXTURE_2_ID, PATIENT_FIXTURE_2_NAME, PATIENT_FIXTURE_2_DATE);
	}

	@Given("The patient is in the meantime removed from the database")
	public void the_patient_is_in_the_meantime_removed_from_the_database() {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(COLLECTION_NAME)
			.deleteOne(Filters.eq("id", PATIENT_FIXTURE_1_ID));
	}

	private void addTestPatientToDatabase(String id, String name, String recoveryDate) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(COLLECTION_NAME)
			.insertOne(
				new Document()
					.append("id", id)
					.append("name", name)
					.append("recoveryDate", recoveryDate));
	}
}
