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
public class Owner {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;
    private Integer id;
    private List<Pet> pets;
}
