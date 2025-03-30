package com.app.bdc_backend.service;

import com.app.bdc_backend.exception.ServerException;
import com.app.bdc_backend.model.dto.response.OauthUserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class Oauth2Service {

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.token-endpoint}")
    private String googleTokenEndpoint;

    @Value("${google.userinfo-endpoint}")
    private String googleUserInfoEndpoint;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

    public OauthUserDTO getOauth2Profile(String code, String provider) throws ServerException {
        RestTemplate restTemplate = new RestTemplate();
        String oauth2AccessToken;
        switch (provider){
            case "google":
                MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
                requestBody.add("client_id", googleClientId);
                requestBody.add("client_secret", googleClientSecret);
                requestBody.add("code", code);
                requestBody.add("grant_type", "authorization_code");
                requestBody.add("redirect_uri", googleRedirectUri);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
                try{
                    ResponseEntity<String> response = restTemplate.postForEntity(googleTokenEndpoint, requestEntity, String.class);
                    Map<String, Object> tokenInfo = convertJsonToMap(response.getBody());
                    oauth2AccessToken = tokenInfo.get("access_token").toString();
                    restTemplate.getInterceptors().add((req, body, executionContext) -> {
                        req.getHeaders().add("Authorization", "Bearer " + oauth2AccessToken);
                        return executionContext.execute(req, body);
                    });
                    Map<String, Object> res = convertJsonToMap(restTemplate.getForEntity(googleUserInfoEndpoint, String.class).getBody());
                    OauthUserDTO userDTO = new OauthUserDTO();
                    userDTO.setEmail((String)res.get("email"));
                    userDTO.setFullName((String)res.get("name"));
                    userDTO.setUsername(userDTO.getEmail().split("@")[0]);
                    userDTO.setAvatarUrl((String) res.get("picture"));
                    return userDTO;
                }
                catch (Exception e){
                    throw new ServerException("Server Error");
                }
        }
        return null;
    }

    private Map<String, Object> convertJsonToMap(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<>() {});
    }

}
