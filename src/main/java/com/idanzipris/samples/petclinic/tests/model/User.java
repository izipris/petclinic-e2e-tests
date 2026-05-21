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
public class User {

    private String username;
    private String password;
    private Boolean enabled;
    private List<Role> roles;
}
