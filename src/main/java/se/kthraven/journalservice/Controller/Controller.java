package se.kthraven.journalservice.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.kthraven.journalservice.Model.IJournalService;
import se.kthraven.journalservice.Model.classes.Doctor;
import se.kthraven.journalservice.Model.classes.Encounter;
import se.kthraven.journalservice.Model.classes.Observation;
import se.kthraven.journalservice.Model.classes.Patient;

import java.util.Collection;

@RestController
public class Controller {

    @Autowired
    private IJournalService journalService;

    @GetMapping("/patient")
    public Patient getPatient(@RequestParam(value = "id") String id){
        Patient patient = journalService.getPatient(id);
        return patient;
    }

    @PostMapping("/patient")
    public ResponseEntity<String> createPatient(@RequestBody Patient patient){
        journalService.createPatient(patient);
        return new ResponseEntity<>("Person created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/doctor")
    public Doctor getDoctor(@RequestParam(value = "id") String id){
        Doctor doctor = journalService.getDoctor(id);
        return doctor;
    }

    @GetMapping("/patient/encounters")
    public Collection<Encounter> patientEncounters(@RequestParam(value = "patientId") String patientId){
        Collection<Encounter> encounters = journalService.getEncountersByPatient(patientId);
        return encounters;
    }

    @GetMapping("/encounter")
    public Encounter getEncounter(@RequestParam(value = "id") String id){
        Encounter encounter = journalService.getEncounter(id);
        return encounter;
    }

    @PostMapping("/encounter")
    public ResponseEntity<String> createEncounter(@RequestBody Encounter encounter){
        journalService.createEncounter(encounter);
        return new ResponseEntity<>("Encounter created successfully", HttpStatus.CREATED);
    }

    @PostMapping("/encounter/observation")
    public Observation createObservation(@RequestBody Observation observation, @RequestParam(value = "encounterId") String encounterId){
        journalService.createObservation(observation, encounterId);
        return observation;
    }
}
