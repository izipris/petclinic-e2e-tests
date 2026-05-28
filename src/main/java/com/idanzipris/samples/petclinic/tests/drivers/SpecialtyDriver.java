package com.idanzipris.samples.petclinic.tests.drivers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.idanzipris.samples.petclinic.tests.model.Specialty;

@Component
public class SpecialtyDriver {

    private static final String SPECIALTIES = "/api/specialties";

    @Autowired
    private BaseDriver baseDriver;

    public ResponseEntity<List<Specialty>> listSpecialties() {
        return baseDriver.get(SPECIALTIES, new ParameterizedTypeReference<>() {});
    }

    public ResponseEntity<Specialty> getSpecialty(int specialtyId) {
        return baseDriver.get(SPECIALTIES + "/" + specialtyId, Specialty.class);
    }

    public ResponseEntity<Specialty> addSpecialty(Specialty specialty) {
        return baseDriver.post(SPECIALTIES, baseDriver.serialize(specialty), Specialty.class);
    }

    public ResponseEntity<Specialty> updateSpecialty(int specialtyId, Specialty specialty) {
        return baseDriver.put(SPECIALTIES + "/" + specialtyId, baseDriver.serialize(specialty), Specialty.class);
    }

    public ResponseEntity<Void> deleteSpecialty(int specialtyId) {
        return baseDriver.delete(SPECIALTIES + "/" + specialtyId);
    }
}
