package com.manojlovic.marco.hospital.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.manojlovic.marco.hospital.app.swing.HospitalSwingApp;
import com.manojlovic.marco.hospital.controller.HospitalController;
import com.manojlovic.marco.hospital.repository.mongo.PatientMongoRepository;
import com.manojlovic.marco.hospital.view.swing.PatientSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class HospitalSwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "hospital";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "patient";

	public static void main(String[] args) {
		new CommandLine(new HospitalSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				PatientMongoRepository patientRepository = new PatientMongoRepository(
						new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionName);
				PatientSwingView patientView = new PatientSwingView();
				HospitalController hospitalController = new HospitalController(patientView, patientRepository);
				patientView.setHospitalController(hospitalController);
				patientView.setVisible(true);
				hospitalController.allPatients();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}

}
