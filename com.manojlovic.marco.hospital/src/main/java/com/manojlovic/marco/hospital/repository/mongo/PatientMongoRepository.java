package com.manojlovic.marco.hospital.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.manojlovic.marco.hospital.model.Patient;
import com.manojlovic.marco.hospital.repository.PatientRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class PatientMongoRepository implements PatientRepository {

	private MongoCollection<Document> patientCollection;

	public PatientMongoRepository(MongoClient client, String databaseName, String collectionName) {
		patientCollection = client
			.getDatabase(databaseName)
			.getCollection(collectionName);
	}

	@Override
	public List<Patient> findAll() {
		return StreamSupport.
				stream(patientCollection.find().spliterator(), false)
				.map(this::fromDocumentToPatient)
				.collect(Collectors.toList());
	}

	public List<Patient> findByName(String name) {
		return StreamSupport.
				stream(patientCollection.find().spliterator(), false)
				.map(this::fromDocumentToPatient)
				.filter(p -> p.getName().contains(name))
				.collect(Collectors.toList());
	}

	private Patient fromDocumentToPatient(Document d) {
		return new Patient(""+d.get("id"), ""+d.get("name"), ""+d.get("recoveryDate"));
	}

	@Override
	public Patient findById(String id) {
		Document d = patientCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumentToPatient(d);
		return null;
	}

	@Override
	public void save(Patient patient) {
		patientCollection.insertOne(
				new Document()
					.append("id", patient.getId())
					.append("name", patient.getName())
					.append("recoveryDate", patient.getRecoveryDate()));
	}

	@Override
	public void delete(String id) {
		patientCollection.deleteOne(Filters.eq("id", id));
	}

}
