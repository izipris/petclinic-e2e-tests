package com.idanzipris.samples.petclinic.tests.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    private String name;
    private LocalDate birthDate;
    private PetType type;
    private Integer id;
    private Integer ownerId;
    private List<Visit> visits;
}
