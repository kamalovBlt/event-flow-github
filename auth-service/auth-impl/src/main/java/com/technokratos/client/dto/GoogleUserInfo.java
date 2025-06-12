package com.technokratos.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfo(
        @JsonProperty("given_name")
        String name,
        @JsonProperty("family_name")
        String surname,
        String email
) {
}