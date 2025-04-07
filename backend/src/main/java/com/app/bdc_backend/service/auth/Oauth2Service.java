package com.app.bdc_backend.service.auth;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.enums.OauthProvider;
import com.app.bdc_backend.model.oauth2.OAuth2UserInfoFactory;
import com.app.bdc_backend.model.oauth2.Oauth2ClientInfo;
import com.app.bdc_backend.model.oauth2.Oauth2ProviderDetail;
import com.app.bdc_backend.model.oauth2.Oauth2UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Oauth2Service {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public Oauth2UserInfo getUserInfo(OauthProvider provider,
                                      String code) {
        RestTemplate restTemplate = new RestTemplate();
        String oauth2AccessToken;

        Oauth2ClientInfo clientInfo = getClientInfo(provider);
        Oauth2ProviderDetail providerDetail = getProviderDetail(provider);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientInfo.getClientId());
        requestBody.add("client_secret", clientInfo.getClientSecret());
        requestBody.add("code", code);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("redirect_uri", clientInfo.getRedirectUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        try{
            ResponseEntity<String> response = restTemplate.postForEntity(providerDetail.getTokenUri(),
                    requestEntity, String.class);
            Map<String, Object> tokenInfo = convertJsonToMap(response.getBody());
            oauth2AccessToken = tokenInfo.get("access_token").toString();
            restTemplate.getInterceptors().add((req, body, executionContext) -> {
                req.getHeaders().add("Authorization", "Bearer " + oauth2AccessToken);
                return executionContext.execute(req, body);
            });
            Map<String, Object> attributes =
                    convertJsonToMap(restTemplate
                            .getForEntity(providerDetail.getUserInfoUri(), String.class).getBody());

            return OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        }
        catch (Exception e){
            throw new RequestException("Cannot log in with provider: " + provider);
        }
    }

    private Oauth2ClientInfo getClientInfo(OauthProvider provider){
        ClientRegistration clientRegistration =
                clientRegistrationRepository.findByRegistrationId(provider.toString());
        return Oauth2ClientInfo.builder()
                .clientId(clientRegistration.getClientId())
                .clientSecret(clientRegistration.getClientSecret())
                .redirectUri(clientRegistration.getRedirectUri())
                .scope(clientRegistration.getScopes().toString())
                .build();
    }

    private Oauth2ProviderDetail getProviderDetail(OauthProvider provider){
        ClientRegistration clientRegistration =
                clientRegistrationRepository.findByRegistrationId(provider.toString());
        return Oauth2ProviderDetail.builder()
                .tokenUri(clientRegistration.getProviderDetails().getTokenUri())
                .userInfoUri(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
                .build();
    }

    private Map<String, Object> convertJsonToMap(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<>() {});
    }

}
