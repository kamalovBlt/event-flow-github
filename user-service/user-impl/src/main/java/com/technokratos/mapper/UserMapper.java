package com.technokratos.mapper;

import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import com.technokratos.dto.request.UserRequest;
import com.technokratos.dto.request.UserWithOAuthRequest;
import com.technokratos.dto.response.UserDetailsResponse;
import com.technokratos.dto.response.UserResponse;
import com.technokratos.model.AuthProvider;
import com.technokratos.model.Role;
import com.technokratos.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper{

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    User toUser(UserRequest userRequest);

    @Mapping(source = "authProvider", target = "authProviderDTO")
    UserDetailsResponse toUserDetailsResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUser(UserWithOAuthRequest userWithOAuthRequest);

    AuthProviderDTO toAuthProviderDTO(AuthProvider authProvider);
    AuthProvider toAuthProvider(AuthProviderDTO authProviderDTO);

    RoleDTO toRoleDTO(Role role);
    Role toTole(RoleDTO roleDTO);

}
