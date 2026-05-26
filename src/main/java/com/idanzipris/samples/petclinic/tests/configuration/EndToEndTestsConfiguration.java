package com.idanzipris.samples.petclinic.tests.configuration;

import com.idanzipris.samples.petclinic.tests.drivers.BaseDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class EndToEndTestsConfiguration {

  @Bean
  public BaseDriver baseDriver(@Value("${petclinic.base-url}") String baseUrl, ObjectMapper objectMapper) {

    var restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    return new BaseDriver(restClient, objectMapper);
  }
}
