package com.idanzipris.samples.petclinic.tests;

import com.idanzipris.samples.petclinic.tests.drivers.PetTypeDriver;
import com.idanzipris.samples.petclinic.tests.model.PetType;
import com.idanzipris.samples.petclinic.tests.model.PetTypeFields;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PetTypeTest {

  @Autowired
  private PetTypeDriver petTypeDriver;

  @Test
  void shouldCreatePetTypeAndRetrieveItById() {

    var fields = newPetTypeFields("hamster");
    var createdId = createPetType(fields);

    var getResponse = petTypeDriver.getPetType(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(createdId, getResponse.getBody().getId());
    assertEquals(fields.getName(), getResponse.getBody().getName());

    petTypeDriver.deletePetType(createdId);
  }

  @Test
  void shouldUpdatePetTypeName() {

    var fields = newPetTypeFields("ferret");
    var createdId = createPetType(fields);

    var updated = PetType.builder()
            .id(createdId)
            .name("parrot-" + UUID.randomUUID().toString().substring(0, 8))
            .build();

    var updateResponse = petTypeDriver.updatePetType(createdId, updated);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getResponse = petTypeDriver.getPetType(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(updated.getName(), getResponse.getBody().getName());

    petTypeDriver.deletePetType(createdId);
  }

  @Test
  void shouldDeletePetTypeAndOmitItFromListing() {

    var fields = newPetTypeFields("gerbil");
    var createdId = createPetType(fields);

    var listBefore = petTypeDriver.listPetTypes();
    assertEquals(HttpStatus.OK, listBefore.getStatusCode());
    assertNotNull(listBefore.getBody());
    assertTrue(listBefore.getBody().stream().anyMatch(petType -> petType.getId().equals(createdId)));

    var deleteResponse = petTypeDriver.deletePetType(createdId);
    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    var listAfter = petTypeDriver.listPetTypes();
    assertEquals(HttpStatus.OK, listAfter.getStatusCode());
    assertNotNull(listAfter.getBody());
    assertTrue(listAfter.getBody().stream().noneMatch(petType -> petType.getId().equals(createdId)));

    var getResponse = petTypeDriver.getPetType(createdId);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  private int createPetType(PetTypeFields fields) {

    var response = petTypeDriver.addPetType(fields);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    var petType = response.getBody();
    assertNotNull(petType);
    assertNotNull(petType.getId());
    assertEquals(fields.getName(), petType.getName());
    return petType.getId();
  }

  private PetTypeFields newPetTypeFields(String namePrefix) {

    return PetTypeFields.builder()
            .name(namePrefix + "-" + UUID.randomUUID().toString().substring(0, 8))
            .build();
  }
}
