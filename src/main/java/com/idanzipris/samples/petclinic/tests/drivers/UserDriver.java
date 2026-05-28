package com.idanzipris.samples.petclinic.tests.drivers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.idanzipris.samples.petclinic.tests.model.User;

@Component
public class UserDriver {

    private static final String USERS = "/api/users";

    @Autowired
    private BaseDriver baseDriver;

    public ResponseEntity<User> addUser(User user) {
        return baseDriver.post(USERS, baseDriver.serialize(user), User.class);
    }
}
