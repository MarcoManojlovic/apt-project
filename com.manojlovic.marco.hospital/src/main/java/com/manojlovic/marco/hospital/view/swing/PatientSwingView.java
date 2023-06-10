package com.manojlovic.marco.hospital.view.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.manojlovic.marco.hospital.controller.HospitalController;
import com.manojlovic.marco.hospital.model.Patient;
import com.manojlovic.marco.hospital.view.PatientView;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.JLabel;
import javax.swing.JList;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class PatientSwingView extends JFrame implements PatientView {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField idTextField;
	private JLabel lblName;
	private JTextField nameTextField;
	private JLabel lblDate;
	private JTextField dateTextField;
	private JButton btnAdd;
	private JLabel lblSearchName;
	private JTextField searchNameTextField;
	private JButton btnSearch;
	private JButton btnDelete;
	private JLabel lblErrorMessage;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	
	private JList<Patient> listPatients;
	private JList<Patient> listSearchedPatients;
	private DefaultListModel<Patient> listPatientsModel;
	private DefaultListModel<Patient> listSearchedPatientsModel;	
	
	private HospitalController hospitalController;

	DefaultListModel<Patient> getListPatientsModel() {
		return listPatientsModel;
	}

	DefaultListModel<Patient> getListSearchedPatientsModel() {
		return listSearchedPatientsModel;
	}
	
	public void setHospitalController(HospitalController hospitalController) {
		this.hospitalController = hospitalController;
	}

	/**
	 * Create the frame.
	 */
	public PatientSwingView() {
		setTitle("Patient View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 618, 442);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("6px"),
				ColumnSpec.decode("114px:grow"),
				ColumnSpec.decode("200px"),
				ColumnSpec.decode("52px"),
				ColumnSpec.decode("200px"),
				ColumnSpec.decode("250px"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("46px"),},
			new RowSpec[] {
				RowSpec.decode("25px"),
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("19px"),
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("19px"),
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("25px"),
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("25px"),
				RowSpec.decode("10px"),
				RowSpec.decode("default:grow"),
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAdd.setEnabled(
						!idTextField.getText().trim().isEmpty() &&
						!nameTextField.getText().trim().isEmpty() &&
						!dateTextField.getText().trim().isEmpty()
				);
			}
		};

		KeyAdapter btnSearchEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnSearch.setEnabled(
						!searchNameTextField.getText().isEmpty()
				);
			}
		};

		JLabel lblId = new JLabel("Id");
		lblId.setName("id");
		contentPane.add(lblId, "2, 2, left, center");
		
		idTextField = new JTextField();
		idTextField.addKeyListener(btnAddEnabler);		
		idTextField.setName("idTextBox");
		contentPane.add(idTextField, "3, 2, fill, center");
		idTextField.setColumns(20);
		
		lblSearchName = new JLabel("Search patient by name");
		lblSearchName.setName("searchId");
		contentPane.add(lblSearchName, "5, 1, center, center");
		
		searchNameTextField = new JTextField();
		searchNameTextField.addKeyListener(btnSearchEnabler);	
		searchNameTextField.setName("searchNameTextBox");
		contentPane.add(searchNameTextField, "5, 4, fill, center");
		searchNameTextField.setColumns(10);
		
		lblName = new JLabel("Name");
		contentPane.add(lblName, "2, 4, left, center");
		
		nameTextField = new JTextField();
		nameTextField.addKeyListener(btnAddEnabler);		
		nameTextField.setName("nameTextBox");
		contentPane.add(nameTextField, "3, 4, fill, center");
		nameTextField.setColumns(20);
		
		lblDate = new JLabel("Recovery date");
		contentPane.add(lblDate, "2, 6, left, center");
		
		dateTextField = new JTextField();
		dateTextField.addKeyListener(btnAddEnabler);		
		dateTextField.setName("recoveryDateTextBox");
		contentPane.add(dateTextField, "3, 6, fill, center");
		dateTextField.setColumns(20);
		
		btnAdd = new JButton("Add");
		btnAdd.setHorizontalAlignment(SwingConstants.LEADING);
		btnAdd.setEnabled(false);
		btnAdd.addActionListener(
				e -> hospitalController.newPatient(new Patient(idTextField.getText(), nameTextField.getText(), dateTextField.getText())));		
		contentPane.add(btnAdd, "3, 8, center, center");
		
		btnSearch = new JButton("Search");
		btnSearch.setEnabled(false);
		btnSearch.addActionListener(
				e -> hospitalController.searchPatient(searchNameTextField.getText()));		
		contentPane.add(btnSearch, "5, 8, center, center");
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "3, 10, 1, 15, fill, fill");
		
		btnDelete = new JButton("Delete Selected");
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(e -> {
            if (!listSearchedPatients.isSelectionEmpty()) {
            	hospitalController.deletePatient(listSearchedPatients.getSelectedValue());
            } else {
            	hospitalController.deletePatient(listPatients.getSelectedValue());
            }
        });
		
		contentPane.add(btnDelete, "3, 28, 3, 1");
		
		
		listPatientsModel = new DefaultListModel<>();
		listPatients = new JList<>(listPatientsModel);
		listPatients.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				Patient patient = (Patient) value;
				return super.getListCellRendererComponent(list,
					getDisplayString(patient),
					index, isSelected, cellHasFocus);
			}
		});		
	
		listPatients.addListSelectionListener(
				e -> {
						btnDelete.setEnabled(listPatients.getSelectedIndex() != -1);
						listSearchedPatients.clearSelection();
				});	

		listPatients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPatients.setName("patientList");
		scrollPane.setViewportView(listPatients);
		
		scrollPane_1 = new JScrollPane();
		contentPane.add(scrollPane_1, "5, 10, 1, 15, fill, fill");
		
		listSearchedPatientsModel = new DefaultListModel<>();
		listSearchedPatients = new JList<>(listSearchedPatientsModel);
		listSearchedPatients.setName("searchedPatientsList");
		listSearchedPatients.addListSelectionListener(
				e -> {
						btnDelete.setEnabled(listSearchedPatients.getSelectedIndex() != -1);
						listPatients.clearSelection();
				});

		listSearchedPatients.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				Patient patient = (Patient) value;
				return super.getListCellRendererComponent(list,
					getDisplayString(patient),
					index, isSelected, cellHasFocus);
			}
		});			
		scrollPane_1.setViewportView(listSearchedPatients);
		
		lblErrorMessage = new JLabel(" ");
		lblErrorMessage.setName("errorMessageLabel");
		contentPane.add(lblErrorMessage, "2, 32, 4, 1, center, center");
	}

	@Override
	public void showAllPatients(List<Patient> patients) {
		patients.stream().forEach(listPatientsModel::addElement);
	}

	@Override
	public void showSearchedPatients(List<Patient> patients) {
		listSearchedPatientsModel.removeAllElements();
		patients.stream().forEach(listSearchedPatientsModel::addElement);
		resetErrorLabel();
	}

	@Override
	public void showError(String message, Patient patient) {
		lblErrorMessage.setText(message + ": " + getDisplayString(patient));
	}

	@Override
	public void patientAdded(Patient patient) {
		listPatientsModel.addElement(patient);
		resetErrorLabel();
	}

	@Override
	public void patientRemoved(Patient patient) {
		listPatientsModel.removeElement(patient);
		if(listSearchedPatientsModel.contains(patient)) {
			listSearchedPatientsModel.removeElement(patient);
		}
		resetErrorLabel();
	}

	private void resetErrorLabel() {
		lblErrorMessage.setText(" ");
	}

	private String getDisplayString(Patient patient) {
		return patient.getId() + " - " + patient.getName() + " - " + patient.getRecoveryDate();
	}

	@Override
	public void showErrorPatientNotFound(String message, Patient patient) {
		lblErrorMessage.setText(message + ": " + getDisplayString(patient));
		listSearchedPatientsModel.removeAllElements();
		listPatientsModel.removeElement(patient);
	}

	@Override
	public void showErrorPatientNotFound(String message) {
		lblErrorMessage.setText(message);
		listSearchedPatientsModel.removeAllElements();
	}

}
