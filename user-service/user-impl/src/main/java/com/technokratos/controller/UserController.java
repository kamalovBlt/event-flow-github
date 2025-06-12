package com.technokratos.controller;

import com.technokratos.api.UserApi;
import com.technokratos.dto.request.UserRequest;
import com.technokratos.dto.request.UserWithOAuthRequest;
import com.technokratos.dto.response.UserDetailsResponse;
import com.technokratos.dto.response.UserResponse;
import com.technokratos.mapper.UserMapper;
import com.technokratos.model.User;
import com.technokratos.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @PreAuthorize("hasAuthority('ADMIN') or (#id == principal.claims['user-id'] and hasAuthority('USER'))")
    public UserResponse findById(Long id) {
        return userMapper.toResponse(userService.findById(id));
    }

    @Override
    @PreAuthorize("hasAuthority('SERVICE')")
    public UserDetailsResponse findByEmail(String email) {
        return userMapper.toUserDetailsResponse(userService.findByEmail(email));
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public List<UserResponse> getRecommendations(Long userId, int page, int size) {
        /* todo method getRecommendations **/
        return List.of();
    }

    @Override
    public long save(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        return userService.save(user);
    }

    @Override
    public long saveOauthUser(UserWithOAuthRequest userWithOAuthRequest) {
        User user = userMapper.toUser(userWithOAuthRequest);
        return userService.save(user);
    }


    @Override
    @PreAuthorize("hasAuthority('USER') and #id == principal")
    public void update(Long id, UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        user.setId(id);
        userService.update(user);
    }

    @Override
    @PreAuthorize("hasAuthority('USER') and #id == principal")
    public void delete(Long id) {
        userService.delete(id);
    }

}
