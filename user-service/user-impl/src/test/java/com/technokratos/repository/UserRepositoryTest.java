package com.technokratos.repository;

import com.technokratos.config.SecurityTestConfig;
import com.technokratos.model.AuthProvider;
import com.technokratos.model.Role;
import com.technokratos.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(classes = SecurityTestConfig.class)
@ActiveProfiles(profiles = "test")
class UserRepositoryTest{

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void shouldFindByEmailBecauseDeletedFalse() {
        User user = User.builder()
                .password("pass123")
                .email("another@mail.com")
                .firstName("Petr")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(false)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndDeletedFalse("another@mail.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("another@mail.com");
    }
    @Test
    void shouldNotFindByEmailBecauseDeleteTrue() {
        User user = User.builder()
                .password("pass123")
                .email("another@mail.com")
                .firstName("Petr")
                .deleted(true)
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndDeletedFalse("another@mail.com");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllByDeletedFalse() {
        User active = User.builder()
                .password("pass")
                .email("active@mail.com")
                .firstName("Active")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(false)
                .build();
        User deleted = User.builder()
                .password("pass")
                .email("deleted@mail.com")
                .firstName("Deleted")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(true)
                .build();
        userRepository.save(active);
        userRepository.save(deleted);

        List<User> users = userRepository.findAllByDeletedFalse();
        assertThat(users)
                .hasSize(1)
                .extracting(User::getEmail)
                .containsExactly("active@mail.com");
    }
    @Test
    void shouldSaveAndFindByIdWhereDeletedFalse() {
        User active = User.builder()
                .password("pass")
                .email("active@mail.com")
                .firstName("Active")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(false)
                .build();
        User user1 = userRepository.save(active);

        Optional<User> found = userRepository.findByIdAndDeletedFalse(user1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Active");
    }
    @Test
    void shouldSaveAndNotFindByIdWhereDeletedTrue() {
        User deleted = User.builder()
                .password("pass")
                .email("deleted@mail.com")
                .firstName("Active")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(true)
                .build();
        User user1 = userRepository.save(deleted);

        Optional<User> found = userRepository.findByIdAndDeletedFalse(user1.getId());

        assertThat(found).isEmpty();
    }

    @Test
    void shouldNotFindNonExistingUserById() {
        Optional<User> found = userRepository.findByIdAndDeletedFalse(999999L);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldSoftDeleteUserById() {
        User user = User.builder()
                .password("123")
                .email("softdelete@example.com")
                .firstName("Soft")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(false)
                .build();

        user = userRepository.save(user);

        userRepository.softDeleteById(user.getId());

        Optional<User> optionalUser = userRepository.findById(user.getId());

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getDeleted()).isTrue();
    }

    @Test
    void shouldReturnTrueIfUserExistsAndNotDeleted() {
        User user = User.builder()
                .password("123")
                .email("existDeletedFalse@example.com")
                .firstName("Soft")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(false)
                .build();

        user = userRepository.save(user);

        Boolean exists = userRepository.existsByIdAndDeletedFalse(user.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfUserIsDeleted() {
        User user = User.builder()
                .password("123")
                .email("existDeletedTrue@example.com")
                .firstName("Soft")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(true)
                .build();

        user = userRepository.save(user);

        Boolean exists = userRepository.existsByIdAndDeletedFalse(user.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnFalseIfUserDoesNotExist() {
        Boolean exists = userRepository.existsByIdAndDeletedFalse(999999L);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueIfEmailExistsAndNotDeleted() {
        User user = User.builder()
                .password("pass123")
                .email("another@mail.com")
                .firstName("Petr")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(false)
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmailAndDeletedFalse("another@mail.com");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfEmailExistsButDeleted() {
        User user = User.builder()
                .password("pass123")
                .email("another@mail.com")
                .firstName("Petr")
                .roles(List.of(Role.USER))
                .authProvider(AuthProvider.GOOGLE)
                .deleted(true)
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmailAndDeletedFalse("another@mail.com");
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmailAndDeletedFalse("nonexistent@test.com");
        assertThat(exists).isFalse();
    }
}