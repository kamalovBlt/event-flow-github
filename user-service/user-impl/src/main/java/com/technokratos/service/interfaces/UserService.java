package com.technokratos.service.interfaces;

import com.technokratos.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    User findById(Long id);
    List<User> findAll();
    Long save(User user);
    void update(User user);
    void delete(Long id);
    User findByEmail(String email);
    boolean isOwner(Long userId, Authentication authentication);
}
