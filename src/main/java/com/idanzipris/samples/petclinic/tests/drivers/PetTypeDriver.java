package com.idanzipris.samples.petclinic.tests.drivers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.idanzipris.samples.petclinic.tests.model.PetType;
import com.idanzipris.samples.petclinic.tests.model.PetTypeFields;

@Component
public class PetTypeDriver {

    private static final String PET_TYPES = "/api/pettypes";

    @Autowired
    private BaseDriver baseDriver;

    public ResponseEntity<List<PetType>> listPetTypes() {
        return baseDriver.get(PET_TYPES, new ParameterizedTypeReference<>() {});
    }

    public ResponseEntity<PetType> getPetType(int petTypeId) {
        return baseDriver.get(PET_TYPES + "/" + petTypeId, PetType.class);
    }

    public ResponseEntity<PetType> addPetType(PetTypeFields petType) {
        return baseDriver.post(PET_TYPES, baseDriver.serialize(petType), PetType.class);
    }

    public ResponseEntity<PetType> updatePetType(int petTypeId, PetType petType) {
        return baseDriver.put(PET_TYPES + "/" + petTypeId, baseDriver.serialize(petType), PetType.class);
    }

    public ResponseEntity<Void> deletePetType(int petTypeId) {
        return baseDriver.delete(PET_TYPES + "/" + petTypeId);
    }
}
