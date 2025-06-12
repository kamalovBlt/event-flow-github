package com.technokratos.dto.response;

import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@Hidden
public record UserDetailsResponse(
        long id,
        String email,
        String password,
        List<RoleDTO> roles,
        AuthProviderDTO authProviderDTO
) {
}
