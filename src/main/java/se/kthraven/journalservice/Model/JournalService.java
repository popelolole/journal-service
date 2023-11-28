package se.kthraven.journalservice.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import se.kthraven.journalservice.Model.classes.Doctor;
import se.kthraven.journalservice.Model.classes.Encounter;
import se.kthraven.journalservice.Model.classes.Observation;
import se.kthraven.journalservice.Model.classes.Patient;
import se.kthraven.journalservice.Model.enums.Role;
import se.kthraven.journalservice.Persistence.IJournalPersistence;
import se.kthraven.journalservice.Persistence.entities.EncounterDB;
import se.kthraven.journalservice.Persistence.entities.ObservationDB;
import se.kthraven.journalservice.Persistence.entities.PersonDB;
import se.kthraven.journalservice.config.CustomAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class JournalService implements IJournalService{
    @Autowired
    private IJournalPersistence persistence;

    @Override
    public Patient getPatient(String id){
        checkAuthorityDoctorOrSamePatient(id);
        PersonDB patientDb = persistence.getPerson(id);

        if(patientDb == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(!patientDb.getRole().equals(Role.PATIENT))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Patient patient = Patient.from(patientDb);
        return patient;
    }

    @Override
    public Doctor getDoctor(String id){
        checkAuthorityDoctorOrOther();
        PersonDB doctorDb = persistence.getPerson(id);
        if(doctorDb == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(!doctorDb.getRole().equals(Role.DOCTOR))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Doctor doctor = Doctor.from(doctorDb);
        return doctor;
    }

    @Override
    public void createPatient(Patient patient) {
        checkAuthorityDoctorOrOther();

        if(!patient.getRole().equals(Role.PATIENT))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if(patient.getDoctor() != null) {
            PersonDB existingDoctor = persistence.getPerson(patient.getDoctor().getId());

            if(existingDoctor == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            if(!existingDoctor.getRole().equals(Role.DOCTOR))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        PersonDB personDb = patient.toPersonDB();
        persistence.createPerson(personDb);
    }

    @Override
    public Collection<Encounter> getEncountersByPatient(String patientId){
        checkAuthorityDoctorOrSamePatient(patientId);
        Collection<EncounterDB> encounterDbs = persistence.getEncountersByPatient(patientId);
        ArrayList<Encounter> encounters = new ArrayList<>();
        for(EncounterDB encounterDb : encounterDbs){
            encounters.add(Encounter.from(encounterDb));
        }
        return encounters;
    }

    @Override
    public Encounter getEncounter(String id){
        EncounterDB encounterDb = persistence.getEncounter(id);
        if(encounterDb == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return Encounter.from(encounterDb);
    }

    @Override
    public void createEncounter(Encounter encounter) {
        if(encounter == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        PersonDB existingDoctor = persistence.getPerson(encounter.getDoctor().getId());
        PersonDB existingPatient = persistence.getPerson(encounter.getPatient().getId());
        if(existingDoctor == null || existingPatient == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(!(existingDoctor.getRole().equals(Role.DOCTOR) || existingPatient.getRole().equals(Role.PATIENT)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        EncounterDB encounterDb = encounter.toEncounterDB();
        persistence.createEncounter(encounterDb);
    }

    @Override
    public void createObservation(Observation observation, String encounterId) {
        if(observation == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        EncounterDB encounterDb = persistence.getEncounter(encounterId);
        if(encounterDb == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        ObservationDB observationDb = observation.toObservationDB();
        observationDb.setEncounter(encounterDb);
        persistence.createObservation(observationDb);
    }

    private void checkAuthorityDoctorOrSamePatient(String patientId){
        CustomAuthenticationToken authToken = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String personId = authToken.getPersonId();
        String role = authToken.getRole();
        System.out.println("hej, " + personId + role);

        if(!role.equals("ROLE_" + Role.DOCTOR) && !personId.equals(patientId))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private void checkAuthorityDoctorOrOther(){
        CustomAuthenticationToken authToken = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String role = authToken.getRole();

        if(!(role.equals("ROLE_" + Role.DOCTOR) || role.equals("ROLE_" + Role.OTHER)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
