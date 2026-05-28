package com.idanzipris.samples.petclinic.tests;

import com.idanzipris.samples.petclinic.tests.drivers.UserDriver;
import com.idanzipris.samples.petclinic.tests.model.Role;
import com.idanzipris.samples.petclinic.tests.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserTest {

  @Autowired
  private UserDriver userDriver;

  @Test
  void shouldCreateUserWithOwnerAdminRole() {

    var user = newUser("OWNER_ADMIN");

    var response = userDriver.addUser(user);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(user.getUsername(), response.getBody().getUsername());
    assertEquals(user.getEnabled(), response.getBody().getEnabled());
    assertNotNull(response.getBody().getRoles());
    assertTrue(response.getBody().getRoles().stream()
            .anyMatch(role -> "ROLE_OWNER_ADMIN".equals(role.getName())));
  }

  @Test
  void shouldCreateUserWithVetAdminRole() {

    var user = newUser("VET_ADMIN");

    var response = userDriver.addUser(user);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(user.getUsername(), response.getBody().getUsername());
    assertNotNull(response.getBody().getRoles());
    assertTrue(response.getBody().getRoles().stream()
            .anyMatch(role -> "ROLE_VET_ADMIN".equals(role.getName())));
  }

  private User newUser(String roleName) {

    return User.builder()
            .username("user-" + UUID.randomUUID().toString().substring(0, 8))
            .password("p@ssw0rd")
            .enabled(true)
            .roles(List.of(Role.builder().name(roleName).build()))
            .build();
  }
}
