package com.idanzipris.samples.petclinic.tests.drivers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BaseDriver {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;

  public <T> ResponseEntity<T> get(String path, Class<T> responseType) {

    return restClient.get()
            .uri(path)
            .exchange((request, response) -> toEntity(response, responseType));
  }

  public <T> ResponseEntity<T> get(String path, ParameterizedTypeReference<T> responseType) {

    return restClient.get()
            .uri(path)
            .exchange((request, response) -> toEntity(response, responseType));
  }

  public <T> ResponseEntity<T> post(String path, String body, Class<T> responseType) {

    return restClient.post()
            .uri(path)
            .body(body)
            .exchange((request, response) -> toEntity(response, responseType));
  }

  public <T> ResponseEntity<T> put(String path, String body, Class<T> responseType) {

    return restClient.put()
            .uri(path)
            .body(body)
            .exchange((request, response) -> toEntity(response, responseType));
  }

  public ResponseEntity<Void> put(String path, String body) {

    return restClient.put()
            .uri(path)
            .body(body)
            .exchange((request, response) -> toBodilessEntity(response));
  }

  public ResponseEntity<Void> delete(String path) {

    return restClient.delete()
            .uri(path)
            .exchange((request, response) -> toBodilessEntity(response));
  }

  String serialize(Object value) {

    try {
      return objectMapper.writeValueAsString(value);
    } catch (JacksonException e) {
      throw new IllegalStateException("Failed to serialize value to JSON", e);
    }
  }

  String generateQueryParameters(Map<String, String> params) {

    if (params.isEmpty()) {
      return "";
    }
    return params.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&", "?", ""));
  }

  private static <T> ResponseEntity<T> toEntity(ConvertibleClientHttpResponse response, Class<T> responseType) throws IOException {

    HttpStatusCode status = response.getStatusCode();
    HttpHeaders headers = response.getHeaders();
    T body = status.isError() ? null : response.bodyTo(responseType);
    return ResponseEntity.status(status).headers(headers).body(body);
  }

  private static <T> ResponseEntity<T> toEntity(ConvertibleClientHttpResponse response, ParameterizedTypeReference<T> responseType)
          throws IOException {

    HttpStatusCode status = response.getStatusCode();
    HttpHeaders headers = response.getHeaders();
    T body = status.isError() ? null : response.bodyTo(responseType);
    return ResponseEntity.status(status).headers(headers).body(body);
  }

  private static ResponseEntity<Void> toBodilessEntity(ConvertibleClientHttpResponse response) throws IOException {

    HttpStatusCode status = response.getStatusCode();
    HttpHeaders headers = response.getHeaders();
    return ResponseEntity.status(status).headers(headers).build();
  }
}
