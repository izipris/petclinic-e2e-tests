package com.idanzipris.samples.petclinic.tests;

import com.idanzipris.samples.petclinic.tests.drivers.OwnerDriver;
import com.idanzipris.samples.petclinic.tests.model.OwnerFields;
import com.idanzipris.samples.petclinic.tests.model.PetFields;
import com.idanzipris.samples.petclinic.tests.model.PetType;
import com.idanzipris.samples.petclinic.tests.model.VisitFields;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OwnerTest {

  private static final int PET_TYPE_ID = 2;

  private static final String PET_TYPE_NAME = "dog";

  @Autowired
  private OwnerDriver ownerDriver;

  @Test
  void shouldCreateOwnerAndRetrieveItById() {

    var fields = newOwnerFields("Smith");
    var createdId = createOwner(fields);

    var getResponse = ownerDriver.getOwner(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(createdId, getResponse.getBody().getId());
    assertEquals(fields.getFirstName(), getResponse.getBody().getFirstName());
    assertEquals(fields.getLastName(), getResponse.getBody().getLastName());
    assertEquals(fields.getTelephone(), getResponse.getBody().getTelephone());

    ownerDriver.deleteOwner(createdId);
  }

  @Test
  void shouldUpdateOwnerDetails() {

    var fields = newOwnerFields("Jones");
    var createdId = createOwner(fields);

    var updatedFields = OwnerFields.builder()
            .firstName(fields.getFirstName())
            .lastName(fields.getLastName())
            .address("742 Evergreen Terrace")
            .city("Springfield")
            .telephone("6085559999")
            .build();

    var updateResponse = ownerDriver.updateOwner(createdId, updatedFields);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getResponse = ownerDriver.getOwner(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(updatedFields.getAddress(), getResponse.getBody().getAddress());
    assertEquals(updatedFields.getCity(), getResponse.getBody().getCity());
    assertEquals(updatedFields.getTelephone(), getResponse.getBody().getTelephone());

    ownerDriver.deleteOwner(createdId);
  }

  @Test
  void shouldDeleteOwnerAndOmitItFromListing() {

    var fields = newOwnerFields("Baker");
    var createdId = createOwner(fields);

    var listBefore = ownerDriver.listOwners();
    assertEquals(HttpStatus.OK, listBefore.getStatusCode());
    assertNotNull(listBefore.getBody());
    assertTrue(listBefore.getBody().stream().anyMatch(owner -> owner.getId().equals(createdId)));

    var deleteResponse = ownerDriver.deleteOwner(createdId);
    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    var listAfter = ownerDriver.listOwners();
    assertEquals(HttpStatus.OK, listAfter.getStatusCode());
    assertNotNull(listAfter.getBody());
    assertTrue(listAfter.getBody().stream().noneMatch(owner -> owner.getId().equals(createdId)));

    var getResponse = ownerDriver.getOwner(createdId);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  @Test
  void shouldFindOwnerByLastName() {

    var uniqueLastName = "Zipfield";
    var fields = OwnerFields.builder()
            .firstName("Idan")
            .lastName(uniqueLastName)
            .address("110 W. Liberty St.")
            .city("Madison")
            .telephone("6085551023")
            .build();
    var createdId = createOwner(fields);

    var listResponse = ownerDriver.listOwners(uniqueLastName);
    assertEquals(HttpStatus.OK, listResponse.getStatusCode());
    assertNotNull(listResponse.getBody());
    assertTrue(listResponse.getBody().stream()
            .anyMatch(owner -> owner.getId().equals(createdId)));
    assertTrue(listResponse.getBody().stream()
            .allMatch(owner -> uniqueLastName.equals(owner.getLastName())));

    ownerDriver.deleteOwner(createdId);
  }

  @Test
  void shouldAddPetToOwnerAndRetrieveIt() {

    var ownerFields = newOwnerFields("Carter");
    var ownerId = createOwner(ownerFields);

    var petFields = newPetFields("Buddy");
    var addPetResponse = ownerDriver.addPetToOwner(ownerId, petFields);
    assertEquals(HttpStatus.CREATED, addPetResponse.getStatusCode());
    assertNotNull(addPetResponse.getBody());
    assertNotNull(addPetResponse.getBody().getId());
    var petId = addPetResponse.getBody().getId();

    var getPetResponse = ownerDriver.getOwnersPet(ownerId, petId);
    assertEquals(HttpStatus.OK, getPetResponse.getStatusCode());
    assertNotNull(getPetResponse.getBody());
    assertEquals(petId, getPetResponse.getBody().getId());
    assertEquals(petFields.getName(), getPetResponse.getBody().getName());
  }

  @Test
  void shouldUpdateOwnersPet() {

    var ownerFields = newOwnerFields("Murphy");
    var ownerId = createOwner(ownerFields);

    var petFields = newPetFields("Rex");
    var addPetResponse = ownerDriver.addPetToOwner(ownerId, petFields);
    assertEquals(HttpStatus.CREATED, addPetResponse.getStatusCode());
    assertNotNull(addPetResponse.getBody());
    var petId = addPetResponse.getBody().getId();

    var updatedBirthDate = petFields.getBirthDate().minusMonths(6);
    var updatedPetFields = PetFields.builder()
            .name(petFields.getName())
            .birthDate(updatedBirthDate)
            .type(petFields.getType())
            .build();

    var updateResponse = ownerDriver.updateOwnersPet(ownerId, petId, updatedPetFields);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getPetResponse = ownerDriver.getOwnersPet(ownerId, petId);
    assertEquals(HttpStatus.OK, getPetResponse.getStatusCode());
    assertNotNull(getPetResponse.getBody());
    assertEquals(updatedBirthDate, getPetResponse.getBody().getBirthDate());
  }

  @Test
  void shouldAddVisitToOwnersPet() {

    var ownerFields = newOwnerFields("Hughes");
    var ownerId = createOwner(ownerFields);

    var petFields = newPetFields("Milo");
    var addPetResponse = ownerDriver.addPetToOwner(ownerId, petFields);
    assertEquals(HttpStatus.CREATED, addPetResponse.getStatusCode());
    assertNotNull(addPetResponse.getBody());
    var petId = addPetResponse.getBody().getId();

    var visitFields = VisitFields.builder()
            .date(LocalDate.now())
            .description("rabies shot")
            .build();

    var addVisitResponse = ownerDriver.addVisitToOwner(ownerId, petId, visitFields);
    assertEquals(HttpStatus.CREATED, addVisitResponse.getStatusCode());
    assertNotNull(addVisitResponse.getBody());
    assertNotNull(addVisitResponse.getBody().getId());
    assertEquals(visitFields.getDescription(), addVisitResponse.getBody().getDescription());
    assertEquals(visitFields.getDate(), addVisitResponse.getBody().getDate());
  }

  private int createOwner(OwnerFields fields) {

    var response = ownerDriver.addOwner(fields);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    var owner = response.getBody();
    assertNotNull(owner);
    assertNotNull(owner.getId());
    assertEquals(fields.getFirstName(), owner.getFirstName());
    assertEquals(fields.getLastName(), owner.getLastName());
    return owner.getId();
  }

  private OwnerFields newOwnerFields(String lastName) {

    return OwnerFields.builder()
            .firstName("Tester")
            .lastName(lastName)
            .address("110 W. Liberty St.")
            .city("Madison")
            .telephone("6085551023")
            .build();
  }

  private PetFields newPetFields(String namePrefix) {

    return PetFields.builder()
            .name(namePrefix + "-" + UUID.randomUUID().toString().substring(0, 8))
            .birthDate(LocalDate.now().minusYears(2))
            .type(PetType.builder().id(PET_TYPE_ID).name(PET_TYPE_NAME).build())
            .build();
  }
}
