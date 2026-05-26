package com.idanzipris.samples.petclinic.tests.drivers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RootDriver {

    private static final String ROOT = "/";

    @Autowired
    private BaseDriver baseDriver;

    /**
     * Hits the swagger redirect served by RootRestController. Returns the
     * raw response (typically a 302 redirect) for assertions.
     */
    public ResponseEntity<Void> redirectToSwagger() {
        return baseDriver.get(ROOT, Void.class);
    }
}
