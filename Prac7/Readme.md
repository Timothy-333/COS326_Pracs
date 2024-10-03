# Hospital Management System

This project is a MongoDB-based Hospital Management System to manage information about patients, doctors, and their appointments. The system includes functions to manage the system's data, including queries and updates for patient records and appointment statistics.

## Setup

Load the database with the following commands:
```bash
load("C:/Users/User/Desktop/Tuks/3rd Year/2nd Semester/COS 326/COS326_Pracs/Prac7/.mongoshrc.js")
```

## Functions

### Task 1
2. Insert 10 random patients into the “Patients” collection using:
```javascript
insertPatients("HospitalDB", "Patients", 10);
```
3. Run the following to list all admitted patients:
```javascript
findAdmittedPatients("HospitalDB", "Patients");
```
4. Update the admission status of a patient (e.g., “Palesa Mohlare”) using:
```javascript
updatePatientAdmission("HospitalDB", "Patients", "Palesa Mohlare", true);
```
5. Remove discharged patients using:
```javascript
removeDischargedPatients("HospitalDB", "Patients");
```

### Task 2

1. Show the total number of patients each doctor is treating
```javascript
doctorStats("HospitalDB", "Patients");
```
2. List all patients treated by a specific doctor (e.g., “Dr. Mandela”)
```javascript
doctorPatientList("HospitalDB", "Patients", "Dr. Mandela");
```
3. Compute and store the number of patients for each doctor:
```javascript
activeDoctorsMR("HospitalDB", "Patients");
```
4. Display the total number of appointments each doctor has:
```javascript
appointmentStats("HospitalDB", "Patients");
```

### Other Functions

1. List all patients in the “Patients” collection
```javascript
listPatients("HospitalDB", "Patients");
```
2. List doctors active in the system
```javascript
listDoctorActivity("HospitalDB");
```
3. List all appointments
```javascript
listAllAppointments("HospitalDB");
```
3. Clear the database
```javascript
clearDatabase("HospitalDB");
```