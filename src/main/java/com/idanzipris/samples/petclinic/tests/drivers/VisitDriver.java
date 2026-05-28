package com.idanzipris.samples.petclinic.tests.drivers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.idanzipris.samples.petclinic.tests.model.Visit;
import com.idanzipris.samples.petclinic.tests.model.VisitFields;

@Component
public class VisitDriver {

    private static final String VISITS = "/api/visits";

    @Autowired
    private BaseDriver baseDriver;

    public ResponseEntity<List<Visit>> listVisits() {
        return baseDriver.get(VISITS, new ParameterizedTypeReference<>() {});
    }

    public ResponseEntity<Visit> getVisit(int visitId) {
        return baseDriver.get(VISITS + "/" + visitId, Visit.class);
    }

    public ResponseEntity<Visit> addVisit(Visit visit) {
        return baseDriver.post(VISITS, baseDriver.serialize(visit), Visit.class);
    }

    public ResponseEntity<Visit> updateVisit(int visitId, VisitFields visit) {
        return baseDriver.put(VISITS + "/" + visitId, baseDriver.serialize(visit), Visit.class);
    }

    public ResponseEntity<Void> deleteVisit(int visitId) {
        return baseDriver.delete(VISITS + "/" + visitId);
    }
}
