package com.richieoscar.clinicapi.controllers;

import com.richieoscar.clinicapi.entities.ClinicalData;
import com.richieoscar.clinicapi.entities.Patient;
import com.richieoscar.clinicapi.exceptions.PatientNotFoundException;
import com.richieoscar.clinicapi.repository.PatientRepository;
import com.richieoscar.clinicapi.util.BMICalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/v1")
@CrossOrigin
public class PatientController {

    private PatientRepository patientRepository;
    private Map<String, String> filters = new HashMap<>();

    @Autowired
    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/patients")
    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/patients/{id}")
    public Patient getPatient(@PathVariable("id") int id) {
        String message = "Patient with " + id + " not found";
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isPresent()) {
            return optionalPatient.get();
        } else throw new PatientNotFoundException(message);
    }

    @PostMapping("/addpatient")
    public Patient addPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientRepository.save(patient);
        return savedPatient;
    }

    @GetMapping("/patients/analyze/{id}")
    public Patient analyse(@PathVariable("id") int id) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            List<ClinicalData> clinicalData = patient.getClinicalData();
            ArrayList<ClinicalData> cloneData = new ArrayList<>(clinicalData);
            for (ClinicalData clinicEntry : cloneData) {
                //we filter the clinical data to get just one clinic data
                if (filters.containsKey(clinicEntry.getComponentName())) {
                    clinicalData.remove(clinicEntry);
                    continue;
                } else {
                    filters.put(clinicEntry.getComponentName(), null);
                }
                BMICalculator.calculateBmi(clinicalData,clinicEntry);
            }
            filters.clear();
            return patient;
        } else throw new PatientNotFoundException("Patient Not found");
    }


}
