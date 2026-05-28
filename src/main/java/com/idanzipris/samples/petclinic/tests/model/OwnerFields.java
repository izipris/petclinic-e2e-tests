package com.idanzipris.samples.petclinic.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerFields {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;
}
