package se.kthraven.journalservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.kthraven.journalservice.Model.JournalService;
import se.kthraven.journalservice.Model.classes.Doctor;
import se.kthraven.journalservice.Model.classes.Encounter;
import se.kthraven.journalservice.Model.classes.Patient;
import se.kthraven.journalservice.Model.classes.Observation;
import se.kthraven.journalservice.Model.enums.Gender;
import se.kthraven.journalservice.Model.enums.Role;
import se.kthraven.journalservice.Persistence.IJournalPersistence;
import se.kthraven.journalservice.Persistence.entities.EncounterDB;
import se.kthraven.journalservice.Persistence.entities.PersonDB;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceApplicationTests {

    @Mock
    private IJournalPersistence persistence;

    @InjectMocks
    private JournalService journalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetPatient() {
        when(persistence.getPerson("existingPatientId")).thenReturn(new PersonDB("existingPatientId", "John Doe", Gender.MALE, new Date(), Role.PATIENT));
        when(persistence.getPerson("nonExistingPatientId")).thenReturn(null);

        Patient resultPatient = journalService.getPatient("existingPatientId");
        assertNotNull(resultPatient);

        assertThrows(ResponseStatusException.class, () -> journalService.getPatient("nonExistingPatientId"));
    }

    @Test
    void testGetDoctor() {
        when(persistence.getPerson("existingDoctorId")).thenReturn(new PersonDB("existingPatientId", "John Doe", Gender.MALE, null, Role.DOCTOR));
        when(persistence.getPerson("nonExistingDoctorId")).thenReturn(null);

        Doctor resultDoctor = journalService.getDoctor("existingDoctorId");
        assertNotNull(resultDoctor);

        assertThrows(ResponseStatusException.class, () -> journalService.getDoctor("nonExistingDoctorId"));
    }

    @Test
    void testCreatePatient() {
        Patient patient = new Patient("newPatientId", "John Doe", Gender.MALE, null, null, null, Role.PATIENT, null, null);

        journalService.createPatient(patient);
        verify(persistence, times(1)).createPerson(any());

        patient.setDoctor(new Doctor("doctorId", "Dr. Smith", Gender.MALE, null, null, null, Role.DOCTOR, null));
        when(persistence.getPerson("doctorId")).thenReturn(new PersonDB("doctorId", "Dr. Smith", Gender.MALE, null, Role.DOCTOR));
        journalService.createPatient(patient);
        verify(persistence, times(2)).createPerson(any());
    }

    @Test
    void testGetEncountersByPatient() {
        when(persistence.getEncountersByPatient("existingPatientId"))
                .thenReturn(Arrays.asList(new EncounterDB("encounterId1", null, null, null), new EncounterDB("encounterId2", null, null, null)));
        when(persistence.getEncountersByPatient("nonExistingPatientId")).thenReturn(Collections.emptyList());

        Collection<Encounter> encounters = journalService.getEncountersByPatient("existingPatientId");
        assertEquals(2, encounters.size());

        encounters = journalService.getEncountersByPatient("nonExistingPatientId");
        assertEquals(0, encounters.size());
    }

    @Test
    void testCreateEncounter() {
        Encounter encounter = new Encounter("encounterId", new Patient("patientId", "John Doe", null, null, null, null, Role.PATIENT, null, null), new Doctor("doctorId", "Dr. Smith", null, null, null, null, Role.DOCTOR, null), null);

        when(persistence.getPerson("doctorId")).thenReturn(new PersonDB("doctorId", null, null, null, Role.DOCTOR));
        when(persistence.getPerson("patientId")).thenReturn(new PersonDB("patientId", null, null, null, Role.PATIENT));

        journalService.createEncounter(encounter);
        verify(persistence, times(1)).createEncounter(any());

        when(persistence.getPerson("nonExistingDoctorId")).thenReturn(null);
        encounter.setDoctor(new Doctor("nonExistingDoctorId", null, null, null, null, null, Role.DOCTOR, null));
        assertThrows(ResponseStatusException.class, () -> journalService.createEncounter(encounter));
    }

    @Test
    void testCreateObservation() {
        Observation observation = new Observation("observationId", "test", new Patient("existingPatientId", null, null, null, null, null, Role.PATIENT, null, null));

        when(persistence.getEncounter("existingEncounterId")).thenReturn(new EncounterDB("existingEncounterId", new PersonDB("existingPatientId", null, null, null, Role.PATIENT), null, null));
        when(persistence.getEncounter("nonExistingEncounterId")).thenReturn(null);

        journalService.createObservation(observation, "existingEncounterId");
        verify(persistence, times(1)).createObservation(any());

        assertThrows(ResponseStatusException.class, () -> journalService.createObservation(observation, "nonExistingEncounterId"));
    }

    @Test
    void testGetEncounter() {
        EncounterDB encounterDb = new EncounterDB("existingEncounterId", null, null, null);

        when(persistence.getEncounter("existingEncounterId")).thenReturn(encounterDb);
        when(persistence.getEncounter("nonExistingEncounterId")).thenReturn(null);

        assertNotNull(journalService.getEncounter("existingEncounterId"));

        assertThrows(ResponseStatusException.class, () -> journalService.getEncounter("nonExistingEncounterId"));
    }
}
