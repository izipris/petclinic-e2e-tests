package com.idanzipris.samples.petclinic.tests.drivers;

import com.idanzipris.samples.petclinic.tests.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PetDriver {

  private static final String PETS = "/api/pets";

  @Autowired
  private BaseDriver baseDriver;

  public ResponseEntity<List<Pet>> listPets() {

    return baseDriver.get(PETS, new ParameterizedTypeReference<>() {
    });
  }

  public ResponseEntity<Pet> getPet(int petId) {

    return baseDriver.get(PETS + "/" + petId, Pet.class);
  }

  public ResponseEntity<Pet> updatePet(int petId, Pet pet) {

    return baseDriver.put(PETS + "/" + petId, baseDriver.serialize(pet), Pet.class);
  }

  public ResponseEntity<Void> deletePet(int petId) {

    return baseDriver.delete(PETS + "/" + petId);
  }
}
