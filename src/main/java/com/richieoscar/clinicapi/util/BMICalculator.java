package com.richieoscar.clinicapi.util;

import com.richieoscar.clinicapi.entities.ClinicalData;

import java.util.List;

public class BMICalculator {

    public static void calculateBmi(List<ClinicalData> clinicalData, ClinicalData clinicEntry) {
        if (clinicEntry.getComponentName().equals("hw")) {
            String[] heightAndWeight = clinicEntry.getComponentValue().split("/");
            if (heightAndWeight != null && heightAndWeight.length > 1) {
                float heightInMeters = Float.parseFloat(heightAndWeight[0]) * 04536F;
                float bmi = Float.parseFloat(heightAndWeight[1]) / (heightInMeters * heightInMeters);
                ClinicalData bmiData = new ClinicalData();
                bmiData.setComponentName("bmi");
                bmiData.setComponentValue(String.valueOf(bmi));
                clinicalData.add(bmiData);
            }
        }
    }
}
