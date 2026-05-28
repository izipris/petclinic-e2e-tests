package com.idanzipris.samples.petclinic.tests.drivers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.idanzipris.samples.petclinic.tests.model.Vet;

@Component
public class VetDriver {

    private static final String VETS = "/api/vets";

    @Autowired
    private BaseDriver baseDriver;

    public ResponseEntity<List<Vet>> listVets() {
        return baseDriver.get(VETS, new ParameterizedTypeReference<>() {});
    }

    public ResponseEntity<Vet> getVet(int vetId) {
        return baseDriver.get(VETS + "/" + vetId, Vet.class);
    }

    public ResponseEntity<Vet> addVet(Vet vet) {
        return baseDriver.post(VETS, baseDriver.serialize(vet), Vet.class);
    }

    public ResponseEntity<Vet> updateVet(int vetId, Vet vet) {
        return baseDriver.put(VETS + "/" + vetId, baseDriver.serialize(vet), Vet.class);
    }

    public ResponseEntity<Void> deleteVet(int vetId) {
        return baseDriver.delete(VETS + "/" + vetId);
    }
}
