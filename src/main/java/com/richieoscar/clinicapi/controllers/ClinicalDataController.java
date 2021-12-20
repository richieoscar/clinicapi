package com.richieoscar.clinicapi.controllers;

import com.richieoscar.clinicapi.dto.ClinicalDataRequest;
import com.richieoscar.clinicapi.entities.ClinicalData;
import com.richieoscar.clinicapi.entities.Patient;
import com.richieoscar.clinicapi.exceptions.PatientNotFoundException;
import com.richieoscar.clinicapi.repository.ClinicalDataRepository;
import com.richieoscar.clinicapi.repository.PatientRepository;
import com.richieoscar.clinicapi.util.BMICalculator;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
@ApiResponse
public class ClinicalDataController {

    private ClinicalDataRepository clinicalDataRepository;
    private PatientRepository patientRepository;

    @Autowired
    public ClinicalDataController(ClinicalDataRepository clinicalDataRepository, PatientRepository patientRepository) {
        this.clinicalDataRepository = clinicalDataRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping("/clinicals")
    public ClinicalData saveclinicalData(@RequestBody ClinicalDataRequest request) {
        Optional<Patient> optionalPatient = patientRepository.findById(request.getPatientId());
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            ClinicalData clinicalData = new ClinicalData();
            clinicalData.setComponentName(request.getComponentName());
            clinicalData.setComponentValue(request.getComponentValue());
            clinicalData.setPatient(patient);
            return clinicalData;
        } else throw new PatientNotFoundException("Patient not Found");
    }

    @GetMapping("/clinicals/{patientId}/{componentName}")
    public List<ClinicalData> getClinicalData(@PathVariable("patientId") int patientId, @PathVariable("componentName") String componentName) {
        if (componentName.equals("bmi")) {
            componentName = "hw";
        }
        List<ClinicalData> clinicalData = clinicalDataRepository.findByPatientIdAndComponentNameOrderByMeasuredDateTime(patientId, componentName);
        ArrayList<ClinicalData> cloneData = new ArrayList<>(clinicalData);

        for (ClinicalData clinicEntry : cloneData) {
            BMICalculator.calculateBmi(cloneData, clinicEntry);
        }
        return clinicalData;
    }
}
