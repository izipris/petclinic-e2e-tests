package com.idanzipris.samples.petclinic.tests.drivers;

import com.idanzipris.samples.petclinic.tests.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OwnerDriver {

  private static final String OWNERS = "/api/owners";

  @Autowired
  private BaseDriver baseDriver;

  public ResponseEntity<List<Owner>> listOwners() {

    return baseDriver.get(OWNERS, new ParameterizedTypeReference<>() {
    });
  }

  public ResponseEntity<List<Owner>> listOwners(String lastName) {

    String queryParametersString = baseDriver.generateQueryParameters(Map.of("lastName", lastName));
    String path = OWNERS + queryParametersString;
    return baseDriver.get(path, new ParameterizedTypeReference<>() {
    });
  }

  public ResponseEntity<Owner> getOwner(int ownerId) {

    String path = OWNERS + "/" + ownerId;
    return baseDriver.get(path, Owner.class);
  }

  public ResponseEntity<Owner> addOwner(OwnerFields owner) {

    return baseDriver.post(OWNERS, baseDriver.serialize(owner), Owner.class);
  }

  public ResponseEntity<Owner> updateOwner(int ownerId, OwnerFields owner) {

    String path = OWNERS + "/" + ownerId;
    return baseDriver.put(path, baseDriver.serialize(owner), Owner.class);
  }

  public ResponseEntity<Void> deleteOwner(int ownerId) {

    String path = OWNERS + "/" + ownerId;
    return baseDriver.delete(path);
  }

  public ResponseEntity<Pet> addPetToOwner(int ownerId, PetFields pet) {

    String path = OWNERS + "/" + ownerId + "/pets";
    return baseDriver.post(path, baseDriver.serialize(pet), Pet.class);
  }

  public ResponseEntity<Pet> getOwnersPet(int ownerId, int petId) {

    String path = OWNERS + "/" + ownerId + "/pets/" + petId;
    return baseDriver.get(path, Pet.class);
  }

  public ResponseEntity<Void> updateOwnersPet(int ownerId, int petId, PetFields pet) {

    String path = OWNERS + "/" + ownerId + "/pets/" + petId;
    return baseDriver.put(path, baseDriver.serialize(pet));
  }

  public ResponseEntity<Visit> addVisitToOwner(int ownerId, int petId, VisitFields visit) {

    String path = OWNERS + "/" + ownerId + "/pets/" + petId + "/visits";
    return baseDriver.post(path, baseDriver.serialize(visit), Visit.class);
  }
}
