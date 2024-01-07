package se.kthraven.journalservice.Persistence;

import se.kthraven.journalservice.Persistence.entities.EncounterDB;
import se.kthraven.journalservice.Persistence.entities.ObservationDB;
import se.kthraven.journalservice.Persistence.entities.PersonDB;

import java.util.Collection;

public interface IJournalPersistence {
     PersonDB getPerson(String id);
     void createPerson(PersonDB person);
     Collection<EncounterDB> getEncountersByPatient(String patientId);
     EncounterDB getEncounter(String id);
     void createEncounter(EncounterDB encounter);
     void createObservation(ObservationDB observation);

     String getUserId(String personId);
}
