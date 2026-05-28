package com.idanzipris.samples.petclinic.tests;

import com.idanzipris.samples.petclinic.tests.drivers.OwnerDriver;
import com.idanzipris.samples.petclinic.tests.drivers.PetDriver;
import com.idanzipris.samples.petclinic.tests.model.Pet;
import com.idanzipris.samples.petclinic.tests.model.PetFields;
import com.idanzipris.samples.petclinic.tests.model.PetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PetTest {

  private static final int OWNER_ID = 1;
  private static final int PET_TYPE_ID = 1;

  @Autowired
  private PetDriver petDriver;

  @Autowired
  private OwnerDriver ownerDriver;

  @Test
  void shouldCreatePetForOwnerAndRetrieveIt() {

    var fields = newPetFields("Buddy");
    var createdId = createPetByOwner(fields);

    var getResponse = petDriver.getPet(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(createdId, getResponse.getBody().getId());
    assertEquals(fields.getName(), getResponse.getBody().getName());

    petDriver.deletePet(createdId);
  }

  @Test
  void shouldCreatePetForOwnerAndRetrieveItByOwner() {

    var fields = newPetFields("Buddy");
    var createdId = createPetByOwner(fields);

    var ownersPetResponse = ownerDriver.getOwnersPet(OWNER_ID, createdId);
    assertEquals(HttpStatus.OK, ownersPetResponse.getStatusCode());
    assertNotNull(ownersPetResponse.getBody());
    assertEquals(createdId, ownersPetResponse.getBody().getId());
    assertEquals(fields.getName(), ownersPetResponse.getBody().getName());

    petDriver.deletePet(createdId);
  }

  @Test
  void shouldUpdatePetName() {

    var fields = newPetFields("Rex");
    var createdId = createPet(fields);
    var update = Pet.builder()
            .id(createdId)
            .name("RexUpd-" + UUID.randomUUID().toString().substring(0, 8))
            .birthDate(fields.getBirthDate())
            .type(fields.getType())
            .build();

    var updateResponse = petDriver.updatePet(createdId, update);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getResponse = petDriver.getPet(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(update.getName(), getResponse.getBody().getName());

    petDriver.deletePet(createdId);
  }

  @Test
  void shouldUpdatePetThroughOwner() {

    var fields = newPetFields("Milo");
    var createdId = createPet(fields);

    var updatedBirthDate = fields.getBirthDate().minusMonths(6);
    var updatedFields = PetFields.builder()
            .name(fields.getName())
            .birthDate(updatedBirthDate)
            .type(fields.getType())
            .build();

    var updateResponse = ownerDriver.updateOwnersPet(OWNER_ID, createdId, updatedFields);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getResponse = ownerDriver.getOwnersPet(OWNER_ID, createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(updatedBirthDate, getResponse.getBody().getBirthDate());

    petDriver.deletePet(createdId);
  }

  @Test
  void shouldDeletePetAndOmitItFromListing() {

    var fields = newPetFields("Luna");
    var createdId = createPet(fields);

    var listBefore = petDriver.listPets();
    assertEquals(HttpStatus.OK, listBefore.getStatusCode());
    assertNotNull(listBefore.getBody());
    assertTrue(listBefore.getBody().stream().anyMatch(pet -> pet.getId() == createdId));

    var deleteResponse = petDriver.deletePet(createdId);
    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    var listAfter = petDriver.listPets();
    assertEquals(HttpStatus.OK, listAfter.getStatusCode());
    assertNotNull(listAfter.getBody());
    assertTrue(listAfter.getBody().stream().noneMatch(pet -> pet.getId() == createdId));

    var getResponse = petDriver.getPet(createdId);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  private int createPet(PetFields fields) {

    var response = ownerDriver.addPetToOwner(OWNER_ID, fields);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    var pet = response.getBody();
    assertNotNull(pet);
    assertNotNull(pet.getId());
    assertEquals(fields.getName(), pet.getName());
    assertEquals(fields.getBirthDate(), pet.getBirthDate());
    assertEquals(PET_TYPE_ID, pet.getType().getId());
    return pet.getId();
  }

  private Integer createPetByOwner(PetFields fields) {

    var createResponse = ownerDriver.addPetToOwner(OWNER_ID, fields);
    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    var created = createResponse.getBody();
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals(fields.getName(), created.getName());
    assertEquals(fields.getBirthDate(), created.getBirthDate());
    assertEquals(PET_TYPE_ID, created.getType().getId());
    return created.getId();
  }

  private PetFields newPetFields(String namePrefix) {

    return PetFields.builder()
            .name(namePrefix + "-" + UUID.randomUUID().toString().substring(0, 8))
            .birthDate(LocalDate.now().minusYears(2))
            .type(PetType.builder().id(PET_TYPE_ID).name("cat").build())
            .build();
  }
}
