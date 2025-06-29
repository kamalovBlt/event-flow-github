package com.technokratos.service.impl;

import com.technokratos.exception.EmailAlreadyExistException;
import com.technokratos.exception.UserNotFoundException;
import com.technokratos.model.AuthProvider;
import com.technokratos.model.User;
import com.technokratos.repository.UserRepository;
import com.technokratos.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(
                        () -> new UserNotFoundException("Пользователь с таким ID %s не найден".formatted(id),id)
                );
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByDeletedFalse();
    }

    @Override
    public Long save(User user) {
        if (userRepository.existsByEmailAndDeletedFalse(user.getEmail())) {
            throw new EmailAlreadyExistException("Пользователь с таким email уже существует");
        }
        if (!user.getAuthProvider().equals(AuthProvider.GOOGLE)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user).getId();
    }

    @Override
    public void update(User user) {
        if (userRepository.existsByEmailAndDeletedFalse(user.getEmail())) {
            throw new EmailAlreadyExistException("Пользователь с таким email уже существует, нельзя обновить", user.getId());
        }
        if (!userRepository.existsByIdAndDeletedFalse(user.getId())) {
            throw new UserNotFoundException("Пользователь с таким %s ID не найден".formatted(user.getId()),user.getId());
        }
        userRepository.save(user);
    }


    @Override
    public void delete(Long id) {
        if (!userRepository.existsByIdAndDeletedFalse(id)) {
            throw new UserNotFoundException("Пользователь с таким %s ID не найден".formatted(id), id);
        }
        userRepository.softDeleteById(id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(
                        () -> new UserNotFoundException("Пользователь с такой почтой не найден")
                );
    }

    @Override
    public boolean isOwner(Long userId, Authentication authentication) {
        Jwt principal = (Jwt) authentication.getPrincipal();
        Long id = principal.getClaim("user-id");
        return id.equals(userId);
    }

}
