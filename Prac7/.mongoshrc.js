// Function to insert patients into the collection
function insertPatients(dbName, colName, patientCount) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    const names = [
        "Palesa Mohlare", "John Doe", "Jane Smith", "Michael Brown", "Emily Davis",
        "Sarah Johnson", "Chris Lee", "Jessica Taylor", "David Wilson", "Laura Martinez",
        "Daniel Anderson", "Sophia Thomas", "James Jackson", "Olivia White", "Liam Harris",
        "Emma Clark", "Noah Lewis", "Ava Robinson", "Mason Walker", "Isabella Young",
        "Lucas Hall", "Mia Allen", "Ethan King", "Amelia Wright", "Alexander Scott",
        "Charlotte Green", "Benjamin Adams", "Harper Baker", "Elijah Nelson", "Evelyn Carter",
        "William Mitchell", "Abigail Perez", "Sebastian Roberts", "Ella Turner", "Jack Phillips",
        "Grace Campbell", "Henry Parker", "Chloe Evans", "Samuel Edwards", "Victoria Collins"
    ];
    const ailments = ["Cold", "Flu", "Headache", "Stomach Ache", "Back Pain"];
    const doctors = ["Dr. Green", "Dr. Blue", "Dr. Red", "Dr. Yellow", "Dr. White"];

    // Shuffle the names array to ensure unique names
    for (let i = names.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [names[i], names[j]] = [names[j], names[i]];
    }

    for (let i = 0; i < patientCount; i++) {
        const patient = {
            name: names[i % names.length], // Ensure we don't go out of bounds
            age: Math.floor(Math.random() * 60) + 18,
            ailment: ailments[Math.floor(Math.random() * ailments.length)],
            doctor: doctors[Math.floor(Math.random() * doctors.length)],
            admitted: Math.random() < 0.5,
            appointments: [
                { date: "2024-09-01", reason: "Initial checkup" },
                { date: "2024-09-15", reason: "Follow-up" }
            ]
        };
        collection.insertOne(patient);
    }
}

// Function to find all admitted patients
function findAdmittedPatients(dbName, colName) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    return collection.find({ admitted: true }).toArray();
}

// Function to update the admission status of a patient
function updatePatientAdmission(dbName, colName, patientName, status) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    collection.updateOne({ name: patientName }, { $set: { admitted: status } });
}

// Function to remove all discharged patients 
function removeDischargedPatients(dbName, colName) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    collection.deleteMany({ admitted: false });
}

// Function to list all patients
function listAllPatients(dbName, colName) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    return collection.find().toArray();
}

// Function to clear the database
function clearDatabase(dbName) {
    const db = connect(dbName);
    db.dropDatabase();
}

// Function to list all doctors and the number of patients they are currently treating
function doctorStats(dbName, colName) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    return collection.aggregate([
        { $match: { admitted: true } },
        { $group: { _id: "$doctor", patientCount: { $sum: 1 } } },
        { $sort: { _id: 1 } }
    ]).toArray();
}

// Function to list all patients under a specific doctor
function doctorPatientList(dbName, colName, doctorName) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    return collection.find({ doctor: doctorName, admitted: true }).toArray();
}

// Function to count the total number of patients for each doctor and store the results in a new collection called "DoctorActivity"
function activeDoctorsMR(dbName, colName) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    const result = collection.aggregate([
        { $group: { _id: "$doctor", patientCount: { $sum: 1 } } }
    ]).toArray();
    db.getCollection("DoctorActivity").insertMany(result);
}
// Function to list all doctor activity
function listDoctorActivity(dbName) {
    const db = connect(dbName);
    const collection = db.getCollection("DoctorActivity");
    return collection.find().toArray();
}

// Function to display the total number of appointments each doctor has, grouped by doctor name
function appointmentStats(dbName, colName) {
    const db = connect(dbName);
    const collection = db.getCollection(colName);
    const appointments = collection.aggregate([
        { $unwind: "$appointments" },
        { $project: { doctor: "$doctor", patient: "$name", date: "$appointments.date" } }
    ]).toArray();

    appointments.forEach(appointment => {
        appointment._id = new ObjectId();
    });

    db.getCollection("Appointments").insertMany(appointments);
}

// Function to list all appointments
function listAllAppointments(dbName) {
    const db = connect(dbName);
    const collection = db.getCollection("Appointments");
    return collection.find().toArray();
}