package com.idanzipris.samples.petclinic.tests.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vet {

    private String firstName;
    private String lastName;
    private List<Specialty> specialties;
    private Integer id;
}
