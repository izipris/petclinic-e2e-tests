package com.idanzipris.samples.petclinic.tests;

import com.idanzipris.samples.petclinic.tests.drivers.SpecialtyDriver;
import com.idanzipris.samples.petclinic.tests.drivers.VetDriver;
import com.idanzipris.samples.petclinic.tests.model.Specialty;
import com.idanzipris.samples.petclinic.tests.model.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class VetTest {

  @Autowired
  private VetDriver vetDriver;

  @Autowired
  private SpecialtyDriver specialtyDriver;

  @Test
  void shouldCreateVetAndRetrieveItById() {

    var vet = newVet("Watson");
    var createdId = createVet(vet);

    var getResponse = vetDriver.getVet(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(createdId, getResponse.getBody().getId());
    assertEquals(vet.getFirstName(), getResponse.getBody().getFirstName());
    assertEquals(vet.getLastName(), getResponse.getBody().getLastName());

    vetDriver.deleteVet(createdId);
  }

  @Test
  void shouldCreateVetWithSpecialtyAndRetrieveIt() {

    var specialty = Specialty.builder()
            .name("cardiology-" + UUID.randomUUID().toString().substring(0, 8))
            .build();
    var addSpecialtyResponse = specialtyDriver.addSpecialty(specialty);
    assertEquals(HttpStatus.CREATED, addSpecialtyResponse.getStatusCode());
    assertNotNull(addSpecialtyResponse.getBody());
    var specialtyId = addSpecialtyResponse.getBody().getId();

    var vet = Vet.builder()
            .firstName("Helen")
            .lastName("Doyle-" + randomLetters())
            .specialties(List.of(addSpecialtyResponse.getBody()))
            .build();
    var createdId = createVet(vet);

    var getResponse = vetDriver.getVet(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertNotNull(getResponse.getBody().getSpecialties());
    assertTrue(getResponse.getBody().getSpecialties().stream()
            .anyMatch(s -> s.getId().equals(specialtyId)));

    vetDriver.deleteVet(createdId);
    specialtyDriver.deleteSpecialty(specialtyId);
  }

  @Test
  void shouldUpdateVetDetails() {

    var vet = newVet("Garcia");
    var createdId = createVet(vet);

    var updated = Vet.builder()
            .id(createdId)
            .firstName("Maria")
            .lastName(vet.getLastName())
            .specialties(List.of())
            .build();

    var updateResponse = vetDriver.updateVet(createdId, updated);
    assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

    var getResponse = vetDriver.getVet(createdId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals(updated.getFirstName(), getResponse.getBody().getFirstName());

    vetDriver.deleteVet(createdId);
  }

  @Test
  void shouldDeleteVetAndOmitItFromListing() {

    var vet = newVet("Wright");
    var createdId = createVet(vet);

    var listBefore = vetDriver.listVets();
    assertEquals(HttpStatus.OK, listBefore.getStatusCode());
    assertNotNull(listBefore.getBody());
    assertTrue(listBefore.getBody().stream().anyMatch(v -> v.getId().equals(createdId)));

    var deleteResponse = vetDriver.deleteVet(createdId);
    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    var listAfter = vetDriver.listVets();
    assertEquals(HttpStatus.OK, listAfter.getStatusCode());
    assertNotNull(listAfter.getBody());
    assertTrue(listAfter.getBody().stream().noneMatch(v -> v.getId().equals(createdId)));

    var getResponse = vetDriver.getVet(createdId);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  private int createVet(Vet vet) {

    var response = vetDriver.addVet(vet);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    var created = response.getBody();
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals(vet.getFirstName(), created.getFirstName());
    assertEquals(vet.getLastName(), created.getLastName());
    return created.getId();
  }

  private Vet newVet(String lastNamePrefix) {

    return Vet.builder()
            .firstName("Tester")
            .lastName(lastNamePrefix + "-" + randomLetters())
            .specialties(List.of())
            .build();
  }

  private static String randomLetters() {

    var random = ThreadLocalRandom.current();
    var sb = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
      sb.append((char) ('a' + random.nextInt(26)));
    }
    return sb.toString();
  }
}
