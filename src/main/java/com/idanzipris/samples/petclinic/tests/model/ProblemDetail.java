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
public class ProblemDetail {

    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String timestamp;
    private List<ValidationMessage> schemaValidationErrors;
}
