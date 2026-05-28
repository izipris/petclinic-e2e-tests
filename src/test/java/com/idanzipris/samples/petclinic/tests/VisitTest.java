package com.idanzipris.samples.petclinic.tests;

import com.idanzipris.samples.petclinic.tests.drivers.OwnerDriver;
import com.idanzipris.samples.petclinic.tests.drivers.PetDriver;
import com.idanzipris.samples.petclinic.tests.drivers.VisitDriver;
import com.idanzipris.samples.petclinic.tests.model.PetFields;
import com.idanzipris.samples.petclinic.tests.model.PetType;
import com.idanzipris.samples.petclinic.tests.model.Visit;
import com.idanzipris.samples.petclinic.tests.model.VisitFields;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class VisitTest {

  private static final int OWNER_ID = 1;
  private static final int PET_TYPE_ID = 1;

  @Autowired
  private VisitDriver visitDriver;

  @Autowired
  private OwnerDriver ownerDriver;

  @Autowired
  private PetDriver petDriver;

  @Test
  void shouldCreateVisitAndRetrieveItById() {

    var petId = createPet();
    var visit = newVisit(petId, "annual checkup");
    var createdId = createVisit(visit);

    var getResponse = visitDriver.getVisit(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(createdId, getResponse.getBody().getId());
    assertEquals(visit.getDescription(), getResponse.getBody().getDescription());
    assertEquals(visit.getDate(), getResponse.getBody().getDate());
    assertEquals(petId, getResponse.getBody().getPetId());

    visitDriver.deleteVisit(createdId);
    petDriver.deletePet(petId);
  }

  @Test
  void shouldUpdateVisitDetails() {

    var petId = createPet();
    var visit = newVisit(petId, "vaccination");
    var createdId = createVisit(visit);

    var updatedFields = VisitFields.builder()
            .date(visit.getDate().minusDays(3))
            .description("rescheduled vaccination")
            .build();

    var updateResponse = visitDriver.updateVisit(createdId, updatedFields);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getResponse = visitDriver.getVisit(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(updatedFields.getDescription(), getResponse.getBody().getDescription());
    assertEquals(updatedFields.getDate(), getResponse.getBody().getDate());

    visitDriver.deleteVisit(createdId);
    petDriver.deletePet(petId);
  }

  @Test
  void shouldDeleteVisitAndOmitItFromListing() {

    var petId = createPet();
    var visit = newVisit(petId, "dental cleaning");
    var createdId = createVisit(visit);

    var listBefore = visitDriver.listVisits();
    assertEquals(HttpStatus.OK, listBefore.getStatusCode());
    assertNotNull(listBefore.getBody());
    assertTrue(listBefore.getBody().stream().anyMatch(v -> v.getId().equals(createdId)));

    var deleteResponse = visitDriver.deleteVisit(createdId);
    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    var listAfter = visitDriver.listVisits();
    assertEquals(HttpStatus.OK, listAfter.getStatusCode());
    assertNotNull(listAfter.getBody());
    assertTrue(listAfter.getBody().stream().noneMatch(v -> v.getId().equals(createdId)));

    var getResponse = visitDriver.getVisit(createdId);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());

    petDriver.deletePet(petId);
  }

  private int createVisit(Visit visit) {

    var response = visitDriver.addVisit(visit);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    var created = response.getBody();
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals(visit.getDescription(), created.getDescription());
    assertEquals(visit.getDate(), created.getDate());
    return created.getId();
  }

  private int createPet() {

    var petFields = PetFields.builder()
            .name("VisitPet-" + UUID.randomUUID().toString().substring(0, 8))
            .birthDate(LocalDate.now().minusYears(1))
            .type(PetType.builder().id(PET_TYPE_ID).name("cat").build())
            .build();
    var response = ownerDriver.addPetToOwner(OWNER_ID, petFields);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
    return response.getBody().getId();
  }

  private Visit newVisit(int petId, String description) {

    return Visit.builder()
            .date(LocalDate.now())
            .description(description)
            .petId(petId)
            .build();
  }
}
