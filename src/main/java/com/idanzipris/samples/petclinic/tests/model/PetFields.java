package com.idanzipris.samples.petclinic.tests.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetFields {

    private String name;
    private LocalDate birthDate;
    private PetType type;
}
