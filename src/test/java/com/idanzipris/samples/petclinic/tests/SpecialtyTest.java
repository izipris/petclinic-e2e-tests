package com.idanzipris.samples.petclinic.tests;

import com.idanzipris.samples.petclinic.tests.drivers.SpecialtyDriver;
import com.idanzipris.samples.petclinic.tests.model.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SpecialtyTest {

  @Autowired
  private SpecialtyDriver specialtyDriver;

  @Test
  void shouldCreateSpecialtyAndRetrieveItById() {

    var specialty = newSpecialty("cardiology");
    var createdId = createSpecialty(specialty);

    var getResponse = specialtyDriver.getSpecialty(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(createdId, getResponse.getBody().getId());
    assertEquals(specialty.getName(), getResponse.getBody().getName());

    specialtyDriver.deleteSpecialty(createdId);
  }

  @Test
  void shouldUpdateSpecialtyName() {

    var specialty = newSpecialty("oncology");
    var createdId = createSpecialty(specialty);

    var updated = Specialty.builder()
            .id(createdId)
            .name("neurology-" + UUID.randomUUID().toString().substring(0, 8))
            .build();

    var updateResponse = specialtyDriver.updateSpecialty(createdId, updated);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getResponse = specialtyDriver.getSpecialty(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(updated.getName(), getResponse.getBody().getName());

    specialtyDriver.deleteSpecialty(createdId);
  }

  @Test
  void shouldDeleteSpecialtyAndOmitItFromListing() {

    var specialty = newSpecialty("dermatology");
    var createdId = createSpecialty(specialty);

    var listBefore = specialtyDriver.listSpecialties();
    assertEquals(HttpStatus.OK, listBefore.getStatusCode());
    assertNotNull(listBefore.getBody());
    assertTrue(listBefore.getBody().stream().anyMatch(s -> s.getId().equals(createdId)));

    var deleteResponse = specialtyDriver.deleteSpecialty(createdId);
    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    var listAfter = specialtyDriver.listSpecialties();
    assertEquals(HttpStatus.OK, listAfter.getStatusCode());
    assertNotNull(listAfter.getBody());
    assertTrue(listAfter.getBody().stream().noneMatch(s -> s.getId().equals(createdId)));

    var getResponse = specialtyDriver.getSpecialty(createdId);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  private int createSpecialty(Specialty specialty) {

    var response = specialtyDriver.addSpecialty(specialty);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    var created = response.getBody();
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals(specialty.getName(), created.getName());
    return created.getId();
  }

  private Specialty newSpecialty(String namePrefix) {

    return Specialty.builder()
            .name(namePrefix + "-" + UUID.randomUUID().toString().substring(0, 8))
            .build();
  }
}
