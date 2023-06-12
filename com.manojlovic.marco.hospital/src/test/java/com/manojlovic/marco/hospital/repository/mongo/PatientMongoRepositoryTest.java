package com.manojlovic.marco.hospital.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.manojlovic.marco.hospital.model.Patient;import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class PatientMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient client;
	private PatientMongoRepository patientRepository;
	private MongoCollection<Document> patientCollection;

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

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
		patientRepository = new PatientMongoRepository(client, HOSPITAL_DB_NAME, PATIENT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(HOSPITAL_DB_NAME);
		database.drop();
		patientCollection = database.getCollection(PATIENT_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(patientRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestPatientToDatabase("1", "test1", "10/02/2023");
		addTestPatientToDatabase("2", "test2", "10/02/2023");
		assertThat(patientRepository.findAll())
			.containsExactly(
				new Patient("1", "test1", "10/02/2023"),
				new Patient("2", "test2", "10/02/2023"));
	}

	public void testFindByNameNotFound() {
		assertThat(patientRepository.findByName("Marco")).isEmpty();
	}

	public void testFindByNameFound() {
		addTestPatientToDatabase("1", "Marco", "10/02/2023");
		addTestPatientToDatabase("2", "Mariangelo", "11/02/2023");
		addTestPatientToDatabase("3", "Ludovico", "11/02/2023");
		assertThat(patientRepository.findByName("Mar"))
			.containsExactly(
				new Patient("1", "Marco", "10/02/2023"),
				new Patient("2", "Mariangelo", "11/02/2023"));
	}

	@Test
	public void testFindByIdNotFound() {
		assertThat(patientRepository.findById("1")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		addTestPatientToDatabase("1", "test1", "10/02/2023");
		addTestPatientToDatabase("2", "test2", "10/02/2023");
		assertThat(patientRepository.findById("2"))
			.isEqualTo(new Patient("2", "test2", "10/02/2023"));
	}

	@Test
	public void testSave() {
		Patient patient = new Patient("1", "test", "10/02/2023");
		patientRepository.save(patient);
		assertThat(readAllPatientsFromDatabase())
			.containsExactly(patient);
	}

	@Test
	public void testDelete() {
		addTestPatientToDatabase("1", "test", "10/02/2023");
		patientRepository.delete("1");
		assertThat(readAllPatientsFromDatabase())
			.isEmpty();
	}

	private void addTestPatientToDatabase(String id, String name, String recoveryDate) {
		patientCollection.insertOne(
				new Document()
					.append("id", id)
					.append("name", name)
					.append("recoveryDate", recoveryDate));
	}

	private List<Patient> readAllPatientsFromDatabase() {
		return StreamSupport.
			stream(patientCollection.find().spliterator(), false)
				.map(d -> new Patient(""+d.get("id"), ""+d.get("name"), ""+d.get("recoveryDate")))
				.collect(Collectors.toList());
	}

}
