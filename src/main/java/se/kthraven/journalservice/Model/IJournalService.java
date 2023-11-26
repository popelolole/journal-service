package se.kthraven.journalservice.Model;

import se.kthraven.journalservice.Model.classes.Doctor;
import se.kthraven.journalservice.Model.classes.Encounter;
import se.kthraven.journalservice.Model.classes.Observation;
import se.kthraven.journalservice.Model.classes.Patient;

import java.util.Collection;

public interface IJournalService {
    Patient getPatient(String id);
    Doctor getDoctor(String id);
    void createPatient(Patient patient);
    Collection<Encounter> getEncountersByPatient(String patientId);
    Encounter getEncounter(String id);
    void createEncounter(Encounter encounter);
    void createObservation(Observation observation, String encounterId);
}
