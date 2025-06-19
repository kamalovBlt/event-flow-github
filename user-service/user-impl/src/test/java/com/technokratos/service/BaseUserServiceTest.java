package com.technokratos.service;

import com.technokratos.exception.EmailAlreadyExistException;
import com.technokratos.exception.UserNotFoundException;
import com.technokratos.model.AuthProvider;
import com.technokratos.model.User;
import com.technokratos.repository.UserRepository;
import com.technokratos.service.impl.BaseUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BaseUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BaseUserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("plainPassword")
                .authProvider(AuthProvider.LOCAL)
                .build();
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isEqualTo(user);
        verify(userRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с таким ID");

        verify(userRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    void shouldThrowExceptionWhenSavingUserWithExistingEmail() {
        User user = User.builder().id(10L).email("email@email.ru").build();
        when(userRepository.existsByEmailAndDeletedFalse(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");

        verify(userRepository).existsByEmailAndDeletedFalse(user.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUserWithExistingEmail() {
        User user = User.builder().id(10L).email("email@email.ru").build();
        when(userRepository.existsByEmailAndDeletedFalse(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.update(user))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");

        verify(userRepository).existsByEmailAndDeletedFalse(user.getEmail());
        verify(userRepository, never()).save(any());
    }


    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(user);
        when(userRepository.findAllByDeletedFalse()).thenReturn(users);

        List<User> result = userService.findAll();

        assertThat(result).containsExactly(user);
    }

    @Test
    void shouldSaveUserWithEncodedPassword() {
        String encoded = "encodedPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(encoded);

        User savedUser = User.builder().id(2L).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Long id = userService.save(user);

        assertThat(id).isEqualTo(2L);
        assertThat(user.getPassword()).isEqualTo(encoded);
    }

    @Test
    void shouldSaveUserWithGoogleWithoutEncodingPassword() {
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setPassword("raw");

        User savedUser = User.builder().id(10L).build();
        when(userRepository.save(user)).thenReturn(savedUser);

        Long id = userService.save(user);

        assertThat(id).isEqualTo(10L);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldUpdateUserWhenExists() {
        when(userRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);

        userService.update(user);

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentUser() {
        when(userRepository.existsByIdAndDeletedFalse(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.update(user))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldSoftDeleteUserIfExists() {
        when(userRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).softDeleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentUser() {
        when(userRepository.existsByIdAndDeletedFalse(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldFindByEmail() {
        when(userRepository.findByEmailAndDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@example.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        when(userRepository.findByEmailAndDeletedFalse("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("notfound@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldReturnTrueIfUserIsOwner() {
        Long userId = 42L;

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("user-id")).thenReturn(userId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        boolean isOwner = userService.isOwner(userId, authentication);

        assertThat(isOwner).isTrue();
    }

    @Test
    void shouldReturnFalseIfUserIsNotOwner() {
        Long actualUserId = 42L;
        Long differentUserId = 99L;

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("user-id")).thenReturn(actualUserId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        boolean isOwner = userService.isOwner(differentUserId, authentication);

        assertThat(isOwner).isFalse();
    }
}
